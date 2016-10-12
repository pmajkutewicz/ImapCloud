package pl.pamsoft.imapcloud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@JsonIgnoreProperties({"readableFileSize"})
@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class RecoveredFileDto extends FileDto {

	private String fileUniqueId;

	private boolean completed;

	private boolean inDb;

	private Boolean completedInDb;

	public RecoveredFileDto(String name, String absolutePath, FileType type, Long size,
	                        String fileUniqueId, boolean completed) {
		super(name, absolutePath, type, size);
		this.fileUniqueId = fileUniqueId;
		this.completed = completed;
	}

	public RecoveredFileDto(String fileUniqueId, boolean completed, boolean inDb, Boolean completedInDb) {
		this.fileUniqueId = fileUniqueId;
		this.completed = completed;
		this.inDb = inDb;
		this.completedInDb = completedInDb;
	}

	public RecoveredFileDto() {
	}

	public String getFileUniqueId() {
		return this.fileUniqueId;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public boolean isInDb() {
		return this.inDb;
	}

	public void setInDb(boolean inDb) {
		this.inDb = inDb;
	}

	public Boolean getCompletedInDb() {
		return this.completedInDb;
	}

	public void setCompletedInDb(Boolean completedInDb) {
		this.completedInDb = completedInDb;
	}
}
