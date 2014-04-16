package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class DataProvider {

	public static DataProvider provider;
	private static final Object lock = new Object();

	public static DataProvider getProvider() {
		synchronized (lock) {
			if (provider == null) {
				provider = new DataProvider();
			}
			return provider;
		}
	}

	private Stack<DataInstance> dataInstances = new Stack<DataInstance>();

	private DataProvider() {
		InputStreamReader reader = new InputStreamReader(DataProvider.class.getClassLoader().getResourceAsStream("data-1278.csv"));

		BufferedReader br = new BufferedReader(reader);

		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				DataInstance di = new DataInstance(split[0], split[1], split[2]);
				dataInstances.push(di);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public DataInstance getNextInstance() {
		// FIXME: reading data in reverse order
		return this.dataInstances.pop();
	}

}
