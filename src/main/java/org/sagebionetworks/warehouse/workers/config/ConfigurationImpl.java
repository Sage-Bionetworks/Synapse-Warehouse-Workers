package org.sagebionetworks.warehouse.workers.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.warehouse.workers.bucket.BucketScanningConfigurationProvider;
import org.sagebionetworks.warehouse.workers.db.FileState;

import com.google.inject.Singleton;

@Singleton
public class ConfigurationImpl implements Configuration {

	private static final String CONFIGURATION_PROPERTIES = "configuration.properties";
	Properties properties;
	/**
	 * Add the class name of each database object to this list.
	 */
	List<String> databaseObjectClassNames =  Arrays.asList(
			FileState.class.getName()
	);
	
	ConfigurationImpl() throws IOException, JDOMException{
		// First load the configuration properties.
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(CONFIGURATION_PROPERTIES);
		if(in == null){
			throw new IllegalArgumentException("Cannot find: "+CONFIGURATION_PROPERTIES+" on the classpath");
		}
		try{
			properties.load(in);
		}finally{
			in.close();
		}
		// replace the properties from settings
		Properties settings = SettingsLoader.loadSettingsFile();
		if(settings != null){
			for(String key: settings.stringPropertyNames()){
				properties.put(key, settings.get(key));
			}
		}
		// Now override the configuration with the system properties.
		for(String key: System.getProperties().stringPropertyNames()){
			properties.put(key, System.getProperties().get(key));
		}
		// Link and replace references in values.
		properties = PropertyLinker.linkAndReplace(properties);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.config.Configuration#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {
		if(key == null){
			throw new IllegalArgumentException("Key cannot be null");
		}
		String value = properties.getProperty(key);
		if(value == null){
			throw new IllegalArgumentException("Cannot find environmental property: "+key);
		}
		value = value.trim();
		if("".equals(value)){
			throw new IllegalArgumentException("Cannot property: "+key+" was empty");
		}
		return value;
	}
	
	/**
	 * Add each worker stack interface to this list to add it to the application.
	 * 
	 */
	@Override
	public List<Class<? extends WorkerStackConfigurationProvider>> listAllWorkerStackInterfaces() {
		List<Class<? extends WorkerStackConfigurationProvider>> list = new ArrayList<Class<? extends WorkerStackConfigurationProvider>>();
		// Finds all access record files that need to be processed.
		list.add(BucketScanningConfigurationProvider.class);
		return list;
	}

	@Override
	public DateTime getStartDate() {
		int currentYear = new DateTime().getYear();
		return new DateTime(currentYear - 2, 1, 1, 0, 0);
	}

	@Override
	public DateTime getEndDate() {
		int currentYear = new DateTime().getYear();
		return new DateTime(currentYear + 9, 1, 1, 0, 0);
	}

}
