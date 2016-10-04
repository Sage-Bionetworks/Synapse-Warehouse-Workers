package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;

public class FileHandleRecordWorkerTest {

	FileHandleRecordWorker worker;

	@Before
	public void before() {
		worker = new FileHandleRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		FileHandleSnapshot snapshot = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(snapshot));
		record.setJsonClassName(FileHandleSnapshot.class.getSimpleName().toLowerCase());
		List<FileHandleSnapshot> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		assertNull(worker.convert(record));
	}
}
