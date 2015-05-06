package org.sagebionetworks.warehouse.workers.db;

import org.springframework.jdbc.core.JdbcTemplate;


public interface ConnectionPool {
	
	/**
	 * Create a thread-safe template connected to the connection pool.
	 * @return
	 */
	public JdbcTemplate createTempalte();
	

}
