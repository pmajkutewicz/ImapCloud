package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.dto.progress.ProgressEntryType;
import pl.pamsoft.imapcloud.dto.progress.ProgressStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
@Entity
public class EntryProgress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Version
	private Long version;

	private String absolutePath;
	private long size;
	private long progress;
	private ProgressStatus status;
	private ProgressEntryType type;

	public EntryProgress(String absolutePath, long size) {
		this.absolutePath = absolutePath;
		this.size = size;
		status = ProgressStatus.WAITING;
		type = ProgressEntryType.FILE;
	}

	public EntryProgress(String absolutePath, int size) {
		this.absolutePath = absolutePath;
		this.size = size;
		status = ProgressStatus.WAITING;
		type = ProgressEntryType.FOLDER;
	}

	public EntryProgress() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAbsolutePath() {
		return this.absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getProgress() {
		return this.progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public ProgressStatus getStatus() {
		return status;
	}

	public void setStatus(ProgressStatus status) {
		this.status = status;
	}

	public ProgressEntryType getType() {
		return type;
	}

	public void setType(ProgressEntryType type) {
		this.type = type;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
