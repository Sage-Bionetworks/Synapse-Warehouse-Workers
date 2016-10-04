package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class FileHandleSnapshotUtils {

	/**
	 * 
	 * @param record - the FileHandle snapshot
	 * @return true is the record contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidFileHandleSnapshot(FileHandleSnapshot record) {
		if (record 					== null) return false;
		if (record.getId() 			== null) return false;
		if (record.getCreatedOn() 	== null) return false;
		if (record.getCreatedBy()	== null) return false;
		if (record.getConcreteType() == null) return false;
		if (record.getFileName() 	== null) return false;
		return true;
	}

	/**
	 * Extract FileHandle record information from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static FileHandleSnapshot getFileHandleSnapshot(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(FileHandleSnapshot.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			return EntityFactory.createEntityFromJSONString(record.getJsonString(), FileHandleSnapshot.class);
		} catch (JSONObjectAdapterException e) {
			throw new RuntimeException(e);
		}
	}
}
