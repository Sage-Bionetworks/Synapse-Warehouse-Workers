package org.sagebionetworks.warehouse.workers.db;

import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConnectionPoolImpl implements ConnectionPool {

	Configuration config;
	BasicDataSource datasource;

	@Inject
	public ConnectionPoolImpl(Configuration config) {
		super();
		this.config = config;
		datasource = new BasicDataSource();
		datasource.setDriverClassName(config.getProperty("org.sagebionetworks.jdbc.driver.name"));
		datasource.setDefaultAutoCommit(Boolean.parseBoolean(config.getProperty("org.sagebionetworks.jdbc.default.autocommit")));
		datasource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		datasource.setInitialSize(Integer.parseInt(config.getProperty("org.sagebionetworks.jdbc.pool.size.initial")));
		datasource.setMaxTotal(Integer.parseInt(config.getProperty("org.sagebionetworks.jdbc.pool.size.max")));
		datasource.setMaxIdle(Integer.parseInt(config.getProperty("org.sagebionetworks.jdbc.pool.max.idle.connections")));
		datasource.setUsername(config.getProperty("org.sagebionetworks.jdbc.user.username"));
		datasource.setPassword(config.getProperty("org.sagebionetworks.jdbc.user.password"));
		datasource.setTestOnBorrow(true);
		datasource.setValidationQuery(config.getProperty("org.sagebionetworks.jdbc.validation.query"));
		datasource.setUrl(config.getProperty("org.sagebionetworks.jdbc.connection.url"));
	}
	
	
}
