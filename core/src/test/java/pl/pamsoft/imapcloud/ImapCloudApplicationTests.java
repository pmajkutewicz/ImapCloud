package pl.pamsoft.imapcloud;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringApplicationConfiguration(ImapCloudApplication.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext
public class ImapCloudApplicationTests extends AbstractTestNGSpringContextTests {

	@Value("${security.user.password}")
	public String password;

	@Value("${local.server.port}")
	public int targetWebServerPort;

	@Test
	public void indexShouldWork() throws IOException {
		String pageCode = getPage(new HttpGet("/"));

		assertThat(pageCode, containsString("Swagger UI"));
		assertThat(pageCode, containsString("FF4J Console"));
		assertThat(pageCode, containsString("FF4J Web Console"));
	}

	@Test
	public void swaggerShouldWork() throws IOException {
		String pageCode = getPage(new HttpGet("/swagger-ui.html"));

		assertThat(pageCode, containsString("<title>Swagger UI</title>"));
		assertThat(pageCode, containsString("swagger-ui-container"));
	}

	@Test
	public void ff4jWebConsoleShouldWork() throws IOException {
		String pageCode = getPage(new HttpGet("/ff4j-web-console/home"));

		assertThat(pageCode, containsString("<title>FF4J - Home</title>"));
		assertThat(pageCode, containsString("Administration"));
	}

	@Test
	public void ff4jConsoleShouldWork() throws IOException {
		String pageCode = getPage(new HttpGet("/ff4j-console"));

		assertThat(pageCode, containsString("<title>ff4j</title>"));
		assertThat(pageCode, containsString("Uploads tab in GUI"));
	}

	private String getPage(HttpRequest request) throws IOException {
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = client.execute(getHost(), request);
		HttpEntity entity = response.getEntity();
		return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.displayName());
	}

	private CloseableHttpClient getClient() {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
			= new UsernamePasswordCredentials("user", password);
		provider.setCredentials(AuthScope.ANY, credentials);

		return HttpClientBuilder.create()
			.setDefaultCredentialsProvider(provider)
			.build();
	}

	private HttpHost getHost() {
		return new HttpHost("localhost", targetWebServerPort);
	}
}
