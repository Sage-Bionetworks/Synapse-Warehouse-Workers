package org.sagebionetworks.warehouse.workers.audit;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDao;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;
import org.sagebionetworks.warehouse.workers.utils.DateTimeUtils;

public class UserActivityPerMonthWorkerTest {
	@Mock
	UserActivityPerMonthDao mockUserActivityPerMonthDao;
	@Mock
	UserActivityPerClientPerDayDao mockUserActivityPerClientPerDayDao;
	@Mock
	Configuration mockConfig;
	@Mock
	ProgressCallback<Void> mockCallback;

	UserActivityPerMonthWorker worker;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		worker = new UserActivityPerMonthWorker(mockUserActivityPerMonthDao,
				mockUserActivityPerClientPerDayDao, mockConfig);
	}

	@Test
	public void testNotAuditDay() throws Exception {
		DateTime time = new DateTime();
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth() + 2);
		worker.run(mockCallback);
		verifyZeroInteractions(mockUserActivityPerMonthDao);
		verifyZeroInteractions(mockUserActivityPerClientPerDayDao);
	}

	@Test
	public void testAlreadyDone() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getPrevMonthAndFloor(time);
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth());
		when(mockUserActivityPerMonthDao.hasRecordForMonth(prevMonth.toDate())).thenReturn(true);
		worker.run(mockCallback);
		verify(mockUserActivityPerMonthDao).hasRecordForMonth(prevMonth.toDate());
		verifyZeroInteractions(mockUserActivityPerClientPerDayDao);
		verify(mockUserActivityPerMonthDao, never()).insert(anyList());
	}

	@Test
	public void testWorking() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getPrevMonthAndFloor(time);
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth());
		when(mockUserActivityPerMonthDao.hasRecordForMonth(prevMonth.toDate())).thenReturn(false);
		List<UserActivityPerMonth> list = new ArrayList<UserActivityPerMonth>();
		when(mockUserActivityPerClientPerDayDao.getUserActivityPerMonth(prevMonth.toDate())).thenReturn(list);
		worker.run(mockCallback);
		verify(mockUserActivityPerMonthDao).hasRecordForMonth(prevMonth.toDate());
		verify(mockUserActivityPerClientPerDayDao).getUserActivityPerMonth(prevMonth.toDate());
		verify(mockUserActivityPerMonthDao).insert(list);
	}
}
