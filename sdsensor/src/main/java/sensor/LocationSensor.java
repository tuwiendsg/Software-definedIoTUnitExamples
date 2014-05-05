package sensor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import sdapi.com.Event;
import sdapi.com.RefreshableProducerDelegate;
import sdapi.container.Bootstrapable;

/**
 * Software-defined temperature sensor
 * */

public class LocationSensor implements Runnable, Bootstrapable {

	private static Logger LOGGER = Logger.getLogger(LocationSensor.class);
	private RefreshableProducerDelegate producerDelegate;
	private ScheduledExecutorService scheduler = null;
	private int updates = 1;

	public LocationSensor() {
	}

	public void setRootDependency(Object o) {
		// do nothing
	}

	public void start() {
		LOGGER.info("Starting Location Sensor ...");
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
	}

	public void run() {
		DataInstance temp = DataProvider.getProvider().getNextInstance();
		LOGGER.info(String.format("Reading update number %s - %s ", this.updates, temp.getJSON()));
		this.producerDelegate.push(new Event(temp.getJSON()));
		updates++;

	}

	public void setProducerDelegate(RefreshableProducerDelegate producer) {
		this.producerDelegate = producer;
	}

	public void stop() {
		LOGGER.info("Stoppinng Location Sensor ...");
		if (scheduler != null) {
			scheduler.shutdown();
		}

	}

	public static void main(String[] args) {
	}
}
