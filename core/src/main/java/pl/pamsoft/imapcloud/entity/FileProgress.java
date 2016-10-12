package pl.pamsoft.imapcloud.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Id;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileProgress {
	@Id
	private String id;
	private String absolutePath;
	private long size;
	private long progress;

	public FileProgress(String absolutePath, long size) {
		this.absolutePath = absolutePath;
		this.size = size;
	}

	public FileProgress() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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
}
