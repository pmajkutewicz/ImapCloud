package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFilesResponse extends AbstractResponse {
	private List<UploadedFileDto> files;

	public UploadedFilesResponse(List<UploadedFileDto> files) {
		this.files = files;
	}

	public UploadedFilesResponse() {
	}

	public List<UploadedFileDto> getFiles() {
		return this.files;
	}

	public void setFiles(List<UploadedFileDto> files) {
		this.files = files;
	}
}
