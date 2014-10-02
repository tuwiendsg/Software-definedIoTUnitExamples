package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import sensor.GenericDataInstance.Record;

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

	private Stack<GenericDataInstance> dataInstances = new Stack<GenericDataInstance>();

	private DataProvider() {
		//Allowed format of the .csv file is
		//SENSORID, DATA_INSTANCE_NAME1, DATA_INSTANCE_NAME2, ... DATA_INSTANCE_NAMEN
		//1278, 123, 123, abc ... 567
		
		InputStreamReader reader = new InputStreamReader(DataProvider.class.getClassLoader().getResourceAsStream("data.csv"));
		BufferedReader br = new BufferedReader(reader);

		try {
			
			String firstLine = br.readLine();
			String headers[] = firstLine.split(",");
						
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				List<Record> records = new ArrayList<Record>();
				for (int i = 1;i < split.length; i++){
					Record r = new Record(headers[i],split[i]);
					records.add(r);
				}
				GenericDataInstance ginst = new GenericDataInstance (split[0],records);
				
				dataInstances.push(ginst);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public GenericDataInstance getNextInstance() {
		// FIXME: reading data in reverse order
		return this.dataInstances.pop();
	}

}
