package pl.pamsoft.imapcloud.ff4j;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public enum IMAPCloudFeature {
	ACCOUNTS("accounts"),
	UPLOADS("uploads"),
	TASKS("tasks"),
	UPLOADED("uploaded"),
	DOWNLOADS("downloads"),
	RECOVERY("recovery"),
	STATUS("status"),
	MONITORING("monitoring");

	private String uid;

	private static Map<String, IMAPCloudFeature> uidToFeatureMap = Arrays.stream(IMAPCloudFeature.values()).collect(toMap(IMAPCloudFeature::getUid, Function.identity()));

	IMAPCloudFeature(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public static IMAPCloudFeature fromUid(String value) {
		return uidToFeatureMap.get(value);
	}
}
