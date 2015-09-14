package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

public interface SnapshotDao<T> {

	/**
	 * Insert a batch of T record
	 * 
	 * @param batch
	 */
	public void insert(List<T> batch);
}
