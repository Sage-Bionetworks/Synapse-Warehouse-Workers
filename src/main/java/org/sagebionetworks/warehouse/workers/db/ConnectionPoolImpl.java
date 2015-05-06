package org.sagebionetworks.warehouse.workers.db;

import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.ConnectionPool#createTempalte()
	 */
	public JdbcTemplate createTempalte() {
		// Connect a new instance to the pool.
		return new JdbcTemplate(datasource);
	}
	
	
}
