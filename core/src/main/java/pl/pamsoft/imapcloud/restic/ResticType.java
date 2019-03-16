package pl.pamsoft.imapcloud.restic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResticType {

	DATA("data"),
	KEYS("keys"),
	LOCKS("locks"),
	SNAPSHOTS("snapshots"),
	INDEX("index"),
	CONFIG("config");

	private String type;

	@JsonValue
	public String getType() {
		return type;
	}

	@JsonCreator
	public static ResticType of(String value) {
		if (null == value) {
			return null;
		}

		for (ResticType item : ResticType.values()) {
			if (value.equals(item.getType())) {
				return item;
			}
		}

		throw new RuntimeException("ResticType: unknown value: " + value);
	}

	ResticType(String type) {
		this.type = type;
	}
}
