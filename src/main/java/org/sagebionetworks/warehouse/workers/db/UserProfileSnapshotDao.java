package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public interface UserProfileSnapshotDao {

	/**
	 * Insert a batch of UserProfileSnapshot into USER_PROFILE_SNAPSHOT table
	 * 
	 * @param batch
	 */
	public void insert(List<UserProfileSnapshot> batch);

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
