package org.tonylin.wiremock.redfish.extensions.redfishevent;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedfishEvents {
	
	@JsonProperty("@odata.context")
	private String odataContext;
	
	@JsonProperty("@odata.id")
	private String odataId;
	
	@JsonProperty("@odata.type")
	private String odataTpye;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Events")
	private List<Event> events = new ArrayList<>();
	
	public String getOdataContext() {
		return odataContext;
	}

	public void setOdataContext(String odataContext) {
		this.odataContext = odataContext;
	}

	public String getOdataId() {
		return odataId;
	}

	public void setOdataId(String odataId) {
		this.odataId = odataId;
	}

	public String getOdataTpye() {
		return odataTpye;
	}

	public void setOdataTpye(String odataTpye) {
		this.odataTpye = odataTpye;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public static class Event{
		@JsonProperty("EventType")
		private String eventType;
		
		@JsonProperty("Severity")
		private String severity;
		
		@JsonProperty("EventTimestamp")
		private String evnetTimestamp;
		
		@JsonProperty("Message")
		private String message;
		
		@JsonProperty("MessageArgs")
		private String[] messageArgs;
		
		@JsonProperty("MessageId")
		private String messageId;
		
		@JsonProperty("Context")
		private String context;

		public String getEventType() {
			return eventType;
		}
		public void setEventType(String eventType) {
			this.eventType = eventType;
		}
		
		public String getSeverity() {
			return severity;
		}
		public void setSeverity(String severity) {
			this.severity = severity;
		}
		
		public String getEvnetTimestamp() {
			return evnetTimestamp;
		}
		public void setEvnetTimestamp(String evnetTimestamp) {
			this.evnetTimestamp = evnetTimestamp;
		}
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		public String[] getMessageArgs() {
			return messageArgs;
		}
		public void setMessageArgs(String[] messageArgs) {
			this.messageArgs = messageArgs;
		}
		
		public String getMessageId() {
			return messageId;
		}
		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}
		
		public String getContext() {
			return context;
		}
		public void setContext(String context) {
			this.context = context;
		}
	}
	
}
