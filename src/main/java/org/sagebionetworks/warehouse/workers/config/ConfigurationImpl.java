package org.sagebionetworks.warehouse.workers.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jdom.JDOMException;
import org.sagebionetworks.warehouse.workers.db.FileState;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
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
	
	public ConfigurationImpl() throws IOException, JDOMException{
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
		// Now override the configuration with the system properties.
		for(String key: System.getProperties().stringPropertyNames()){
			properties.put(key, System.getProperties().get(key));
		}
		// replace the properties from settings
		Properties settings = SettingsLoader.loadSettingsFile();
		if(settings != null){
			for(String key: settings.stringPropertyNames()){
				properties.put(key, settings.get(key));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.config.Configuration#getAWSCredentials()
	 */
	public AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(getProperty("AWS_ACCESS_KEY_ID"), getProperty("AWS_SECRET_KEY"));
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

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.config.Configuration#getDatabaseObjectClassNames()
	 */
	public List<String> getDatabaseObjectClassNames() {
		return databaseObjectClassNames;
	}

}
