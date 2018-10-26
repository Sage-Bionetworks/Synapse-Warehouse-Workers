package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;

public class NodeSnapshotWorkerTest {

	NodeSnapshotWorker worker;

	@Before
	public void before() {
		worker = new NodeSnapshotWorker(null, null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
		List<NodeSnapshot> actual = worker.convert(ObjectSnapshotTestUtil.createValidNodeObjectRecord());
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		worker.convert(null);
	}
}
