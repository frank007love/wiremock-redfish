package org.tonylin.wiremock.redfish.extensions.redfishevent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
		
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscribeRedfishRequestBody {
	@JsonProperty(value="Destination")
	private String destination;
	@JsonProperty(value="Protocol")
	private String protocol;
	@JsonProperty(value="Context")
	private String context;
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getContext() {
		return context;
	}
}