package pl.pamsoft.imapcloud.integration;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.ff4j.IMAPCloudFeature;
import pl.pamsoft.imapcloud.responses.GetFeaturesResponse;
import pl.pamsoft.imapcloud.rest.IMAPCloudFeaturesClient;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class FeaturesControllerIT extends AbstractIntegrationTest {

	private IMAPCloudFeaturesClient imapCloudFeaturesClient;

	@BeforeClass
	public void init() {
		imapCloudFeaturesClient = new IMAPCloudFeaturesClient(getEndpoint(), getUsername(), getPassword());
	}

	@Test
	public void shouldReturnGitStats() throws IOException, InterruptedException {
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
