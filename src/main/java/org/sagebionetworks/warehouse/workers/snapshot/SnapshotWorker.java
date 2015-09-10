package org.sagebionetworks.warehouse.workers.snapshot;

public interface SnapshotWorker<K,V> {

	/**
	 * Convert record of type K to type V
	 * 
	 * @param record
	 * @return
	 */
	public V convert(K record);
}
