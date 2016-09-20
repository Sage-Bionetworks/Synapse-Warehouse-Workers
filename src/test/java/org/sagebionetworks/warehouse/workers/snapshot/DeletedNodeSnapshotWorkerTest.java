package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.DeletedNode;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;

public class DeletedNodeSnapshotWorkerTest {

	DeletedNodeSnapshotWorker worker;

	@Before
	public void before() {
		worker = new DeletedNodeSnapshotWorker(null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		DeletedNode deletedNode = new DeletedNode();
		deletedNode.setId("syn123");
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(DeletedNode.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(deletedNode));
		List<DeletedNodeSnapshot> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		assertNull(worker.convert(null));
	}
}
