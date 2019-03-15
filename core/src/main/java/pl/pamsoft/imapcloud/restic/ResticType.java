package pl.pamsoft.imapcloud.restic;

public enum ResticType {

	DATA("data"),
	KEYS("keys"),
	LOCKS("locks"),
	SNAPSHOTS("snapshots"),
	INDEX("index"),
	CONFIG("config");

	private String type;

	ResticType(String type) {
		this.type = type;
	}
}
