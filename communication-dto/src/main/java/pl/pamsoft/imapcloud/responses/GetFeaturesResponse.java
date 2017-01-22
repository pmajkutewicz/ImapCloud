package pl.pamsoft.imapcloud.responses;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import pl.pamsoft.imapcloud.ff4j.IMAPCloudFeature;

import java.util.Map;

@SuppressFBWarnings({"UCPM_USE_CHARACTER_PARAMETERIZED_METHOD", "USBR_UNNECESSARY_STORE_BEFORE_RETURN"})
public class GetFeaturesResponse extends AbstractResponse {

	private Map<IMAPCloudFeature, Boolean> featureMap;

	public GetFeaturesResponse() {

	}

	public GetFeaturesResponse(Map<IMAPCloudFeature, Boolean> featureMap) {
		this.featureMap = featureMap;
	}

	public Map<IMAPCloudFeature, Boolean> getFeatureMap() {
		return featureMap;
	}
}
