package pl.pamsoft.imapcloud.websocket;

import javax.websocket.ClientEndpointConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class AuthorizationConfigurator extends ClientEndpointConfig.Configurator {
	@Override
	public void beforeRequest(Map<String, List<String>> headers) {
		headers.put("Authorization", Collections.singletonList("Basic " + printBase64Binary("user:test".getBytes())));
	}
}
