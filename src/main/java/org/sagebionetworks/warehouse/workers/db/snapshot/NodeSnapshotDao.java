package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public interface NodeSnapshotDao extends HasPartitions, SnapshotDao<NodeSnapshot>{

	/**
	 * 
	 * @return an NodeSnapshot given the timestamp and nodeId
	 */
	public NodeSnapshot get(Long timestamp, Long nodeId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
