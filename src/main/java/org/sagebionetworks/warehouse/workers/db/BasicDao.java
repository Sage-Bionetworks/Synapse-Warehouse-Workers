package org.sagebionetworks.warehouse.workers.db;

import java.util.ArrayList;



/**
 * A generic Data Access Object (DAO) reader and writer.
 * @author John
 *
 */
public interface BasicDao {
	
	public <T extends DatabaseObject<T>> void update(T databaseObject);
	
	/**
	 * Insert a batch of database objects.
	 * @param databaseObject
	 */
	public <T extends DatabaseObject<T>> void batchUpdate(ArrayList<T> databaseObject);

}
