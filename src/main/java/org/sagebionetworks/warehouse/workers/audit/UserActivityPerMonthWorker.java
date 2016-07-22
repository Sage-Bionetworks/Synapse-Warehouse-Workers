package org.sagebionetworks.warehouse.workers.audit;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.common.util.progress.ProgressingRunner;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDao;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;
import org.sagebionetworks.warehouse.workers.utils.DateTimeUtils;

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
		DateTime prevMonth = DateTimeUtils.getPrevMonthAndFloor(time);
		if (userActivityPerMonthDao.hasRecordForMonth(prevMonth.toDate())) {
			return;
		}
		log.trace("Processing UserActivityPerMonth for "+prevMonth.toString());
		List<UserActivityPerMonth> batch = userActivityPerClientPerDayDao.getUserActivityPerMonth(prevMonth.toDate());
		userActivityPerMonthDao.insert(batch);
		log.info("Inserted "+batch.size()+" records for "+prevMonth.toString());
	}

}
