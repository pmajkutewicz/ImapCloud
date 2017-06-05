package pl.pamsoft.imapcloud.dto.progress;

public enum ProgressStatus {
	WAITING,
	UPLOADING, UPLOADED(true), ALREADY_UPLOADED(true),
	RECOVERY_SCANNING, RECOVERY_SCANNED(true),
	RECOVERING, RECOVERED(true);

	private boolean isTaskCompleted;

	ProgressStatus() {
		this.isTaskCompleted = false;
	}

	ProgressStatus(boolean isTaskCompleted) {
		this.isTaskCompleted = isTaskCompleted;
	}

	public boolean isTaskCompleted() {
		return isTaskCompleted;
	}
}
