package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

public class UserAccessRecordDaoImplTest {

	UserActivityPerClientPerDayDao dao = TestContext.singleton().getInstance(UserActivityPerClientPerDayDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(UserActivityPerClientPerDayDaoImpl.USER_ACCESS_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		UserActivityPerClientPerDay uar1 = AccessRecordUtils.getUserAccessRecord(AccessRecordTestUtil.createValidAccessRecord());
		UserActivityPerClientPerDay uar2 = AccessRecordUtils.getUserAccessRecord(AccessRecordTestUtil.createValidAccessRecord());

		dao.insert(Arrays.asList(uar1, uar2));

		// verify that we have 2 entries in the table
		UserActivityPerClientPerDay actualUar1 = dao.get(uar1.getUserId(), uar1.getDate(), uar1.getClient());
		UserActivityPerClientPerDay actualUar2 = dao.get(uar2.getUserId(), uar2.getDate(), uar2.getClient());
		assertEquals(uar1, actualUar1);
		assertEquals(uar2, actualUar2);

		// update
		dao.insert(Arrays.asList(uar2));
		// validate that the par2 record is updated
		actualUar2 = dao.get(uar2.getUserId(), uar2.getDate(), uar2.getClient());
		assertEquals(uar2, actualUar2);

		// different client
		UserActivityPerClientPerDay uar3 = uar2;
		uar3.setClient(Client.R);
		dao.insert(Arrays.asList(uar3));
		UserActivityPerClientPerDay actualUar3 = dao.get(uar3.getUserId(), uar3.getDate(), uar3.getClient());
		assertEquals(uar3, actualUar3);
		assertFalse(actualUar2.equals(actualUar3));
	}

}
