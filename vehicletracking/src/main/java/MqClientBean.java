import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import jsonpars.JSONObject;
import jsonpars.JSONTokener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

@ManagedBean(name = "mqBean")
@ApplicationScoped
public class MqClientBean {

	@ManagedProperty(value = "#{mapBean}")
	private MapBean mapBean;
	private String lat = "10.77582";
	private String lon = "106.63454";
	private String label;
	final ArrayBlockingQueue<String> list = new ArrayBlockingQueue<String>(10);
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = "failover://tcp://127.0.0.1:61616";
	private boolean transacted;
	private int ackMode = Session.AUTO_ACKNOWLEDGE;
	private Session session;
	private Destination destination;
	private MessageConsumer consumer = null;
	private Connection connection;

	private PushContext pushContext;

	public MqClientBean() {
		System.out.println("Creatin consumer bean ...");
		this.pushContext = PushContextFactory.getDefault().getPushContext();
		try {
			String ip = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader("/etc/environment"));
				String line = br.readLine();
				while (line != null) {
					if (line.startsWith("mqtt")) {
						ip = line.split("=")[1];
						break;
					}
					line = br.readLine();
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if ("".equals(ip)) {
				ip = "127.0.0.1";
			}
			this.url = "failover://tcp://" + ip + ":61616";

			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			this.connection = connectionFactory.createConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void init() {
		System.out.println("Starting mq bean ...");
		try {
			connection.start();
			session = connection.createSession(transacted, ackMode);
			destination = session.createTopic("topic");
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {

				public void onMessage(Message message) {
					System.out.println("Thread update...");
					try {
						TextMessage event = (TextMessage) message;
						list.put(event.getText());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isStopped = false;

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public void startPush() {
		while (!isStopped()) {
			try {
				System.out.println("Pooling sync qeue!!");
				String event = list.poll(5, TimeUnit.SECONDS);

				if (event != null) {
					JSONTokener jtoken = new JSONTokener(event);
					JSONObject jsn = (JSONObject) jtoken.nextValue();
					String lat = jsn.getString("latitude");
					String lon = jsn.getString("longitude");
					this.mapBean.setLabel("Tracking vehicle id:" + jsn.getString("id") + "!\n Current location - latitude: " + lat + ", longitude: " + lon);
					this.mapBean.setLat(lat);
					this.mapBean.setLon(lon);
					this.mapBean.click(null);
					pushContext.push("/location", "update location");
					System.out.println("Pushed!!");
				} else {
					System.out.println("No sensor readings available!");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@PreDestroy
	public void cleanUp() {
		System.out.println("Stopping mq bean ...");
		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			setStopped(true);
		}
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setMapBean(MapBean mapBean) {
		this.mapBean = mapBean;
	}

	public MapBean getMapBean() {
		return mapBean;
	}

	public void setLabelandPush(String label) {
		this.label = label;
	}

}
