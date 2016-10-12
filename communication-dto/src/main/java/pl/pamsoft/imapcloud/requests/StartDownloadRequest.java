package pl.pamsoft.imapcloud.requests;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.FileDto;
import pl.pamsoft.imapcloud.dto.UploadedFileDto;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class StartDownloadRequest {
	private UploadedFileDto fileToDownload;
	private FileDto destDir;

	public StartDownloadRequest(UploadedFileDto fileToDownload, FileDto destDir) {
		this.fileToDownload = fileToDownload;
		this.destDir = destDir;
	}

	public StartDownloadRequest() {
	}

	public UploadedFileDto getFileToDownload() {
		return this.fileToDownload;
	}

	public void setFileToDownload(UploadedFileDto fileToDownload) {
		this.fileToDownload = fileToDownload;
	}

	public FileDto getDestDir() {
		return this.destDir;
	}

	public void setDestDir(FileDto destDir) {
		this.destDir = destDir;
	}
}
