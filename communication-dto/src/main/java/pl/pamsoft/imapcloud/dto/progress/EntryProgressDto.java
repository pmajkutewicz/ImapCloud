package pl.pamsoft.imapcloud.dto.progress;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class EntryProgressDto {
	private Long id;
	private String absolutePath;
	private long size;
	private long progress;
	private ProgressStatus status;
	private ProgressEntryType type;

	public EntryProgressDto(Long id, String absolutePath, long size, long progress, ProgressStatus status, ProgressEntryType type) {
		this.id = id;
		this.absolutePath = absolutePath;
		this.size = size;
		this.progress = progress;
		this.status = status;
		this.type = type;
	}

	public EntryProgressDto() {
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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("EntryProgressDto{");
		sb.append("id='").append(id).append('\'');
		sb.append(", absolutePath='").append(absolutePath).append('\'');
		sb.append(", size=").append(size);
		sb.append(", progress=").append(progress);
		sb.append(", status=").append(status);
		sb.append(", type=").append(type);
		sb.append('}');
		return sb.toString();
	}
}
