package sensor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sdapi.Bootstrapable;

import activeMQclient.sdcomapi.Event;
import activeMQclient.sdcomapi.Producer;

/**
 * Software-defined temperature sensor
 * */

public class LocationSensor implements Runnable, Bootstrapable {

	private Producer producer;
	private LocationSensor sens;
	
	public LocationSensor() {}

	public void setRootDependency(Object o) {
		sens = (LocationSensor)o;
	}

	public void start(){
		sens.producer.setUp();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(sens, 0, 1, TimeUnit.SECONDS);
	}
	
	public void run() {
		DataInstance temp = DataProvider.getProvider().getNextInstance();
		System.out.println("Sensor reading update: " + temp.getJSON());
		this.producer.push(new Event(temp.getJSON()));

	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

		
	public static void main(String[] args) {

	}
}
