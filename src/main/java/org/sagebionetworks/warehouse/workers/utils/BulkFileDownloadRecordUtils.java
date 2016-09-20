package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.BulkFileDownloadResponse;
import org.sagebionetworks.repo.model.file.FileDownloadStatus;
import org.sagebionetworks.repo.model.file.FileDownloadSummary;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;

public class BulkFileDownloadRecordUtils {

	/**
	 * 
	 * @param record - the download record
	 * @return true is the record contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidBulkFileDownloadRecord(BulkFileDownloadRecord record) {
		if (record 						== null) return false;
		if (record.getUserId() 			== null) return false;
		if (record.getObjectId() 		== null) return false;
		if (record.getObjectType()		== null) return false;
		return true;
	}

	/**
	 * Extract download record information and build BulkFileDownloadRecord from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static List<BulkFileDownloadRecord> getBulkFileDownloadRecords(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(BulkFileDownloadResponse.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			BulkFileDownloadResponse response = EntityFactory.createEntityFromJSONString(record.getJsonString(), BulkFileDownloadResponse.class);
			Set<BulkFileDownloadRecord> records = new HashSet<BulkFileDownloadRecord>();
			for (FileDownloadSummary fileSummary : response.getFileSummary()) {
				if (fileSummary.getStatus() != FileDownloadStatus.SUCCESS) {
					continue;
				}
				BulkFileDownloadRecord downloadRecord = new BulkFileDownloadRecord();
				downloadRecord.setUserId(Long.parseLong(response.getUserId()));
				downloadRecord.setObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(fileSummary.getAssociateObjectId()));
				downloadRecord.setObjectType(fileSummary.getAssociateObjectType());
				records.add(downloadRecord);
			}
			return new ArrayList<BulkFileDownloadRecord>(records);
		} catch (JSONObjectAdapterException | NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidBulkFileDownloadRecords(List<BulkFileDownloadRecord> records) {
		if (records == null) {
			return false;
		}
		for (BulkFileDownloadRecord record : records) {
			if (!isValidBulkFileDownloadRecord(record)) {
				return false;
			}
		}
		return true;
	}

}
