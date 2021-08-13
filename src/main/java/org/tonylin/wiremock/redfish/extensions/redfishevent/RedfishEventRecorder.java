package org.tonylin.wiremock.redfish.extensions.redfishevent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RedfishEventRecorder {

	private static RedfishEventRecorder INSTANCE = new RedfishEventRecorder();
	
	private Map<String, Date> contextOfStartTime = new ConcurrentHashMap<>();
	private Map<String, String> contextOfDestination = new ConcurrentHashMap<>();
	private Map<String, List<Event>> rawEventOfContexts = new ConcurrentHashMap<>();
	
	private boolean isRecording = false;
	
	private RedfishEventRecorder() {
		
	}
	
	public boolean isRecording() {
		return isRecording;
	}
	
	public void start() {
		isRecording = true;
		contextOfDestination.clear();
		contextOfStartTime.clear();
		rawEventOfContexts.clear();
	}
	
	public static RedfishEventRecorder getInstance() {
		return INSTANCE;
	}
	
	public void addEvent(String context, String rawData) {
		if( !contextOfStartTime.containsKey(context) ) {
			return;
		}
		long duration = System.currentTimeMillis() - contextOfStartTime.get(context).getTime();
		List<Event> events = rawEventOfContexts.getOrDefault(context, new ArrayList<>());
		events.add(new Event(rawData, duration));
		rawEventOfContexts.put(context, events);
	}
	
	public List<Event> getEvents(String context) {
		return rawEventOfContexts.getOrDefault(context, new ArrayList<>());
	}
	
	public void markSubscription(String context, String destionation) {
		contextOfDestination.putIfAbsent(context, destionation);
		contextOfStartTime.putIfAbsent(context, new Date());
	}
	
	public Optional<String> getDestination(String context){
		return Optional.ofNullable(contextOfDestination.get(context));
	}
	
	public void stop() {
		isRecording = false;
	}
	
	public static class Event {
		private final String rawData;
		private final long delay;
		
		public Event(String rawData, long delay) {
			this.rawData = rawData;
			this.delay = delay;
		}
		
		public String getRawData() {
			return rawData;
		}
		
		public long getDelay() {
			return delay;
		}
	}
}
