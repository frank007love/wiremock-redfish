package org.tonylin.wiremock.redfish.extensions.redfishevent;

import java.util.Optional;

import com.github.tomakehurst.wiremock.http.Request;

public class RedfishEventRecorder {

	private static RedfishEventRecorder INSTANCE = new RedfishEventRecorder();
	
	private RedfishEventRecorder() {
		
	}
	
	public static RedfishEventRecorder getInstance() {
		return INSTANCE;
	}
	
	public void addEvent() {
		
	}
	
	public void addRequest(Request request, String context) {
		
	}
	
	public Optional<Request> getRequest(String context) {
		return null;
	}
	
	public void stop() {
		
	}
}
