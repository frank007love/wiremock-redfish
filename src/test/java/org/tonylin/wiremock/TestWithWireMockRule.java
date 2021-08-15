package org.tonylin.wiremock;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.HttpClientFactory;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class TestWithWireMockRule {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options()
			.port(80)
			.httpsPort(443)
			.usingFilesUnderDirectory("./testdata/loadMappings/query"));
	
	
	@Test
	public void getHttpCode200WhenQueryUser() throws IOException {
		HttpGet post = new HttpGet("http://localhost/api/users/2");
		CloseableHttpClient httpClient = HttpClientFactory.createClient();
		try {
			CloseableHttpResponse response = httpClient.execute(post);
			assertEquals(200, response.getStatusLine().getStatusCode());
		} finally {
			httpClient.close();
		}	
	}
}
