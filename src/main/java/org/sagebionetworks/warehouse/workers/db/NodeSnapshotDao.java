package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public interface NodeSnapshotDao extends HasPartitions{

	/**
	 * Insert a batch of NodeSnapshot into NODE_SNAPSHOT table
	 * 
	 * @param batch
	 */
	public void insert(List<NodeSnapshot> batch);

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
