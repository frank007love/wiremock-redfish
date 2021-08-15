package org.tonylin.wiremock.redfish;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.tonylin.wiremock.redfish.extensions.FaultStubMappingTransformer;
import org.tonylin.wiremock.redfish.extensions.SocketTimedOutResponseTransformer;
import org.tonylin.wiremock.redfish.extensions.redfishevent.RedfishSubscriptionRequestFilter;
import org.tonylin.wiremock.redfish.extensions.redfishevent.RedfishEventStubMappingTransformer;
import org.wiremock.webhooks.Webhooks;

import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;

public class Application {
	private static final String REDFISH_HTTPS_PORT = "443";
	private static final String REDFISH_HTTP_PORT = "80";
	private static final List<Class<?>> EXTENSION_CLASSES = Arrays.asList(FaultStubMappingTransformer.class,
			SocketTimedOutResponseTransformer.class, RedfishSubscriptionRequestFilter.class, 
			RedfishEventStubMappingTransformer.class, Webhooks.class);

	private static String getExtensions() {
		return EXTENSION_CLASSES.stream().map(Class::getName).collect(Collectors.joining(","));
	}
	
	public static void main(String[] args) {
		
		String[] redfishArgs = new String[] {
				"--verbose",
				"--https-port", REDFISH_HTTPS_PORT,
				"--port", REDFISH_HTTP_PORT,
				"--enable-browser-proxying", "--trust-all-proxy-targets",
				"--extensions",
				getExtensions()};

		RedfishSubscriptionRequestFilter.setPort(REDFISH_HTTP_PORT);
		
		WireMockServerRunner.main(redfishArgs);
	}

}
