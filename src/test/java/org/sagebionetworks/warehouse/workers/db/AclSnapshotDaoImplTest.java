package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class AclSnapshotDaoImplTest {
	AclSnapshotDao dao = TestContext.singleton().getInstance(AclSnapshotDao.class);

	@Before
	public void before(){
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		AclSnapshot snapshot1 = ObjectSnapshotTestUtil.createValidAclSnapshot();
		AclSnapshot snapshot2 = ObjectSnapshotTestUtil.createValidAclSnapshot();

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), snapshot1.getOwnerId(), snapshot1.getOwnerType()));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), snapshot2.getOwnerId(), snapshot2.getOwnerType()));

		AclSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot3.setTimestamp(snapshot2.getTimestamp());
		snapshot3.setOwnerId(snapshot2.getOwnerId());
		snapshot3.setOwnerType(snapshot2.getOwnerType());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(snapshot3.getTimestamp(), snapshot3.getOwnerId(), snapshot3.getOwnerType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong(), ObjectType.ENTITY);
	}
}
