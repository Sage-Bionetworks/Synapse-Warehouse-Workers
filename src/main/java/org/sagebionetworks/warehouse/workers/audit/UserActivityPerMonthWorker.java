package org.sagebionetworks.warehouse.workers.audit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.common.util.progress.ProgressingRunner;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDao;

import com.google.inject.Inject;

public class UserActivityPerMonthWorker implements ProgressingRunner<Void>{
	private static Logger log = LogManager.getLogger(UserActivityPerMonthWorker.class);

	UserActivityPerMonthDao userActivityPerMonthDao;
	UserActivityPerClientPerDayDao userActivityPerClientPerDayDao;
	Configuration config;

	@Inject
	UserActivityPerMonthWorker (UserActivityPerMonthDao userActivityPerMonthDao,
			UserActivityPerClientPerDayDao userActivityPerClientPerDayDao,
			Configuration config) {
		this.userActivityPerMonthDao = userActivityPerMonthDao;
		this.userActivityPerClientPerDayDao = userActivityPerClientPerDayDao;
		this.config = config;
	}

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		DateTime time = new DateTime();
		if (time.getDayOfMonth() != config.getMonthlyAuditDay()) {
			return;
		}
		DateTime prevMonth = time.minusMonths(1)
				.withDayOfMonth(0)
				.withHourOfDay(0)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		// find out if there are records for prev month
		if (userActivityPerMonthDao.hasRecordForMonth(prevMonth)) {
			return;
		}
		// find all records for prev month
		// insert all records for prev month
	}


}
