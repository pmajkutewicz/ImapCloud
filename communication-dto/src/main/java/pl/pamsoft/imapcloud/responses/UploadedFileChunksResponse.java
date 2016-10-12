package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.UploadedFileChunkDto;

import java.util.List;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFileChunksResponse extends AbstractResponse {
	private List<UploadedFileChunkDto> fileChunks;

	public UploadedFileChunksResponse(List<UploadedFileChunkDto> fileChunks) {
		this.fileChunks = fileChunks;
	}

	public UploadedFileChunksResponse() {
	}

	public List<UploadedFileChunkDto> getFileChunks() {
		return this.fileChunks;
	}

	public void setFileChunks(List<UploadedFileChunkDto> fileChunks) {
		this.fileChunks = fileChunks;
	}
}
