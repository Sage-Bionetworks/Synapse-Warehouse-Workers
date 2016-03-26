package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.NodeRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public class NodeSnapshotUtils {

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

}
