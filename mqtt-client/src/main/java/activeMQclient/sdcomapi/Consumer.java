package activeMQclient.sdcomapi;

public interface Consumer {

	public void setUp();
	
	public void onEvent();
	
	public void close();
}
