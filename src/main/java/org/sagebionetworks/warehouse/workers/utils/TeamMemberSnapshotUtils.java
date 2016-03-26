package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;

public class TeamMemberSnapshotUtils {

	/**
	 * 
	 * @param snapshot - the snapshot of a Synapse TeamMember object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidTeamMemberSnapshot(TeamMemberSnapshot snapshot) {
		if (snapshot 					== null) return false;
		if (snapshot.getTimestamp() 	== null) return false;
		if (snapshot.getTeamId() 		== null) return false;
		if (snapshot.getMemberId() 		== null) return false;
		return true;
	}

	/**
	 * Extract team member's information and build TeamMemberSnapshot from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static TeamMemberSnapshot getTeamMemberSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(TeamMember.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			TeamMember teamMember = EntityFactory.createEntityFromJSONString(record.getJsonString(), TeamMember.class);
			TeamMemberSnapshot snapshot = new TeamMemberSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setTeamId(Long.parseLong(teamMember.getTeamId()));
			snapshot.setMemberId(Long.parseLong(teamMember.getMember().getOwnerId()));
			snapshot.setIsAdmin(teamMember.getIsAdmin());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
