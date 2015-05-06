package org.sagebionetworks.warehouse.workers.db;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

public interface DatabaseObject<T> extends PreparedStatementSetter {
	
	/**
	 * Get the create table statement.
	 * @return
	 */
	public String getCreateTableStatement();

	/**
	 * 
	 * @return
	 */
	public String getInsertStatement();
	
	/**
	 * The resulting RowMapper is used to create an object from a database query.
	 * @return
	 */
	public RowMapper<T> getRowMapper();
}
