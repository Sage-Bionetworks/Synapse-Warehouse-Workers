package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamMemberSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamMemberSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class TeamMemberSnapshotDaoImplTest {
	TeamMemberSnapshotDao dao = TestContext.singleton().getInstance(TeamMemberSnapshotDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(TeamMemberSnapshotDaoImpl.TEAM_MEMBER_SNAPSHOT_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		TeamMemberSnapshot snapshot1 = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		TeamMemberSnapshot snapshot2 = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();

		dao.insert(Arrays.asList(snapshot1, snapshot2));
		assertEquals(snapshot1, dao.get(snapshot1.getTimestamp(), snapshot1.getTeamId(), snapshot1.getMemberId()));
		assertEquals(snapshot2, dao.get(snapshot2.getTimestamp(), snapshot2.getTeamId(), snapshot2.getMemberId()));

		TeamMemberSnapshot snapshot3 = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot3.setTimestamp(snapshot2.getTimestamp());
		snapshot3.setTeamId(snapshot2.getTeamId());
		snapshot3.setMemberId(snapshot2.getMemberId());
		dao.insert(Arrays.asList(snapshot3));
		assertEquals(snapshot2, dao.get(snapshot3.getTimestamp(), snapshot3.getTeamId(), snapshot3.getMemberId()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(System.currentTimeMillis(), new Random().nextLong(), new Random().nextLong());
	}
}
