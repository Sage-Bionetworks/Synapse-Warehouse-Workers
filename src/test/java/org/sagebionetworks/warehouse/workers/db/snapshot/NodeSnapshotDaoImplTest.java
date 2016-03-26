package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.NodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.NodeSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class NodeSnapshotDaoImplTest {
	NodeSnapshotDao dao = TestContext.singleton().getInstance(NodeSnapshotDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(NodeSnapshotDaoImpl.NODE_SNAPSHOT_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		NodeSnapshot snapshot1 = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		NodeSnapshot snapshot2 = ObjectSnapshotTestUtil.createValidNodeSnapshot();

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), Long.parseLong(snapshot1.getId())));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), Long.parseLong(snapshot2.getId())));

		NodeSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot3.setTimestamp(snapshot2.getTimestamp());
		snapshot3.setId(snapshot2.getId());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(snapshot3.getTimestamp(), Long.parseLong(snapshot3.getId())));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong());
	}
}
