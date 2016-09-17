package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;

public class TeamMemberSnapshotWorkerTest {

	TeamMemberSnapshotWorker worker;

	@Before
	public void before() {
		worker = new TeamMemberSnapshotWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		List<TeamMemberSnapshot> actual = worker.convert(ObjectSnapshotTestUtil.createValidTeamMemberObjectRecord());
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		ObjectRecord record = ObjectSnapshotTestUtil.createValidTeamMemberObjectRecord();
		record.setTimestamp(null);
		assertNull(worker.convert(record));
	}
}
