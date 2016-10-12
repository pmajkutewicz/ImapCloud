package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.AccountDto;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class StartUploadRequest {
	private List<FileDto> selectedFiles;
	private AccountDto selectedAccount;
	private Encryption chunkEncryption;

	public StartUploadRequest(List<FileDto> selectedFiles, AccountDto selectedAccount, Encryption chunkEncryption) {
		this.selectedFiles = selectedFiles;
		this.selectedAccount = selectedAccount;
		this.chunkEncryption = chunkEncryption;
	}

	public StartUploadRequest() {
	}

	public List<FileDto> getSelectedFiles() {
		return this.selectedFiles;
	}

	public void setSelectedFiles(List<FileDto> selectedFiles) {
		this.selectedFiles = selectedFiles;
	}

	public AccountDto getSelectedAccount() {
		return this.selectedAccount;
	}

	public void setSelectedAccount(AccountDto selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public Encryption getChunkEncryption() {
		return this.chunkEncryption;
	}

	public void setChunkEncryption(Encryption chunkEncryption) {
		this.chunkEncryption = chunkEncryption;
	}
}
