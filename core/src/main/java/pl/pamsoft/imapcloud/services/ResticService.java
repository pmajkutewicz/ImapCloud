package pl.pamsoft.imapcloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pamsoft.imapcloud.dao.AccountRepository;
import pl.pamsoft.imapcloud.dao.FileRepository;
import pl.pamsoft.imapcloud.dao.ResticMappingsRepository;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.ResticMapping;
import pl.pamsoft.imapcloud.restic.ResticType;
import pl.pamsoft.imapcloud.restic.ResticUtils;
import pl.pamsoft.imapcloud.restic.dto.V1Result;
import pl.pamsoft.imapcloud.restic.dto.V2Result;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResticService {

	private AccountRepository accountRepository;
	private ResticMappingsRepository mappingsRepository;
	private UploadService uploadService;
	private DownloadService downloadService;
	private DeletionService deletionService;
	private FileRepository fileRepository;
	private String tempFolder;

	private Function<ResticMapping, V1Result> toV1 = m -> new V1Result(m.getResticId());
	private Function<ResticMapping, V2Result> toV2 = m -> new V2Result(m.getResticId(), m.getFile().getSize());

	public ResticMapping getByTypeAndResticId(ResticType type, String resticId, long ownerAccountId) {
		return mappingsRepository.getByTypeAndResticIdAndOwnerAccountId(type, resticId, ownerAccountId);
	}

	public Collection<? extends V1Result> findByTypeAndOwnerAccountId(ResticType type, ResticUtils.ResticVersion apiVersion, long ownerAccountId) {
		return mappingsRepository.findByTypeAndOwnerAccountId(type, ownerAccountId).stream()
			.map(ResticUtils.ResticVersion.API_V2.equals(apiVersion) ? toV2 : toV1)
			.collect(Collectors.toList());
	}

	public void saveMapping(AccountDto accountId, ResticType type, String resticId, String absolutePath) {
		Account account = accountRepository.getById(accountId.getId());
		pl.pamsoft.imapcloud.entity.File uploadedFile = fileRepository.getByAbsolutePath(absolutePath);
		ResticMapping resticMapping = new ResticMapping();
		resticMapping.setOwnerAccount(account);
		resticMapping.setResticId(resticId);
		resticMapping.setType(type);
		resticMapping.setFile(uploadedFile);
		mappingsRepository.save(resticMapping);
	}


	public Path generatePath(ResticType type, String resticId) {
		return Paths.get(tempFolder, type.toString(), resticId);
	}

	public String storeOnDisk(byte[] data, ResticType type, String resticId) throws IOException {
		Path path = generatePath(type, resticId);
		Files.write(path, data);
		return path.toString();
	}

	public void upload(AccountDto accountDto, String fileNameWithPath, String fileName, Long size) throws IOException {
		FileDto fileDto = createFileDto(fileNameWithPath, fileName, size);
		uploadService.uploadSync(accountDto, Collections.singletonList(fileDto), true);
		Files.delete(Paths.get(fileNameWithPath));
	}

	public Path download(ResticType type, String name, long ownerAccountId) {
		ResticMapping mapping = getByTypeAndResticId(type, name, ownerAccountId);
		pl.pamsoft.imapcloud.entity.File file = mapping.getFile();
		Path dirPath = Paths.get(tempFolder, type.toString());
		new File(dirPath.toString()).mkdirs();
		Path path = generatePath(type, name);

		UploadedFileDto uploaded = new UploadedFileDto();
		uploaded.setFileUniqueId(file.getFileUniqueId());
		uploaded.setCompleted(file.isCompleted());

		FileDto fileDto = new FileDto();
		fileDto.setAbsolutePath(dirPath.toString());
		fileDto.setType(FileDto.FileType.FILE);
		fileDto.setName(name);
		fileDto.setSize(file.getSize());

		downloadService.downloadSync(uploaded, fileDto);
		return path;
	}

	@Transactional
	public void delete(ResticType type, String name, long ownerAccountId) {
		ResticMapping mapping = getByTypeAndResticId(type, name, ownerAccountId);
		deletionService.delete(mapping.getFile());
		mappingsRepository.deleteByTypeAndResticIdAndOwnerAccountId(type, name, ownerAccountId);
	}

	private FileDto createFileDto(String filename, String resticId, Long size) {
		FileDto fileDto = new FileDto();
		fileDto.setAbsolutePath(filename);
		fileDto.setName(resticId);
		fileDto.setType(FileDto.FileType.FILE);
		fileDto.setSize(size);
		return fileDto;
	}

	@Autowired
	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Autowired
	public void setMappingsRepository(ResticMappingsRepository mappingsRepository) {
		this.mappingsRepository = mappingsRepository;
	}

	@Autowired
	public void setUploadService(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	@Autowired
	public void setDownloadService(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	@Autowired
	public void setDeletionService(DeletionService deletionService) {
		this.deletionService = deletionService;
	}

	@Autowired
	public void setFileRepository(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	@Value("${ic.temp.folder}")
	public void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
		new File(tempFolder).mkdirs();
		for (ResticType type : ResticType.values()) {
			new File(Paths.get(tempFolder, type.toString()).toString()).mkdirs();
		}

	}

}
