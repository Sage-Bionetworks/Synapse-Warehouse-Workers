package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.BulkFileDownloadResponse;
import org.sagebionetworks.repo.model.file.FileDownloadRecord;
import org.sagebionetworks.repo.model.file.FileDownloadStatus;
import org.sagebionetworks.repo.model.file.FileDownloadSummary;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileDownload;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;

public class FileDownloadRecordUtils {

	/**
	 * 
	 * @param record - the download record
	 * @return true is the record contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidFileDownloadRecord(FileDownload record) {
		if (record 								== null) return false;
		if (record.getTimestamp() 				== null) return false;
		if (record.getUserId() 					== null) return false;
		if (record.getFileHandleId() 			== null) return false;
		if (record.getAssociationObjectId() 	== null) return false;
		if (record.getAssociationObjectType()	== null) return false;
		return true;
	}

	/**
	 * Extract download record information and build FileDownload object from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static List<FileDownload> getFileDownloadRecordsForBulkFileDownloadRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(BulkFileDownloadResponse.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			BulkFileDownloadResponse response = EntityFactory.createEntityFromJSONString(record.getJsonString(), BulkFileDownloadResponse.class);
			List<FileDownload> records = new ArrayList<FileDownload>();
			for (FileDownloadSummary fileSummary : response.getFileSummary()) {
				if (fileSummary.getStatus() != FileDownloadStatus.SUCCESS) {
					continue;
				}
				FileDownload downloadRecord = new FileDownload();
				downloadRecord.setTimestamp(record.getTimestamp());
				downloadRecord.setUserId(Long.parseLong(response.getUserId()));
				downloadRecord.setFileHandleId(Long.parseLong(fileSummary.getFileHandleId()));
				downloadRecord.setAssociationObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(fileSummary.getAssociateObjectId()));
				downloadRecord.setAssociationObjectType(fileSummary.getAssociateObjectType());
				records.add(downloadRecord);
			}
			return records;
		} catch (JSONObjectAdapterException | NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extract download record information and build FileDownload object from the captured record
	 * 
	 * @param record
	 * @return
	 */
	public static List<FileDownload> getFileDownloadRecordsForFileDownloadRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(FileDownloadRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			FileDownloadRecord fdrecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), FileDownloadRecord.class);
			FileDownload downloadRecord = new FileDownload();
			downloadRecord.setTimestamp(record.getTimestamp());
			downloadRecord.setUserId(Long.parseLong(fdrecord.getUserId()));
			downloadRecord.setFileHandleId(Long.parseLong(fdrecord.getDownloadedFile().getFileHandleId()));
			downloadRecord.setAssociationObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(fdrecord.getDownloadedFile().getAssociateObjectId()));
			downloadRecord.setAssociationObjectType(fdrecord.getDownloadedFile().getAssociateObjectType());
			return Arrays.asList(downloadRecord);
		} catch (JSONObjectAdapterException | NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidFileDownloadRecords(List<FileDownload> records) {
		if (records == null) {
			return false;
		}
		for (FileDownload record : records) {
			if (!isValidFileDownloadRecord(record)) {
				return false;
			}
		}
		return true;
	}

	public static List<FileHandleDownload> getFileHandleDownloadRecordsForBulkFileDownloadRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(BulkFileDownloadResponse.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			BulkFileDownloadResponse response = EntityFactory.createEntityFromJSONString(record.getJsonString(), BulkFileDownloadResponse.class);
			List<FileHandleDownload> records = new ArrayList<FileHandleDownload>();
			for (FileDownloadSummary fileSummary : response.getFileSummary()) {
				if (fileSummary.getStatus() != FileDownloadStatus.SUCCESS) {
					continue;
				}
				FileHandleDownload downloadRecord = new FileHandleDownload();
				downloadRecord.setTimestamp(record.getTimestamp());
				downloadRecord.setUserId(Long.parseLong(response.getUserId()));
				downloadRecord.setDownloadedFileHandleId(Long.parseLong(response.getResultZipFileHandleId()));
				downloadRecord.setRequestedFileHandleId(Long.parseLong(fileSummary.getFileHandleId()));
				downloadRecord.setAssociationObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(fileSummary.getAssociateObjectId()));
				downloadRecord.setAssociationObjectType(fileSummary.getAssociateObjectType());
				records.add(downloadRecord);
			}
			return records;
		} catch (JSONObjectAdapterException | NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<FileHandleDownload> getFileHandleDownloadRecordsForFileDownloadRecord(ObjectRecord record) {
		if (record == null || 
				record.getTimestamp() == null ||
				record.getJsonString() == null ||
				record.getJsonClassName() == null || 
				!record.getJsonClassName().equals(FileDownloadRecord.class.getSimpleName().toLowerCase())) {
			return null;
		}
		try {
			FileDownloadRecord fdrecord = EntityFactory.createEntityFromJSONString(record.getJsonString(), FileDownloadRecord.class);
			FileHandleDownload downloadRecord = new FileHandleDownload();
			downloadRecord.setTimestamp(record.getTimestamp());
			downloadRecord.setUserId(Long.parseLong(fdrecord.getUserId()));
			downloadRecord.setDownloadedFileHandleId(Long.parseLong(fdrecord.getDownloadedFile().getFileHandleId()));
			downloadRecord.setRequestedFileHandleId(Long.parseLong(fdrecord.getDownloadedFile().getFileHandleId()));
			downloadRecord.setAssociationObjectId(ObjectSnapshotUtils.convertSynapseIdToLong(fdrecord.getDownloadedFile().getAssociateObjectId()));
			downloadRecord.setAssociationObjectType(fdrecord.getDownloadedFile().getAssociateObjectType());
			return Arrays.asList(downloadRecord);
		} catch (JSONObjectAdapterException | NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isValidFileHandleDownloadRecords(List<FileHandleDownload> records) {
		if (records == null) {
			return false;
		}
		for (FileHandleDownload record : records) {
			if (!isValidFileHandleDownloadRecord(record)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidFileHandleDownloadRecord(FileHandleDownload record) {
		if (record 								== null) return false;
		if (record.getTimestamp() 				== null) return false;
		if (record.getUserId() 					== null) return false;
		if (record.getDownloadedFileHandleId() 	== null) return false;
		if (record.getRequestedFileHandleId() 	== null) return false;
		if (record.getAssociationObjectId() 	== null) return false;
		if (record.getAssociationObjectType()	== null) return false;
		return true;
	}

}
