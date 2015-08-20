package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;

public interface TeamMemberSnapshotDao {

	/**
	 * Insert a batch of TeamMemberSnapshot into TEAM_MEMBER_SNAPSHOT table
	 * 
	 * @param batch
	 */
	public void insert(List<TeamMemberSnapshot> batch);

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
