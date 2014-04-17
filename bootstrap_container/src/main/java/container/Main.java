package container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import sdapi.Bootstrapable;

public class Main implements Observer {

	private static final String DIR = "chef_jars";
	private String mainClass = "";
	private AbstractApplicationContext context;
	private ScheduledExecutorService scheduler;

	public static void main(String[] args) {
		// PRE: Before the container is started confiruration manager creates working directory for the units.

		// TODO: Currently, to perform runtime configuration changes, all dependencies have to be available when starting the container.
		// We need to provide a custom class loader to add dependencies to the factory dynamically.
		Main container = new Main();
		container.startContainer();
	}

	private void startContainer() {
		this.context = new ClassPathXmlApplicationContext("META-INF/wire.xml");
		Resource res = this.context.getResource("classpath:META-INF/etc.conf");
		// res.lastModified()
		this.mainClass = getProperty(res, "Main");
		Bootstrapable mainObject = (Bootstrapable) this.context.getBean(this.mainClass);
		mainObject.setRootDependency(mainObject);
		mainObject.start();

		Listener configListener = new Listener(context);
		configListener.addObserver(this);
		this.scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(configListener, 0, 1, TimeUnit.SECONDS);

	}

	public void stopContainer() {
		this.context.stop();
		this.scheduler.shutdown();
	}

	private String getProperty(Resource from, String key) {
		String value = "";
		try {
			InputStream is = from.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(key)) {
					value = line.split(":")[1];
					value = value.trim();
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("No .conf file found!");
		}
		return value;
	}

	public void update(Observable o, Object arg) {
		if (o instanceof Listener) {
			String e = (String) arg;
			if ("update".equals(e)) {
				Bootstrapable mainObject = (Bootstrapable) this.context.getBean(this.mainClass);
				mainObject.setRootDependency(mainObject);
				mainObject.start();
			}

		}

	}

}
