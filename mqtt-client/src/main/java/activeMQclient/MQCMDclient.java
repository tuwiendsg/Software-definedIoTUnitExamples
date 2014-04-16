package activeMQclient;

import activeMQclient.sdcomapi.Event;

public class MQCMDclient{

	

	public static void main(String[] args) {
		System.out.println("USAGE: (producer|consumer) hostIP ");
		String host = args[1];
		String url = "failover://tcp://"+host+":61616";
		
		if("consumer".equals(args[0])){
			System.out.println("Starting consumer ...");
			MQConsumer listener = new MQConsumer(url,"topic");
			listener.setUp();
			System.out.println("Sucessfully connnected to "+host);
		}else if("producer".equals(args[0])){
			System.out.println("Starting producer ...");
			MQProducer producer = new MQProducer(url,"topic");
			producer.setUp();
			producer.push(new Event("This is a meassage!"));
			System.out.println("Sucessfully sent message to "+host);
			producer.close();
		}
		
	}

	

}
