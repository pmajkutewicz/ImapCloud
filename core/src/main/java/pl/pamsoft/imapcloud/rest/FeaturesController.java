package pl.pamsoft.imapcloud.rest;

import io.swagger.annotations.ApiOperation;
import org.ff4j.FF4j;
import org.ff4j.core.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pamsoft.imapcloud.ff4j.IMAPCloudFeature;
import pl.pamsoft.imapcloud.responses.GetFeaturesResponse;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/features")
public class FeaturesController {

	private FF4j ff4j;

	@ApiOperation("Returns enabled features info")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<GetFeaturesResponse> checkGitRevision() throws IOException {
		Map<String, Feature> features = ff4j.getFeatures();
		Map<IMAPCloudFeature, Boolean> featureBooleanMap = features.entrySet().stream().collect(toMap(f -> IMAPCloudFeature.fromUid(f.getKey()), f -> f.getValue().isEnable()));
		return new ResponseEntity<>(new GetFeaturesResponse(featureBooleanMap), HttpStatus.OK);
	}

	@Autowired
	public void setFf4j(FF4j ff4j) {
		this.ff4j = ff4j;
	}
}
