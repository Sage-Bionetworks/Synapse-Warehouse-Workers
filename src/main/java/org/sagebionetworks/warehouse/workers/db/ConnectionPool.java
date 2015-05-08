package org.sagebionetworks.warehouse.workers.db;

import javax.sql.DataSource;


public interface ConnectionPool {
	
	/**
	 * Get the DataSource that wraps the connection pool.
	 * @return
	 */
	public DataSource getDataSource();
	
	
	/**
	 * Close the connection pool on shutdown.
	 */
	public void close();

}
