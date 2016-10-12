package pl.pamsoft.imapcloud.services.recovery;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileChunkRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;
import pl.pamsoft.imapcloud.entity.FileChunk;
import pl.pamsoft.imapcloud.exceptions.ChunkAlreadyExistException;
import pl.pamsoft.imapcloud.services.CryptoService;
import pl.pamsoft.imapcloud.services.RecoveryChunkContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.fromHexString;

public class FileRecovery implements Function<RecoveryChunkContainer, RecoveryChunkContainer> {

	private final Set<String> uniqueFileIds;
	private FileRepository fileRepository;
	private final FileChunkRepository fileChunkRepository;
	private final AccountRepository accountRepository;
	private CryptoService cryptoService;

	public FileRecovery(Set<String> uniqueFileIds, FileRepository fileRepository, FileChunkRepository fileChunkRepository,
	                    AccountRepository accountRepository, CryptoService cryptoService) {
		this.uniqueFileIds = uniqueFileIds;
		this.fileRepository = fileRepository;
		this.fileChunkRepository = fileChunkRepository;
		this.accountRepository = accountRepository;
		this.cryptoService = cryptoService;
	}

	@Override
	public RecoveryChunkContainer apply(RecoveryChunkContainer rcc) {
		uniqueFileIds.forEach(id -> {
			try {
				recoverFile(id, rcc);
			} catch (InvalidCipherTextException | IOException e) {
				e.printStackTrace();
			}
		});
		return rcc;
	}

	@SuppressWarnings("PMD.EmptyCatchBlock")
	private void recoverFile(String id, RecoveryChunkContainer rcc) throws IOException, InvalidCipherTextException {
		PaddedBufferedBlockCipher key = cryptoService.getDecryptingCipher(fromHexString(rcc.getAccount().getCryptoKey()));
		File file = decryptFile(key, rcc.getFileMap().get(id));
		List<FileChunk> fileChunks = rcc.getFileChunkMap().get(id);
		Account savedAccount = accountRepository.getById(rcc.getAccount().getId());
		file.setOwnerAccount(savedAccount);
		File savedFile;
		try {
			savedFile = fileRepository.save(file);
		} catch (FileAlreadyExistsException e) {
			savedFile = fileRepository.getByFileUniqueId(file.getFileUniqueId());
		}
		for (FileChunk fileChunk : fileChunks) {
			fileChunk.setOwnerFile(savedFile);
			try {
				fileChunkRepository.save(fileChunk);
			} catch (ChunkAlreadyExistException ignore) {
				// already saved, lets recover next one
			}
		}
	}

	private File decryptFile(PaddedBufferedBlockCipher key, File file) throws IOException, InvalidCipherTextException {
		File f = new File();
		f.setAbsolutePath(new String(cryptoService.decryptHex(key, file.getAbsolutePath()),StandardCharsets.UTF_8));
		f.setName(new String(cryptoService.decryptHex(key, file.getName()),StandardCharsets.UTF_8));
		f.setSize(file.getSize());
		f.setFileUniqueId(file.getFileUniqueId());
		f.setFileHash(file.getFileHash());
		f.setCompleted(file.isCompleted());
		return f;
	}
}
