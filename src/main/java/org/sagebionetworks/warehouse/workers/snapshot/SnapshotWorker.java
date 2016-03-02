package org.sagebionetworks.warehouse.workers.snapshot;

import java.util.List;

public interface SnapshotWorker<K,V> {

	/**
	 * Convert record of type K to type V
	 * 
	 * @param record
	 * @return
	 */
	public List<V> convert(K record);
}
