package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;

public class UserActivityPerClientPerDayWorkerTest {

	UserActivityPerClientPerDayWorker worker;

	@Before
	public void before() {
		worker = new UserActivityPerClientPerDayWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		List<UserActivityPerClientPerDay> actual = worker.convert(AccessRecordTestUtil.createValidAccessRecord());
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		AccessRecord record = AccessRecordTestUtil.createValidAccessRecord();
		record.setTimestamp(null);
		assertNull(worker.convert(record));
	}
}
