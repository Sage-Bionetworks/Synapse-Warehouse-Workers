package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;

public class AclSnapshotUtils {

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

}
