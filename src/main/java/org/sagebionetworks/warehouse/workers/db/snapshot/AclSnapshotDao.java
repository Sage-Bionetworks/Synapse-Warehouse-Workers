package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;

public interface AclSnapshotDao extends SnapshotDao<AclSnapshot>{

	/**
	 * 
	 * @return an AclSnapshot given the timestamp, ownerId, and ownerType
	 */
	public AclSnapshot get(Long timestamp, Long ownerId, ObjectType ownerType);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
