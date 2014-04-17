package container;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import sdapi.IRefreshable;

public class Listener extends Observable implements Runnable {

	private final AbstractApplicationContext context;
	private long timestamp;

	public Listener(AbstractApplicationContext context) {
		this.context = context;
		Resource res = this.context.getResource("classpath:META-INF/wire.xml");
		try {
			this.timestamp = res.lastModified();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		Resource res = this.context.getResource("classpath:META-INF/wire.xml");
		try {
			long temp =  res.lastModified();
			if (this.timestamp != temp) {
				// config changed
				this.context.refresh();
				this.timestamp = temp;
				//TODO: this works well for simple dependency trees. Use the below strategy+delegate (IRefreshable) to update only part of the tree.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers("update");
	}
	public static <T> Map<String, T> extractBeans(Class<T> beanType, List<String> contextXmls, ApplicationContext parentContext) throws Exception {

		List<String> paths = new ArrayList<String>();
		try {
			for (String xml : contextXmls) {
				File file = File.createTempFile("spring", "xml");
				// ... write the file using a utility method
				//FileUtils.writeStringToFile(file, xml, "UTF-8");
				paths.add(file.getAbsolutePath());
			}

			String[] pathArray = paths.toArray(new String[0]);
			return buildContextAndGetBeans(beanType, pathArray, parentContext);

		} finally {
			// ... clean up temp files immediately if desired
		}
	}

	private static <T> Map<String, T> buildContextAndGetBeans(Class<T> beanType, String[] paths, ApplicationContext parentContext) throws Exception {

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(paths, false, parentContext) {
			@Override
			// suppress refresh events bubbling to parent context
			public void publishEvent(ApplicationEvent event) {
			}

			@Override
			protected Resource getResourceByPath(String path) {
				return new FileSystemResource(path); // support absolute paths
			}
		};

		try {
			// avoid classloader errors in some environments
			context.setClassLoader(beanType.getClassLoader());
			context.refresh(); // parse and load context
			Map<String, T> beanMap = context.getBeansOfType(beanType);

			return beanMap;
		} finally {
			try {
				context.close();
			} catch (Exception e) {
				// ... log this
			}
		}
	}

	public void afterPropertiesSet() throws Exception {
		Map<String, IRefreshable> refreshableServices = context.getBeansOfType(IRefreshable.class);
		for (Map.Entry<String, IRefreshable> entry : refreshableServices.entrySet()) {
			Object beanRef = entry.getValue();
			if (beanRef instanceof IRefreshable) {
				IRefreshable refresh = (IRefreshable) beanRef;
				refreshableServices.put(entry.getKey(),refresh);
			}
		}
	}

//	private void updateDependencies() {
//		List<String> definitions = Arrays.asList(xmlDefinition);
//		Map<String, Main> beans = extractBeans(IRefreshable.class, definitions, context);
//		if (beans.size() != 1) {
//			throw new RuntimeException("Invalid number of beans: " + beans.size());
//		}
//		// this.protocol = beans.values().iterator().next();
//	}
}
