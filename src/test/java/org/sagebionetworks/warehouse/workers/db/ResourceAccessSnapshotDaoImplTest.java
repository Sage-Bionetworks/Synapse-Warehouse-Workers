package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.model.ResourceAccessSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class ResourceAccessSnapshotDaoImplTest {
	ResourceAccessSnapshotDao dao = TestContext.singleton().getInstance(ResourceAccessSnapshotDao.class);

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
		ResourceAccessSnapshot snapshot1 = ObjectSnapshotTestUtil.createValidResourceAccessSnapshot();
		ResourceAccessSnapshot snapshot2 = ObjectSnapshotTestUtil.createValidResourceAccessSnapshot();
		System.out.println(snapshot1.toString());

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), snapshot1.getOwnerId(), snapshot1.getOwnerType(), snapshot1.getPrincipalId(), snapshot1.getAccessType()));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), snapshot2.getOwnerId(), snapshot2.getOwnerType(), snapshot2.getPrincipalId(), snapshot2.getAccessType()));

		ResourceAccessSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidResourceAccessSnapshot();
		snapshot3.setTimestamp(snapshot2.getTimestamp());
		snapshot3.setOwnerId(snapshot2.getOwnerId());
		snapshot3.setOwnerType(snapshot2.getOwnerType());
		snapshot3.setPrincipalId(snapshot2.getPrincipalId());
		snapshot3.setAccessType(snapshot2.getAccessType());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(snapshot3.getTimestamp(), snapshot3.getOwnerId(), snapshot3.getOwnerType(), snapshot3.getPrincipalId(), snapshot3.getAccessType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong(), ObjectType.ENTITY, new Random().nextLong(), ACCESS_TYPE.READ);
	}
}
