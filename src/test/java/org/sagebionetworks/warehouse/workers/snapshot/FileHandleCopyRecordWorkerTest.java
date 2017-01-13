package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.file.FileHandleAssociation;
import org.sagebionetworks.repo.model.file.FileHandleCopyRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;

public class FileHandleCopyRecordWorkerTest {

	FileHandleCopyRecordWorker worker;

	@Before
	public void before() {
		worker = new FileHandleCopyRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleCopyRecord copyRecord = new FileHandleCopyRecord();
		copyRecord.setUserId("1");
		copyRecord.setNewFileHandleId("3");
		FileHandleAssociation originalFileHandle = new FileHandleAssociation();
		originalFileHandle.setFileHandleId("2");
		originalFileHandle.setAssociateObjectId("4");
		originalFileHandle.setAssociateObjectType(FileHandleAssociateType.FileEntity);
		copyRecord.setOriginalFileHandle(originalFileHandle );
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(copyRecord));
		record.setJsonClassName(FileHandleCopyRecord.class.getSimpleName().toLowerCase());
		List<FileHandleCopyRecordSnapshot> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		assertNull(worker.convert(record));
	}
}
