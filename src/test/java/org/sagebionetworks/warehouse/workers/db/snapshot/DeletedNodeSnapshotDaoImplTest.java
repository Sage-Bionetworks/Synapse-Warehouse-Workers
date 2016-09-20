package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;
import org.springframework.dao.EmptyResultDataAccessException;

public class DeletedNodeSnapshotDaoImplTest {
	DeletedNodeSnapshotDao dao = TestContext.singleton().getInstance(DeletedNodeSnapshotDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(DeletedNodeSnapshotDaoImpl.DELETED_NODE_SNAPSHOT_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		DeletedNodeSnapshot snapshot1 = new DeletedNodeSnapshot();
		snapshot1.setId(1L);
		Long timestamp1 = System.currentTimeMillis();
		snapshot1.setTimestamp(timestamp1);
		DeletedNodeSnapshot snapshot2 = new DeletedNodeSnapshot();
		snapshot2.setId(2L);
		Long timestamp2 = System.currentTimeMillis();
		snapshot2.setTimestamp(timestamp2);

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(timestamp1, 1L));
		assertEquals(snapshot2, dao.get(timestamp2, 2L));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong());
	}
}
