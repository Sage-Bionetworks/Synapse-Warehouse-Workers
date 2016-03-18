package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public interface UserProfileSnapshotDao extends SnapshotDao<UserProfileSnapshot>{

	/**
	 * 
	 * @return an UserProfileSnapshot given the timestamp and userId
	 */
	public UserProfileSnapshot get(Long timestamp, Long userId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
