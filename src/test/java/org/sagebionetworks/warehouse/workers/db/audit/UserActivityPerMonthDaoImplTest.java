package org.sagebionetworks.warehouse.workers.db.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDao;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDaoImpl;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;

public class UserActivityPerMonthDaoImplTest {

	UserActivityPerMonthDao dao = TestContext.singleton().getInstance(UserActivityPerMonthDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(UserActivityPerMonthDaoImpl.USER_ACTIVITY_PER_MONTH_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void testRoundTrip() {
		UserActivityPerMonth uar1 = createUserActivityPerMonth(1L, "2016-01-01", 2L);
		UserActivityPerMonth uar2 = createUserActivityPerMonth(2L, "2016-01-01", 1L);

		dao.insert(Arrays.asList(uar1, uar2));

		// verify that we have 2 entries in the table
		UserActivityPerMonth actualUar1 = dao.get(uar1.getUserId(), uar1.getMonth());
		UserActivityPerMonth actualUar2 = dao.get(uar2.getUserId(), uar2.getMonth());
		assertEquals(uar1, actualUar1);
		assertEquals(uar2, actualUar2);

		// update
		UserActivityPerMonth uar3 = uar2;
		uar3.setUniqueDate(3L);
		dao.insert(Arrays.asList(uar3));
		UserActivityPerMonth actualUar3 = dao.get(uar3.getUserId(), uar3.getMonth());
		assertEquals(uar3, actualUar3);
		assertFalse(actualUar2.equals(actualUar3));
	}

	@Test
	public void testHasRecordForMonthFalse() {
		Date month = new DateTime().withDate(2016, 1, 1).withTime(0, 0, 0, 0).toDate();
		assertFalse(dao.hasRecordForMonth(month));
	}

	@Test
	public void testHasRecordForMonthTrue() {
		Date month = new DateTime().withDate(2016, 1, 1).withTime(0, 0, 0, 0).toDate();
		UserActivityPerMonth uar = createUserActivityPerMonth(1L, "2016-01-01", 2L);
		dao.insert(Arrays.asList(uar));
		assertTrue(dao.hasRecordForMonth(month));
	}

	private UserActivityPerMonth createUserActivityPerMonth(long userId, String month, long uniqueDate) {
		UserActivityPerMonth uapm = new UserActivityPerMonth();
		uapm.setUserId(userId);
		uapm.setMonth(month);
		uapm.setUniqueDate(uniqueDate);
		return uapm;
	}

}
