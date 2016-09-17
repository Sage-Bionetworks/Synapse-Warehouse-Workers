package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;

public class ProcessedAccessRecordWorkerTest {

	ProcessAccessRecordWorker worker;

	@Before
	public void before() {
		worker = new ProcessAccessRecordWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() {
		List<ProcessedAccessRecord> actual = worker.convert(AccessRecordTestUtil.createValidAccessRecord());
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() {
		AccessRecord record = AccessRecordTestUtil.createValidAccessRecord();
		record.setDate(null);
		assertNull(worker.convert(record));
	}
}
