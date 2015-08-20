package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.Node;
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
		if (!record.getJsonClassName().equals(Node.class.getSimpleName().toLowerCase())) {
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
}
