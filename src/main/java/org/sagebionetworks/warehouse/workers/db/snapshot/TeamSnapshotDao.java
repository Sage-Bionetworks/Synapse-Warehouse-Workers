package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;

public interface TeamSnapshotDao extends SnapshotDao<TeamSnapshot> {

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
