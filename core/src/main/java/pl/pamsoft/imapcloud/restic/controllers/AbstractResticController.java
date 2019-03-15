package pl.pamsoft.imapcloud.restic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.restic.ResticType;
import pl.pamsoft.imapcloud.services.AccountServices;
import pl.pamsoft.imapcloud.services.ResticService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public abstract class AbstractResticController {

	AccountServices accountServices;
	ResticService resticService;

	void upload(String path, ResticType type, String name, InputStream is) throws IOException {
		Optional<AccountDto> account = accountServices.getByResticName(path);
		byte[] data = StreamUtils.copyToByteArray(is);
		String fileNameWithPath = resticService.storeOnDisk(data, type, name);
		resticService.upload(account.get(), fileNameWithPath, name, (long) data.length);
		resticService.saveMapping(account.get(), type, name, fileNameWithPath);
	}

	byte[] download(String path, ResticType type, String name) throws IOException {
		Optional<AccountDto> account = accountServices.getByResticName(path);
		Path downloadedFile = resticService.download(type, name, account.get().getId());
		byte[] bytes = StreamUtils.copyToByteArray(Files.newInputStream(downloadedFile));
		Files.delete(downloadedFile);
		return bytes;
	}

	void delete(String path, ResticType type, String name) throws IOException {
		Optional<AccountDto> account = accountServices.getByResticName(path);
		resticService.delete(type, name, account.get().getId());
	}

	@Autowired
	public void setResticService(ResticService resticService) {
		this.resticService = resticService;
	}

	@Autowired
	public void setAccountServices(AccountServices accountServices) {
		this.accountServices = accountServices;
	}
}
