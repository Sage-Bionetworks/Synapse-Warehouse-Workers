package org.sagebionetworks.warehouse.workers.audit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.sagebionetworks.warehouse.workers.audit.UserActivityPerMonthWorker.*;

import java.util.Date;
import java.util.Iterator;
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
	@Mock
	Iterator<UserActivityPerMonth> mockIterator;

	UserActivityPerMonthWorker worker;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		worker = new UserActivityPerMonthWorker(mockUserActivityPerMonthDao,
				mockUserActivityPerClientPerDayDao, mockConfig);
		when(mockUserActivityPerClientPerDayDao.getUserActivityPerMonth(any(Date.class))).thenReturn(mockIterator);
		when(mockIterator.hasNext()).thenReturn(false);
		when(mockIterator.next()).thenReturn(new UserActivityPerMonth());
	}

	@Test
	public void testRunBeforeAuditDay() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth() + 2);
		worker.run(mockCallback);
		verify(mockUserActivityPerMonthDao, never()).hasRecordForMonth(prevMonth.toDate());
		verify(mockUserActivityPerMonthDao, times(MONTHS_TO_PROCESS)).hasRecordForMonth(any(Date.class));
	}

	@Test
	public void testOnAuditDay() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth());
		when(mockUserActivityPerMonthDao.hasRecordForMonth(prevMonth.toDate())).thenReturn(false);
		worker.run(mockCallback);
		verify(mockUserActivityPerMonthDao).hasRecordForMonth(prevMonth.toDate());
		verify(mockUserActivityPerMonthDao, times(MONTHS_TO_PROCESS)).hasRecordForMonth(any(Date.class));
		verify(mockUserActivityPerClientPerDayDao).getUserActivityPerMonth(prevMonth.toDate());
	}

	@Test
	public void testOnAuditDayAlreadyProceed() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		when(mockConfig.getMonthlyAuditDay()).thenReturn(time.getDayOfMonth());
		when(mockUserActivityPerMonthDao.hasRecordForMonth(prevMonth.toDate())).thenReturn(true);
		worker.run(mockCallback);
		verify(mockUserActivityPerMonthDao).hasRecordForMonth(prevMonth.toDate());
		verify(mockUserActivityPerMonthDao, times(MONTHS_TO_PROCESS)).hasRecordForMonth(any(Date.class));
		verify(mockUserActivityPerClientPerDayDao, never()).getUserActivityPerMonth(prevMonth.toDate());
	}

	@Test
	public void testUpdateUserActivityForMonthLessThanBatchSize() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		when(mockIterator.hasNext()).thenReturn(true, false);
		worker.updateUserActivityForMonth(prevMonth, 2);
		verify(mockUserActivityPerMonthDao).insert(any(List.class));
	}

	@Test
	public void testUpdateUserActivityForMonthMoreThanBatchSize() throws Exception {
		DateTime time = new DateTime();
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		when(mockIterator.hasNext()).thenReturn(true, true, true, false);
		worker.updateUserActivityForMonth(prevMonth, 2);
		verify(mockUserActivityPerMonthDao, times(2)).insert(any(List.class));
	}
}
