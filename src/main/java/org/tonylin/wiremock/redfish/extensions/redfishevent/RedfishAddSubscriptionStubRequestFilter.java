package org.tonylin.wiremock.redfish.extensions.redfishevent;

import static com.github.tomakehurst.wiremock.common.LocalNotifier.notifier;

import java.io.IOException;
import java.util.Optional;

import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilter;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestWrapper;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.Request;
import com.google.common.base.Strings;

public class RedfishAddSubscriptionStubRequestFilter implements RequestFilter {
	private static final String REDFISH_PROTOCOL = "Redfish";
    @Override
    public String getName() {
        return "RedfishAddSubscriptionStubRequestFilter";
    }
	
    @Override
    public RequestFilterAction filter(Request request) {
    	HandleRequestState state = RedfishEventRecorder.getInstance().isRecording() ? new RecordingState() : new InitState();
    	return state.handle(request, this);
    }
    
    private interface HandleRequestState {
    	RequestFilterAction handle(Request request, RedfishAddSubscriptionStubRequestFilter context);
    }
    
    private static class InitState implements HandleRequestState {
		@Override
		public RequestFilterAction handle(Request request, RedfishAddSubscriptionStubRequestFilter context) {
			boolean isRecordingStart = request.getAbsoluteUrl().endsWith("__admin/recordings/start");
			if( isRecordingStart ) {
				RedfishEventRecorder.getInstance().start();
			}
			return RequestFilterAction.continueWith(request);
		}
    	
    }

    private static class RecordingState implements HandleRequestState {
    	private ObjectMapper objectMapper = new ObjectMapper();
    	
    	private RecordingState() {
    		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	
    	}
    	
		@Override
		public RequestFilterAction handle(Request request, RedfishAddSubscriptionStubRequestFilter context) {
			boolean isRecordingStop = request.getAbsoluteUrl().endsWith("__admin/recordings/stop");
			if( isRecordingStop ) {
				RedfishEventRecorder.getInstance().stop();
				return RequestFilterAction.continueWith(request);
			}
			
	    	SubscribeRedfishRequestBody redfishEventRequestBody = getSubscribeRedifshRequestBody(request);
	    	if( redfishEventRequestBody != null )
	    		return handleSubscribeRedfishRequest(request, redfishEventRequestBody);
	    	
	    	RedfishEvents redfishEvents = getPostRedfishEventsBody(request);
	    	if( redfishEvents != null )
	    		return handlePostRedfishEventsRequest(request, redfishEvents);
			
			return RequestFilterAction.continueWith(request);
		}
		
		private void publishEventToSource(String destination, String requestBody) {
//			HttpPost post = new HttpPost(destination);
//			post.setEntity(new StringEntity(requestBody, "utf-8"));
//			CloseableHttpClient httpClient = HttpClientFactory.createClient();
//			try {
//				CloseableHttpResponse response = httpClient.execute(post);
//				notifier().info(String.format("Handle post redfish event to %s: %s", destination, response.getStatusLine()));
//			} catch (IOException e) {
//				notifier().error(String.format("Failed to post redfish event: %s", e.getMessage()));
//			} finally {
//				closeHttpClient(httpClient);
//			}	
		}
		
		private RequestFilterAction handlePostRedfishEventsRequest(Request request, RedfishEvents redfishEventRequestBody) {
			String context = redfishEventRequestBody.getEvents().get(0).getContext();
			RedfishEventRecorder.getInstance().addEvent(context, request.getBodyAsString());
			
			Optional<String> destinationOpt = RedfishEventRecorder.getInstance().getDestination(context);
			destinationOpt.ifPresent(destination->publishEventToSource(destination, request.getBodyAsString()));
			return RequestFilterAction.stopWith(ResponseDefinitionBuilder.okForEmptyJson().build());
		}
		
		private void closeHttpClient(CloseableHttpClient httpClient) {
			try {
				httpClient.close();
			} catch (IOException e) {
				notifier().info(String.format("Failed to close httpclient: %s", e.getMessage()));
			}
		}
		
		private RequestFilterAction handleSubscribeRedfishRequest(Request request, SubscribeRedfishRequestBody redfishEventRequestBody) {
			RedfishEventRecorder.getInstance().markSubscription(redfishEventRequestBody.getContext(), redfishEventRequestBody.getDestination());
			
			Request wrapRequest = RequestWrapper.create()
					.transformBody(body->new Body(body.asString().replaceAll(":\\d+", ":80")))
					.wrap(request);
			
			return RequestFilterAction.continueWith(wrapRequest);
		}
    	
		private RedfishEvents getPostRedfishEventsBody(Request request) {
			String bodyString = request.getBodyAsString();
			if( Strings.isNullOrEmpty(bodyString) )
				return null;
			
			try {
				return objectMapper.readValue(bodyString, RedfishEvents.class);
			} catch (JsonProcessingException e) {
				return null;
			}
		}
		
		private SubscribeRedfishRequestBody getSubscribeRedifshRequestBody(Request request) {
			String bodyString = request.getBodyAsString();
			if( Strings.isNullOrEmpty(bodyString) )
				return null;
			
			try {
				SubscribeRedfishRequestBody redfishEventRequestBody = objectMapper.readValue(bodyString, SubscribeRedfishRequestBody.class);
				if( !REDFISH_PROTOCOL.equals(redfishEventRequestBody.getProtocol()) ||
						Strings.isNullOrEmpty(redfishEventRequestBody.getDestination()))
					return null;
				
				return redfishEventRequestBody;
			} catch (JsonProcessingException e) {
				return null;
			}
		}
    }

	@Override
	public boolean applyToAdmin() {
		return true;
	}

	@Override
	public boolean applyToStubs() {
		return true;
	}
}