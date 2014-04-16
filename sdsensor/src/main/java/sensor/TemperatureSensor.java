package sensor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import activeMQclient.sdcomapi.Event;
import activeMQclient.sdcomapi.Producer;

/**
 * Software-defined temperature sensor
 * */

public class TemperatureSensor implements Runnable {

	private Producer producer;

	public TemperatureSensor() {}

	public static void main(String[] args) {

		final ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/wire.xml");
		BeanFactory factory = context;
		TemperatureSensor sens = (TemperatureSensor) factory.getBean("TemperatureSensor");
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
}
