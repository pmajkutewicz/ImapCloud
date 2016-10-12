package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class UploadedFileDto extends FileDto {

	private Boolean completed;

	private String fileUniqueId;

	public UploadedFileDto(Boolean completed, String fileUniqueId) {
		this.completed = completed;
		this.fileUniqueId = fileUniqueId;
	}

	public UploadedFileDto() {
	}

	public static UploadedFileDto folder(String name) {
		UploadedFileDto uploadedFileDto = new UploadedFileDto();
		uploadedFileDto.setName(name);
		uploadedFileDto.setType(FileType.DIRECTORY);
		return uploadedFileDto;
	}

	public Boolean getCompleted() {
		return this.completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public String getFileUniqueId() {
		return this.fileUniqueId;
	}

	public void setFileUniqueId(String fileUniqueId) {
		this.fileUniqueId = fileUniqueId;
	}
}
