package pl.pamsoft.imapcloud.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class FileProgressData {
	private String id;
	private String absolutePath;
	private long size;
	private long progress;

	public FileProgressData(String id, String absolutePath, long size, long progress) {
		this.id = id;
		this.absolutePath = absolutePath;
		this.size = size;
		this.progress = progress;
	}

	public FileProgressData() {
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
