package org.sagebionetworks.warehouse.workers.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.inject.Singleton;

@Singleton
public class ConfigurationImpl implements Configuration {

	private static final String CONFIGURATION_PROPERTIES = "configuration.properties";
	Properties properties;
	
	public ConfigurationImpl() throws IOException{
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

}
