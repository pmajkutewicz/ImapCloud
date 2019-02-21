package pl.pamsoft.imapcloud.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.pamsoft.imapcloud.ff4j.IMAPCloudFeature;
import pl.pamsoft.imapcloud.responses.GetFeaturesResponse;
import pl.pamsoft.imapcloud.rest.IMAPCloudFeaturesClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeaturesControllerIT extends AbstractIntegrationTest {

	private IMAPCloudFeaturesClient imapCloudFeaturesClient;

	@BeforeAll
	void init() {
		imapCloudFeaturesClient = new IMAPCloudFeaturesClient(getEndpoint(), getUsername(), getPassword());
	}

	@Test
	void shouldReturnGitStats() throws IOException, InterruptedException {
		GetFeaturesResponse features = imapCloudFeaturesClient.getFeatures();

		Map<IMAPCloudFeature, Boolean> featureMap = features.getFeatureMap();
		assertTrue(featureMap.get(IMAPCloudFeature.ACCOUNTS));
		assertTrue(featureMap.get(IMAPCloudFeature.UPLOADS));
		assertTrue(featureMap.get(IMAPCloudFeature.DOWNLOADS));
		assertTrue(featureMap.get(IMAPCloudFeature.RECOVERY));
		assertTrue(featureMap.get(IMAPCloudFeature.UPLOADED));
		assertTrue(featureMap.get(IMAPCloudFeature.TASKS));
		assertTrue(featureMap.containsKey(IMAPCloudFeature.MONITORING));
		assertTrue(featureMap.containsKey(IMAPCloudFeature.STATUS));
	}

}
