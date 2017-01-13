package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.FileHandleCopyRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;

public class FileHandleCopyRecordUtils {

	/**
	 * 
	 * @param record - the FileHandleCopyRecordSnapshot snapshot
	 * @return true is the record contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidFileHandleCopyRecordSnapshot(FileHandleCopyRecordSnapshot record) {
		if (record 								== null) return false;
		if (record.getAssociationObjectType() 	== null) return false;
		return true;
	}

	/**
	 * Extract FileHandleCopyRecord record information from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static FileHandleCopyRecordSnapshot getFileHandleCopyRecordSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(FileHandleCopyRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			FileHandleCopyRecord copyRecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), FileHandleCopyRecord.class);
			FileHandleCopyRecordSnapshot snapshot = new FileHandleCopyRecordSnapshot();
			snapshot.setTimestamp(record.getTimestamp());
			snapshot.setUserId(Long.parseLong(copyRecord.getUserId()));
			snapshot.setOriginalFileHandleId(Long.parseLong(copyRecord.getOriginalFileHandle().getFileHandleId()));
			snapshot.setAssociationObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(copyRecord.getOriginalFileHandle().getAssociateObjectId()));
			snapshot.setAssociationObjectType(copyRecord.getOriginalFileHandle().getAssociateObjectType());
			snapshot.setNewFileHandleId(Long.parseLong(copyRecord.getNewFileHandleId()));
			return snapshot;
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}
}
