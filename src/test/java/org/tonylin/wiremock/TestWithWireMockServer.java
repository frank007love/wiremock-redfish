package org.tonylin.wiremock;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.After;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.HttpClientFactory;

public class TestWithWireMockServer {
	private WireMockServer wireMockServer;
	
	@After
	public void teardown() {
		wireMockServer.stop();
	}
	
	private void launchWireMockServer(String testdataPath) {
		wireMockServer = new WireMockServer(WireMockConfiguration.options()
				.port(80)
				.httpsPort(443)
				.usingFilesUnderDirectory(testdataPath));
		wireMockServer.start();
	}
	
	@Test
	public void getHttpCode200WhenQueryUser() throws IOException {
		launchWireMockServer("./testdata/loadMappings/query");
		
		HttpGet post = new HttpGet("http://localhost/api/users/2");
		CloseableHttpClient httpClient = HttpClientFactory.createClient();
		try {
			CloseableHttpResponse response = httpClient.execute(post);
			assertEquals(200, response.getStatusLine().getStatusCode());
		} finally {
			httpClient.close();
		}	
	}
	
	@Test
	public void getHttpCode200WhenUpdateUser() throws IOException {
		launchWireMockServer("./testdata/loadMappings/update");
		
		HttpPatch patch = new HttpPatch("http://localhost/api/users/2");
		patch.setEntity(new StringEntity("{\"name\":\"morpheus\",\"job\":\"zion resident\"}", "utf-8"));
		CloseableHttpClient httpClient = HttpClientFactory.createClient();
		try {
			CloseableHttpResponse response = httpClient.execute(patch);
			assertEquals(200, response.getStatusLine().getStatusCode());
		} finally {
			httpClient.close();
		}	
	}
}
