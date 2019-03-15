package pl.pamsoft.imapcloud.services;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.requests.AccountCapacityTestRequest;
import pl.pamsoft.imapcloud.requests.CreateAccountRequest;
import pl.pamsoft.imapcloud.services.containers.UploadChunkContainer;
import pl.pamsoft.imapcloud.services.upload.UploadUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountServices extends AbstractBackgroundService {

	private static final Logger LOG = LoggerFactory.getLogger(AccountServices.class);
	//CSOFF: MagicNumber
	private static final float TWENTY_PERCENT = 20 / 100.0f;
	//CSON: MagicNumber
	private AccountRepository accountRepository;
	private FileChunkRepository fileChunkRepository;
	private CryptoService cryptoService;
	private UploadService uploadService;

	private Function<? super Account, AccountDto> toAccount = a -> {
		List<FileChunk> chunks = fileChunkRepository.getFileChunksByAccountId(a.getId());
		long sum = chunks.stream().mapToLong(FileChunk::getOrgSize).sum();
		return new AccountDto(a.getId(), String.format("%s@%s", a.getLogin(), a.getHost()), a.getCryptoKey(), sum);
	};

	public void addAccount(CreateAccountRequest request) {
		Account account = new Account();

		account.setLogin(request.getUsername());
		account.setPassword(request.getPassword());
		account.setType(request.getSelectedAccountProvider().getType());
		account.setHost(request.getSelectedAccountProvider().getHost());
		account.setAccountSizeMB(request.getSelectedAccountProvider().getAccountSizeMB());
		account.setAttachmentSizeMB(request.getSelectedAccountProvider().getMaxFileSizeMB());
		account.setMaxConcurrentConnections(request.getSelectedAccountProvider().getMaxConcurrentConnections());
		account.setCryptoKey(getCryptoKey(request));
		account.setAdditionalProperties(request.getSelectedAccountProvider().getAdditionalProperties());

		accountRepository.save(account);

	}

	private enum CapacityStatus {
		TO_HIGH, TO_LOW
	};

	public boolean testAccountCapacity(AccountCapacityTestRequest request) {
		final String taskId = UUID.randomUUID().toString();
		Future<Void> future = runAsyncOnExecutor(() -> {
			String fileId = "00000000-0000-0000-0000-0000000000000";
			Thread.currentThread().setName("ACTT-" + taskId.substring(0, NB_OF_TASK_ID_CHARS));
			AccountDto selectedAccount = request.getSelectedAccount();
			int declaredMaxChunkSize = UploadUtils.toBytes(accountRepository.getById(selectedAccount.getId()).getAttachmentSizeMB());

			LOG.debug("Trying chunk size: {}", declaredMaxChunkSize);
			boolean isSuccessfullyUploaded = uploadService.uploadTestChunk(selectedAccount, createUCC(fileId, 1, declaredMaxChunkSize, false, getData(declaredMaxChunkSize)));
			int[] range = new int[] {declaredMaxChunkSize, declaredMaxChunkSize};
			CapacityStatus lastStatus = isSuccessfullyUploaded ? CapacityStatus.TO_LOW : CapacityStatus.TO_HIGH;
			range = determineRange(fileId, selectedAccount, 2, range, lastStatus);
			int foundedCapacity = findCapacity(fileId, selectedAccount, 2, range, lastStatus);
			Account byId = accountRepository.getById(selectedAccount.getId());
			byId.setVerifiedAttachmentSizeBytes(foundedCapacity);
			accountRepository.save(byId);
		});
		getTaskMap().put(taskId, future);
		return true;
	}

	private int[] determineRange(String uuid, AccountDto account, int chunkNumber, int[] range, CapacityStatus lastStatus) {
		LOG.debug("Chunk {} with size: {} was {}", chunkNumber, CapacityStatus.TO_LOW == lastStatus ? range[0] : range[1], lastStatus);
		int newChunkSize = calculateNewChunkSize(CapacityStatus.TO_LOW == lastStatus ? range[0] : range[1], lastStatus);
		LOG.debug("Trying new chunk size: {}", newChunkSize);
		boolean isSuccessfullyUploaded = uploadService.uploadTestChunk(account, createUCC(uuid, chunkNumber, newChunkSize, false, getData(newChunkSize)));

		switch (lastStatus) {
			case TO_LOW:
				if (isSuccessfullyUploaded) {
					range[0] = newChunkSize;
					return determineRange(uuid, account, ++chunkNumber, range, lastStatus);
				} else {
					range[1] = newChunkSize;
					Arrays.sort(range);
					return range;
				}
			case TO_HIGH:
				if (isSuccessfullyUploaded) {
					range[0] = newChunkSize;
					Arrays.sort(range);
					return range;
				} else {
					range[1] = newChunkSize;
					return determineRange(uuid, account, ++chunkNumber, range, lastStatus);
				}
			default:
				return range;// infinite loop
		}
	}

	private int findCapacity(String uuid, AccountDto account, int chunkNumber, int[] range, CapacityStatus lastStatus) {
		LOG.debug("Looking for valid size between {} and {}, range: {}", range[0], range[1], range[1] - range[0]);
		int chunkSize = findMiddle(range);
		boolean isSuccessfullyUploaded = uploadService.uploadTestChunk(account, createUCC(uuid, chunkNumber++, chunkSize, false, getData(chunkSize)));
		LOG.debug("New size: {} is OK?: {}", chunkSize, isSuccessfullyUploaded);
		if (range[1]-range[0] <= 1 && isSuccessfullyUploaded) {
			int founded = range[0] == chunkSize ? range[0] : range[1];
			LOG.debug("Founded max size: {}", founded);
			return founded;
		}
		switch (lastStatus) {
			case TO_LOW:
				range[isSuccessfullyUploaded ? 0 : 1] = chunkSize;
				return findCapacity(uuid, account, chunkNumber, range, lastStatus);
			case TO_HIGH:
				range[isSuccessfullyUploaded ? 1 : 0] = chunkSize;
				return findCapacity(uuid, account, chunkNumber, range, lastStatus);
			default:
				return 0;
		}
	}

	private int findMiddle(int[] range) {
		return range[0] + (int) ((range[1] - range[0]) / 2.0);
	}

	private int calculateNewChunkSize(int lastChunkSize, CapacityStatus lastStatus){
		int change = (int)(lastChunkSize * TWENTY_PERCENT);
		switch (lastStatus) {
			case TO_LOW:
				return lastChunkSize + change;
			case TO_HIGH:
				return lastChunkSize - change;
			default: // infinite loop :P
				return lastChunkSize;
		}
	}

	private byte[] getData(int size) {
		byte[] data = new byte[size];
		Arrays.fill(data, (byte) 0);
		return data;
	}

	private UploadChunkContainer createUCC(String uuid, int chunkNumber, long chunkSize, boolean isLastChunk, byte[] data) {
		FileDto fileDto = new FileDto("dummyFile", "noPath", FileDto.FileType.FILE, chunkSize);
		UploadChunkContainer ucc = new UploadChunkContainer(uuid, fileDto);
		ucc = UploadChunkContainer.addFileHash(ucc, "0000000000000000");
		ucc = UploadChunkContainer.addIds(ucc, -1L, uuid);
		ucc = UploadChunkContainer.addChunk(ucc, chunkSize, chunkSize, data, chunkNumber, isLastChunk);
		ucc = UploadChunkContainer.addChunkHash(ucc, "0000000000000000");
		ucc = UploadChunkContainer.addEncryptedData(ucc, data);
		return ucc;
	}

	@VisibleForTesting
	protected String getCryptoKey(CreateAccountRequest request) {
		try {
			if (Strings.isNullOrEmpty(request.getCryptoKey())) {
				return ByteUtils.toHexString(cryptoService.generateKey());
			} else {
				try {
					return ByteUtils.toHexString(cryptoService.calcSha256(request.getCryptoKey()));
				} catch (UnsupportedEncodingException e) {
					LOG.error("Can't hash passphrase");
					return ByteUtils.toHexString(cryptoService.generateKey());
				}
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			LOG.error("Can't create encryption key.", e);
		}
		return ByteUtils.toHexString(cryptoService.generateWeakKey());
	}

	public List<AccountDto> listAccounts() {
		Collection<Account> all = accountRepository.findAll();
		return all.stream()
			.map(toAccount)
			.collect(Collectors.toList());
	}

	public Optional<AccountDto> getByResticName(String resticName) {
		Account byResticName = accountRepository.getByResticName(resticName);
		return null == byResticName ? Optional.empty() : Optional.of(toAccount.apply(byResticName));
	}

	@Autowired
	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setFileChunkRepository(FileChunkRepository fileChunkRepository) {
		this.fileChunkRepository = fileChunkRepository;
	}

	@Autowired
	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}

	@Autowired
	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	protected int getMaxTasks() {
		return DEFAULT_MAX_TASKS;
	}

	@Override
	protected String getNameFormat() {
		return "AccountCapacityTestTask-%d";
	}
}
