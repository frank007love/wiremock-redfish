package org.tonylin.wiremock.redfish.extensions.redfishevent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.tonylin.wiremock.redfish.extensions.redfishevent.RedfishEventRecorder.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeActionDefinition;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class RedfishEventStubMappingTransformer extends StubMappingTransformer {
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public String getName() {
		return "RedfishEventStubMappingTransformer";
	}

	private boolean notSupportedRequest(RequestPattern requestPattern) {
		if(!requestPattern.getUrl().contains("/redfish/v1/EventService/Subscriptions")) {
			return true;
		}
		if( requestPattern.getBodyPatterns() == null ||
				requestPattern.getBodyPatterns().isEmpty() )
			return true;
		
		return !(requestPattern.getBodyPatterns().get(0) instanceof EqualToJsonPattern);
	}
	
	private void removeDestination(RequestPattern requestPattern) {
		EqualToJsonPattern bodyPattern = (EqualToJsonPattern)requestPattern.getBodyPatterns().get(0);
		final String bodyString = bodyPattern.getEqualToJson();
		String removedDestBodyString = null;
		
		try {
			ObjectNode objectNode = (ObjectNode)objectMapper.readTree(bodyString);
			objectNode.remove("Destination");
			removedDestBodyString = objectMapper.writeValueAsString(objectNode);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}

		EqualToJsonPattern  newBodyPattern = new EqualToJsonPattern(removedDestBodyString, true, true);
		requestPattern.getBodyPatterns().clear();
		requestPattern.getBodyPatterns().add(newBodyPattern);
	}
	
	private String parseContext(StubMapping stubMapping) {
		RequestPattern requestPattern = stubMapping.getRequest();
		EqualToJsonPattern bodyPattern = (EqualToJsonPattern)requestPattern.getBodyPatterns().get(0);
		
		try {
			return objectMapper.readValue(bodyPattern.getEqualToJson(), SubscribeRedfishRequestBody.class).getContext();
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private Parameters toParameters(Event postEvent) {
		Parameters parameters = new Parameters();
		parameters.put("method", "POST");
		parameters.put("url", "{{jsonPath originalRequest.body '$.Destination'}}");
		parameters.put("body", postEvent.getRawData());
		
		Map<String, Object> delay = new HashMap<>();
		delay.put("type", "fixed");
		delay.put("milliseconds", postEvent.getDelay());
		parameters.put("delay", delay);
		
		return parameters;
	}
	
	private void applyRedfishEvents(StubMapping stubMapping) {
		String context = parseContext(stubMapping);
		if( context == null )
			return;
		
		List<Event> postEvents = RedfishEventRecorder.getInstance().getEvents(context);
		List<PostServeActionDefinition> postServerActionDefinitions = postEvents.stream()
				.map(this::toParameters)
				.map(parameters->new PostServeActionDefinition("webhook", parameters))
				.collect(Collectors.toList());
		
		stubMapping.setPostServeActions(postServerActionDefinitions);
	}
	
	@Override
	public StubMapping transform(StubMapping stubMapping, FileSource files, Parameters parameters) {
		RequestPattern requestPattern = stubMapping.getRequest();
		if(notSupportedRequest(requestPattern))
			return stubMapping;
		
		removeDestination(requestPattern);
		applyRedfishEvents(stubMapping);
		
		return stubMapping;
	}

}
