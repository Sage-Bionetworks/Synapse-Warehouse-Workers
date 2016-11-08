package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.BulkFileDownloadResponse;
import org.sagebionetworks.repo.model.file.FileDownloadStatus;
import org.sagebionetworks.repo.model.file.FileDownloadSummary;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;

public class BulkFileHandleDownloadRecordWorkerTest {

	BulkFileHandleDownloadRecordWorker worker;

	@Before
	public void before() {
		worker = new BulkFileHandleDownloadRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		BulkFileDownloadResponse response = new BulkFileDownloadResponse();
		response.setUserId("123");
		FileDownloadSummary fileDownloadSummary = new FileDownloadSummary();
		fileDownloadSummary.setAssociateObjectId("syn456");
		fileDownloadSummary.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fileDownloadSummary.setFileHandleId("999");
		fileDownloadSummary.setStatus(FileDownloadStatus.SUCCESS);
		List<FileDownloadSummary> fileDownloadSummaryList = Arrays.asList(fileDownloadSummary);
		response.setFileSummary(fileDownloadSummaryList);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(response));
		record.setJsonClassName(BulkFileDownloadResponse.class.getSimpleName().toLowerCase());
		List<FileHandleDownload> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		assertNull(worker.convert(record));
	}
}
