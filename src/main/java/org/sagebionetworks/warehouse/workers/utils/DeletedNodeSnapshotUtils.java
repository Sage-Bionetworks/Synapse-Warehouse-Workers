package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.DeletedNode;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;

public class DeletedNodeSnapshotUtils {

	/**
	 * 
	 * @param snapshot - the snapshot of a DeletedNodeSnapshot object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidDeletedNodeSnapshot(DeletedNodeSnapshot snapshot) {
		if (snapshot 				== null) return false;
		if (snapshot.getId() 		== 0L) return false;
		if (snapshot.getTimestamp() == 0L) return false;
		return true;
	}

	/**
	 * Extract deleted node's information and build DeletedNodeSnapshot from the captured record
	 * 
	 * @param record
	 * @return the DeletedNodeSnapshot,
	 *         or null if fails to get the snapshot
	 */
	public static DeletedNodeSnapshot getDeletedNodeSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(DeletedNode.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			DeletedNode deletedNode = EntityFactory.createEntityFromJSONString(record.getJsonString(), DeletedNode.class);
			DeletedNodeSnapshot snapshot = new DeletedNodeSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setId(ObjectSnapshotUtils.convertSynapseIdToLong(deletedNode.getId()));
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}

}
