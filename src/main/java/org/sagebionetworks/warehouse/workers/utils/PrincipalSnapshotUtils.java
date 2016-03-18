package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public class PrincipalSnapshotUtils {

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
	public static boolean isValidUserGroupSnapshot(UserGroup snapshot) {
		if (snapshot 					== null) return false;
		if (snapshot.getId() 			== null) return false;
		if (snapshot.getIsIndividual() 	== null) return false;
		if (snapshot.getCreationDate() 	== null) return false;
		return true;
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

}
