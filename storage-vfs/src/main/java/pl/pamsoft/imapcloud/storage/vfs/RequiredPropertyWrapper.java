package pl.pamsoft.imapcloud.storage.vfs;

import java.util.Map;

public class RequiredPropertyWrapper {

	private String fs;
	private String location;

	public RequiredPropertyWrapper(Map<String, String> additionalProperties) {
		this.fs = additionalProperties.get("fs");
		this.location = additionalProperties.get("location");
	}

	public String getFs() {
		return fs;
	}

	public String getLocation() {
		return location;
	}
}
