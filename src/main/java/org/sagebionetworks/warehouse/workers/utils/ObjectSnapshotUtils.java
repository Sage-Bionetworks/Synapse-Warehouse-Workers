package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.NodeRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.quiz.PassingRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
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
		if (snapshot.getIsPublic() 				== null) return false;
		if (snapshot.getIsControlled() 			== null) return false;
		if (snapshot.getIsRestricted() 			== null) return false;
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
		return true;
	}

	/**
	 * 
	 * @param snapshot
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidAclSnapshot(AclSnapshot snapshot) {
		if (snapshot 					== null) return false;
		if (snapshot.getTimestamp() 	== null) return false;
		if (snapshot.getId() 			== null) return false;
		if (snapshot.getOwnerType() 	== null) return false;
		return true;
	}

	/**
	 * 
	 * @param snapshot
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidUserGroupSnapshot(UserGroup snapshot) {
		if (snapshot 					== null) return false;
		if (snapshot.getId() 			== null) return false;
		if (snapshot.getIsIndividual() 	== null) return false;
		if (snapshot.getCreationDate() 	== null) return false;
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
				!record.getJsonClassName().equals(NodeRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			NodeRecord node = EntityFactory.createEntityFromJSONString(record.getJsonString(), NodeRecord.class);
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
			snapshot.setIsPublic(node.getIsPublic());
			snapshot.setIsControlled(node.getIsControlled());
			snapshot.setIsRestricted(node.getIsRestricted());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException(e);
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
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		} catch (NumberFormatException e) {
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
			if (profile.getEmails() != null) {
				snapshot.setEmail(profile.getEmails().get(0));
			}
			snapshot.setLocation(profile.getLocation());
			snapshot.setCompany(profile.getCompany());
			snapshot.setPosition(profile.getPosition());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			return null;
		}
	}

	/**
	 * Extract ACL record's information and build AclSnapshot from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static AclSnapshot getAclSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(AclRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			AclRecord acl = EntityFactory.createEntityFromJSONString(record.getJsonString(), AclRecord.class);
			AclSnapshot snapshot = new AclSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setCreationDate(acl.getCreationDate());
			snapshot.setId(acl.getId());
			snapshot.setOwnerType(acl.getOwnerType());
			snapshot.setResourceAccess(acl.getResourceAccess());
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extract UserGroup record's information
	 * 
	 * @param record
	 * @return
	 */
	public static UserGroup getUserGroupSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(UserGroup.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			return EntityFactory.createEntityFromJSONString(record.getJsonString(), UserGroup.class);
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param synId
	 * @return
	 */
	public static long convertSynapseIdToLong(String synId) {
		if (synId == null)
			throw new IllegalArgumentException();
		synId = synId.trim().toLowerCase();
		if (synId.startsWith("syn"))
			synId = synId.substring(3);
		return Long.parseLong(synId);
	}

	public static CertifiedQuizRecord getCertifiedQuizRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(PassingRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			PassingRecord passingRecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), PassingRecord.class);
			CertifiedQuizRecord certifiedQuizRecord = new CertifiedQuizRecord();
			certifiedQuizRecord.setResponseId(passingRecord.getResponseId());
			certifiedQuizRecord.setUserId(Long.parseLong(passingRecord.getUserId()));
			certifiedQuizRecord.setPassed(passingRecord.getPassed());
			certifiedQuizRecord.setPassedOn(passingRecord.getPassedOn().getTime());
			return certifiedQuizRecord;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidCertifiedQuizRecord(CertifiedQuizRecord record) {
		if (record.getResponseId() 		== null) return false;
		if (record.getUserId() 			== null) return false;
		if (record.getPassed() 			== null) return false;
		if (record.getPassedOn() 		== null) return false;
		return true;
	}
}
