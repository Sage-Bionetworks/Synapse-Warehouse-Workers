package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public class ObjectSnapshotUtils {

	/**
	 * 
	 * @param snapshot - the snapshot of a Synapse Node object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidNodeSnapshot(NodeSnapshot snapshot) {
		if (snapshot 							== null) return false;
		if (snapshot.getTimestamp() 			== null) return false;
		if (snapshot.getId() 					== null) return false;
		if (snapshot.getNodeType() 				== null) return false;
		if (snapshot.getCreatedOn() 			== null) return false;
		if (snapshot.getCreatedByPrincipalId() 	== null) return false;
		if (snapshot.getModifiedOn()	 		== null) return false;
		if (snapshot.getModifiedByPrincipalId() == null) return false;
		return true;
	}

	/**
	 * 
	 * @param snapshot - the snapshot of a Synapse Team object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidTeamSnapshot(TeamSnapshot snapshot) {
		if (snapshot 						== null) return false;
		if (snapshot.getTimestamp() 		== null) return false;
		if (snapshot.getId() 				== null) return false;
		if (snapshot.getCreatedOn() 		== null) return false;
		if (snapshot.getCreatedBy() 		== null) return false;
		if (snapshot.getModifiedOn()		== null) return false;
		if (snapshot.getModifiedBy() 		== null) return false;
		if (snapshot.getCanPublicJoin() 	== null) return false;
		return true;
	}

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
	 * 
	 * @param snapshot - the snapshot of a Synapse UserProfile object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidUserProfileSnapshot(UserProfileSnapshot snapshot) {
		if (snapshot 					== null) return false;
		if (snapshot.getTimestamp() 	== null) return false;
		if (snapshot.getOwnerId() 		== null) return false;
		if (snapshot.getUserName() 		== null) return false;
		return true;
	}

	/**
	 * Extract node's information and build NodeSnapshot from the captured record
	 * 
	 * @param record
	 * @return the NodeSnapshot,
	 *         or null if fails to get the snapshot
	 */
	public static NodeSnapshot getNodeSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(Node.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			Node node = EntityFactory.createEntityFromJSONString(record.getJsonString(), Node.class);
			NodeSnapshot snapshot = new NodeSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setId(node.getId());
			snapshot.setBenefactorId(node.getBenefactorId());
			snapshot.setProjectId(node.getProjectId());
			snapshot.setParentId(node.getParentId());
			snapshot.setNodeType(node.getNodeType());
			snapshot.setCreatedOn(node.getCreatedOn());
			snapshot.setCreatedByPrincipalId(node.getCreatedByPrincipalId());
			snapshot.setModifiedOn(node.getModifiedOn());
			snapshot.setModifiedByPrincipalId(node.getModifiedByPrincipalId());
			snapshot.setVersionNumber(node.getVersionNumber());
			snapshot.setFileHandleId(node.getFileHandleId());
			snapshot.setName(node.getName());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			return null;
		}
	}

	/**
	 * Extract team's information and build TeamSnapshot from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static TeamSnapshot getTeamSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(Team.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			Team team = EntityFactory.createEntityFromJSONString(record.getJsonString(), Team.class);
			TeamSnapshot snapshot = new TeamSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setId(team.getId());
			snapshot.setCreatedOn(team.getCreatedOn());
			snapshot.setCreatedBy(team.getCreatedBy());
			snapshot.setModifiedOn(team.getModifiedOn());
			snapshot.setModifiedBy(team.getModifiedBy());
			snapshot.setName(team.getName());
			snapshot.setCanPublicJoin(team.getCanPublicJoin());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			return null;
		}
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
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Extract user profile's information and build UserProfileSnapshot from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static UserProfileSnapshot getUserProfileSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(UserProfile.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			UserProfile profile = EntityFactory.createEntityFromJSONString(record.getJsonString(), UserProfile.class);
			UserProfileSnapshot snapshot = new UserProfileSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setOwnerId(profile.getOwnerId());
			snapshot.setUserName(profile.getUserName());
			snapshot.setFirstName(profile.getFirstName());
			snapshot.setLastName(profile.getLastName());
			snapshot.setEmail(profile.getEmail());
			snapshot.setLocation(profile.getLocation());
			snapshot.setCompany(profile.getCompany());
			snapshot.setPosition(profile.getPosition());
			return snapshot;
		} catch (Exception e) {
			return null;
		}
	}
}
