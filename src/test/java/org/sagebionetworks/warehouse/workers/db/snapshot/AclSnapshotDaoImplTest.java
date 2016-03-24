package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class AclSnapshotDaoImplTest {
	AclSnapshotDao dao = TestContext.singleton().getInstance(AclSnapshotDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(AclSnapshotDaoImpl.ACL_SNAPSHOT_DDL_SQL);
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
		snapshot2.setResourceAccess(new HashSet<ResourceAccess>());
		AclSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidAclSnapshot();
		ResourceAccess ra = new ResourceAccess();
		Set<ResourceAccess> set = new HashSet<ResourceAccess>();
		set.add(ra);
		snapshot3.setResourceAccess(set);

		dao.insert(Arrays.asList(snapshot1, snapshot2, snapshot3));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), Long.parseLong(snapshot1.getId()), snapshot1.getOwnerType()));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), Long.parseLong(snapshot2.getId()), snapshot2.getOwnerType()));
		assertEquals(snapshot3, dao.get(snapshot3.getTimestamp(), Long.parseLong(snapshot3.getId()), snapshot3.getOwnerType()));

		AclSnapshot snapshot4 = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot4.setTimestamp(snapshot2.getTimestamp());
		snapshot4.setId(snapshot2.getId());
		snapshot4.setOwnerType(snapshot2.getOwnerType());
		dao.insert(Arrays.asList(snapshot4));
		assertEquals(snapshot2, dao.get(snapshot4.getTimestamp(), Long.parseLong(snapshot4.getId()), snapshot4.getOwnerType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong(), ObjectType.ENTITY);
	}
}
