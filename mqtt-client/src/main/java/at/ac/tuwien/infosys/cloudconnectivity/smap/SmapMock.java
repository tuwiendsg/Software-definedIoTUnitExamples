package at.ac.tuwien.infosys.cloudconnectivity.smap;

import at.ac.tuwien.infosys.common.sdapi.Event;
import at.ac.tuwien.infosys.common.sdapi.Producer;

public class SmapMock implements Producer{

	public void push(Event e) {
		System.out.println("sMAP sends: " + e.getEventContent());
	}

	public void setUp() {
		System.out.println("Starting sMAP client!");
	}

	public void pollEvent() {
		throw new UnsupportedOperationException();
	}

	public void close() {
		//throw new UnsupportedOperationException();
		System.out.println("Stopping sMAP client!");
	}
}
