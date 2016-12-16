package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.FileDownloadRecord;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;

public class FileHandleDownloadRecordWorkerTest {

	FileHandleDownloadRecordWorker worker;

	@Before
	public void before() {
		worker = new FileHandleDownloadRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileDownloadRecord fdrecord = new FileDownloadRecord();
		fdrecord.setUserId("123");
		FileHandleAssociation fha = new FileHandleAssociation();
		fha.setAssociateObjectId("syn456");
		fha.setAssociateObjectType(FileHandleAssociateType.TableEntity);
		fha.setFileHandleId("999");
		fdrecord.setDownloadedFile(fha);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(fdrecord));
		record.setJsonClassName(FileDownloadRecord.class.getSimpleName().toLowerCase());
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
