package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;

public interface TeamSnapshotDao {
	/**
	 * Insert a batch of TeamSnapshot into TEAM_SNAPSHOT table
	 * 
	 * @param batch
	 */
	public void insert(List<TeamSnapshot> batch);

	/**
	 * 
	 * @return an TeamSnapshot given the timestamp and teamId
	 */
	public TeamSnapshot get(Long timestamp, Long teamId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
