package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class UserProfileSnapshotDaoImplTest {
	UserProfileSnapshotDao dao = TestContext.singleton().getInstance(UserProfileSnapshotDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(UserProfileSnapshotDaoImpl.USER_PROFILE_SNAPSHOT_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		UserProfileSnapshot snapshot1 = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		UserProfileSnapshot snapshot2 = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), Long.parseLong(snapshot1.getOwnerId())));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), Long.parseLong(snapshot2.getOwnerId())));

		UserProfileSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot3.setTimestamp(snapshot2.getTimestamp());
		snapshot3.setOwnerId(snapshot2.getOwnerId());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(snapshot3.getTimestamp(), Long.parseLong(snapshot3.getOwnerId())));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong());
	}
}
