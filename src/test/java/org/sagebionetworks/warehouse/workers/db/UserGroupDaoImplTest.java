package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class UserGroupDaoImplTest {
	UserGroupDao dao = TestContext.singleton().getInstance(UserGroupDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(UserGroupDaoImpl.USER_GROUP_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		UserGroup snapshot1 = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		UserGroup snapshot2 = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(Long.parseLong(snapshot1.getId()), snapshot1.getIsIndividual()));
		assertEquals(snapshot2, dao.get(Long.parseLong(snapshot2.getId()), snapshot2.getIsIndividual()));

		UserGroup snapshot3 = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot3.setId(snapshot2.getId());
		snapshot3.setIsIndividual(snapshot2.getIsIndividual());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(Long.parseLong(snapshot3.getId()), snapshot3.getIsIndividual()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), true);
	}
}
