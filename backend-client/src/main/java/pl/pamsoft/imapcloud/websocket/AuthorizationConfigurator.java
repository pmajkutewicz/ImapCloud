package pl.pamsoft.imapcloud.websocket;

import javax.websocket.ClientEndpointConfig;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class AuthorizationConfigurator extends ClientEndpointConfig.Configurator {
	private final String basicAuth;

	public AuthorizationConfigurator(String username, String password) throws UnsupportedEncodingException {
		String basicAuthString = username + ':' + password;
		this.basicAuth = "Basic " + printBase64Binary(basicAuthString.getBytes(StandardCharsets.UTF_8.toString()));
	}

	@Override
	public void beforeRequest(Map<String, List<String>> headers) {
		headers.put("Authorization", Collections.singletonList(basicAuth));
	}
}
