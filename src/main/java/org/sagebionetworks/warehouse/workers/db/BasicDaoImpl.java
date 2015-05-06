package org.sagebionetworks.warehouse.workers.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.inject.Inject;

public class BasicDaoImpl implements BasicDao {
	
	JdbcTemplate template;
	
	Map<Class, String> classToUpdate;
	
	@Inject
	public BasicDaoImpl(ConnectionPool pool, Configuration config){
		template = pool.createTempalte();
		// Map each database class
		try {
			classToUpdate = new HashMap<Class, String>(config.getDatabaseObjectClassNames().size());
			for(String name: config.getDatabaseObjectClassNames()){
				DatabaseObject object = (DatabaseObject) Class.forName(name).newInstance();
				// Create the table
				String tableDDL = object.getCreateTableStatement();
				if(tableDDL == null){
					throw new IllegalArgumentException("Table create statement was null for: "+name);
				}
				template.update(tableDDL);
				String insert = object.getInsertStatement();
				if(insert == null){
					throw new IllegalArgumentException("Insert statement was null for: "+name);
				}
				classToUpdate.put(object.getClass(), insert);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}


	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.BasicDao#update(org.sagebionetworks.warehouse.workers.db.DatabaseObject)
	 */
	public <T extends DatabaseObject<T>> void update(T databaseObject) {
		if(databaseObject == null){
			throw new IllegalArgumentException("DatabaesObject list cannot be null");
		}
		String insert = classToUpdate.get(databaseObject.getClass());
		if(insert == null){
			throw new IllegalArgumentException("Cannot find insert statement for: "+databaseObject.getClass().getName());
		}
		template.update(insert, databaseObject);
	}


	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.BasicDao#batchUpdate(java.util.ArrayList)
	 */
	public <T extends DatabaseObject<T>> void batchUpdate(final ArrayList<T> list) {
		if(list == null){
			throw new IllegalArgumentException("DatabaesObject list cannot be null");
		}
		if(list.isEmpty()){
			throw new IllegalArgumentException("DatabaesObject list cannot be e");
		}
		DatabaseObject first = list.get(0);
		String insert = classToUpdate.get(first.getClass());
		if(insert == null){
			throw new IllegalArgumentException("Cannot find insert statement for: "+first.getClass().getName());
		}
		// do the batch update
		template.batchUpdate(insert, new BatchPreparedStatementSetter() {
			
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				list.get(i).setValues(ps);
			}
			
			public int getBatchSize() {
				return list.size();
			}
		});
	}
}
