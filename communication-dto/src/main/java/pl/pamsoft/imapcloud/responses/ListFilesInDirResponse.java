package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.FileDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class ListFilesInDirResponse extends AbstractResponse {
	private List<FileDto> files;

	public ListFilesInDirResponse(List<FileDto> files) {
		this.files = files;
	}

	public ListFilesInDirResponse() {
	}

	public List<FileDto> getFiles() {
		return this.files;
	}

	public void setFiles(List<FileDto> files) {
		this.files = files;
	}
}
