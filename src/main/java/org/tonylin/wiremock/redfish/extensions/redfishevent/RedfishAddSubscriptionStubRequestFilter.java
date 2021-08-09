package org.tonylin.wiremock.redfish.extensions.redfishevent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.StubRequestFilter;
import com.github.tomakehurst.wiremock.http.Request;
import com.google.common.base.Strings;

public class RedfishAddSubscriptionStubRequestFilter extends StubRequestFilter {
	private static final String REDFISH_PROTOCOL = "Redfish";
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class RedfishEventRequestBody {
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
	
	private RedfishEventRequestBody getRedfishEventRequestBody(Request request) {
		String bodyString = request.getBodyAsString();
		if( Strings.isNullOrEmpty(bodyString) )
			return null;
		
		try {
			RedfishEventRequestBody redfishEventRequestBody = objectMapper.readValue(bodyString, RedfishEventRequestBody.class);
			if( !REDFISH_PROTOCOL.equals(redfishEventRequestBody.getProtocol()) ||
					Strings.isNullOrEmpty(redfishEventRequestBody.getDestination()))
				return null;
			
			return redfishEventRequestBody;
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
    @Override
    public RequestFilterAction filter(Request request) {
    	RedfishEventRequestBody redfishEventRequestBody = getRedfishEventRequestBody(request);
    	if( redfishEventRequestBody == null )
    		return RequestFilterAction.continueWith(request);
    	
    	// refine request
    	// forward redfish event to original source
    	
    	return RequestFilterAction.continueWith(request);
    }

    @Override
    public String getName() {
        return "RedfishAddSubscriptionStubRequestFilter";
    }
}