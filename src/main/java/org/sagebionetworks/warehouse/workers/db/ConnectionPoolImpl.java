package org.sagebionetworks.warehouse.workers.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.config.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConnectionPoolImpl implements ConnectionPool {

	private static final Logger log = LogManager.getLogger(ConnectionPoolImpl.class
			.getName());
	
	Configuration config;
	BasicDataSource datasource;

	@Inject
	public ConnectionPoolImpl(Configuration config) {
		super();
		this.config = config;
		log.info("Starting database connection pool...");
		datasource = new BasicDataSource();
		datasource.setDriverClassName(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.driver.name"));
		datasource.setDefaultAutoCommit(Boolean.parseBoolean(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.default.autocommit")));
		datasource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		datasource.setInitialSize(Integer.parseInt(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.pool.size.initial")));
		datasource.setMaxTotal(Integer.parseInt(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.pool.size.max")));
		datasource.setMaxIdle(Integer.parseInt(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.pool.max.idle.connections")));
		datasource.setUsername(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.user.username"));
		datasource.setPassword(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.user.password"));
		datasource.setTestOnBorrow(true);
		datasource.setValidationQuery(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.validation.query"));
		datasource.setUrl(config.getProperty("org.sagebionetworks.warehouse.workers.jdbc.connection.url"));
		log.info("Connecting to "+datasource.getUrl());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.ConnectionPool#getDataSource()
	 */
	public DataSource getDataSource() {
		return datasource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.ConnectionPool#close()
	 */
	public void close() {
		try {
			log.info("Shutting down database connection pool..");
			datasource.close();
		} catch (SQLException e) {
			log.error("Failed to shut down the connection pool", e);
		}
	}
	
	
}
