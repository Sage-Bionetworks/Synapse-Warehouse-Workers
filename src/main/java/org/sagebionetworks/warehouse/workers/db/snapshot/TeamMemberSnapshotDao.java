package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;

public interface TeamMemberSnapshotDao extends SnapshotDao<TeamMemberSnapshot>{

	/**
	 * 
	 * @return an TeamMemberSnapshot given the timestamp, teamId, and memberId
	 */
	public TeamMemberSnapshot get(Long timestamp, Long teamId, Long memberId);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
