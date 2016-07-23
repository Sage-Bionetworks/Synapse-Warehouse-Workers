package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

public class UserActivityPerClientPerDayDaoImplTest {

	UserActivityPerClientPerDayDao dao = TestContext.singleton().getInstance(UserActivityPerClientPerDayDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(UserActivityPerClientPerDayDaoImpl.USER_ACTIVITY_PER_CLIENT_PER_DAY_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void testCreateUpdate() {
		UserActivityPerClientPerDay uar1 = AccessRecordUtils.getUserActivityPerClientPerDay(AccessRecordTestUtil.createValidAccessRecord());
		UserActivityPerClientPerDay uar2 = AccessRecordUtils.getUserActivityPerClientPerDay(AccessRecordTestUtil.createValidAccessRecord());

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

	@Test
	public void testGetUserActivityPerMonthNoRecords(){
		Date month = new DateTime().withDate(2016, 1, 1).withTime(0, 0, 0, 0).toDate();
		List<UserActivityPerMonth> results = dao.getUserActivityPerMonth(month);
		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetUserActivityPerMonth(){
		// user with 2 distinct dates
		UserActivityPerClientPerDay uar1 = createUserActivityPerClientPerDay(1L, "2016-01-13", Client.R);
		UserActivityPerClientPerDay uar2 = createUserActivityPerClientPerDay(1L, "2016-01-02", Client.R);
		// user with 2 clients
		UserActivityPerClientPerDay uar3 = createUserActivityPerClientPerDay(2L, "2016-01-08", Client.R);
		UserActivityPerClientPerDay uar4 = createUserActivityPerClientPerDay(2L, "2016-01-08", Client.JAVA);
		// user with 2 activity on the same date, same client
		UserActivityPerClientPerDay uar5 = createUserActivityPerClientPerDay(3L, "2016-01-14", Client.R);
		UserActivityPerClientPerDay uar6 = createUserActivityPerClientPerDay(3L, "2016-01-14", Client.R);

		dao.insert(Arrays.asList(uar1, uar2, uar3, uar4, uar5, uar6));
		Date month = new DateTime().withDate(2016, 1, 1).withTime(0, 0, 0, 0).toDate();
		List<UserActivityPerMonth> results = dao.getUserActivityPerMonth(month);
		assertNotNull(results);
		assertEquals(3, results.size());

		UserActivityPerMonth ua1 = createUserActivityPerMonth(1L, "2016-01-01", 2L);
		UserActivityPerMonth ua2 = createUserActivityPerMonth(2L, "2016-01-01", 1L);
		UserActivityPerMonth ua3 = createUserActivityPerMonth(3L, "2016-01-01", 1L);

		assertTrue(results.contains(ua1));
		assertTrue(results.contains(ua2));
		assertTrue(results.contains(ua3));
	}

	private UserActivityPerMonth createUserActivityPerMonth(long userId, String month, long uniqueDate) {
		UserActivityPerMonth ua = new UserActivityPerMonth();
		ua.setUserId(userId);
		ua.setMonth(month);
		ua.setUniqueDate(uniqueDate);
		return ua;
	}

	private UserActivityPerClientPerDay createUserActivityPerClientPerDay(Long userId, String date, Client client) {
		UserActivityPerClientPerDay uar = new UserActivityPerClientPerDay();
		uar.setClient(client);
		uar.setUserId(userId);
		uar.setDate(date);
		return uar;
	}
}
