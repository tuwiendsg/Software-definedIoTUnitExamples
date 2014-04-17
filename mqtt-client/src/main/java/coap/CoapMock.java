package coap;

import activeMQclient.sdcomapi.Event;
import activeMQclient.sdcomapi.Producer;

public class CoapMock implements Producer {

	public void push(Event e) {
		System.out.println("CoAP sends: " + e.getEventContent());
	}

	public void setUp() {
		// Do nothing
	}

	public void pollEvent() {
		throw new UnsupportedOperationException();
	}

	public void close() {
		throw new UnsupportedOperationException();
	}

}
