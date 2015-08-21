package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.model.ResourceAccessSnapshot;

public interface ResourceAccessSnapshotDao {

	/**
	 * Insert a batch of ResourceAccessSnapshot into RESOURCE_ACCESS_SNAPSHOT table
	 * 
	 * @param batch
	 */
	public void insert(List<ResourceAccessSnapshot> batch);

	/**
	 * 
	 * @return an ResourceAccessSnapshot given the timestamp, ownerId, ownerType, principalId, and accessType
	 */
	public ResourceAccessSnapshot get(Long timestamp, Long ownerId, ObjectType ownerType, Long principalId, ACCESS_TYPE accessType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
