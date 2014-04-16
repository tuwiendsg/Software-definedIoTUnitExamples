package activeMQclient.sdcomapi;

public class Event {

	private String eventContent;
	
	public Event(String eventContent){
		this.eventContent = eventContent;
	}
	
	public String getEventContent() {
		return eventContent;
	}
}
