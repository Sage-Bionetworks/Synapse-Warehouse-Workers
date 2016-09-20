package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;

public interface DeletedNodeSnapshotDao extends HasPartitions, SnapshotDao<DeletedNodeSnapshot>{

	/**
	 * 
	 * @return a DeletedNodeSnapshot given the timestamp and nodeId
	 */
	public DeletedNodeSnapshot get(Long timestamp, Long nodeId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
