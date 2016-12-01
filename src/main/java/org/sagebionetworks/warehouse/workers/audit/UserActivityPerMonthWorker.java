package org.sagebionetworks.warehouse.workers.audit;

import java.util.ArrayList;
import java.util.Iterator;
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
	public static final int MONTHS_TO_PROCESS = 24;
	private static final int BATCH_SIZE = 1000;
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
		DateTime prevMonth = DateTimeUtils.getFirstDayOfPreviousMonth(time);
		if (time.getDayOfMonth() < config.getMonthlyAuditDay()) {
			prevMonth = prevMonth.minusMonths(1);
		}
		for (int i = 0; i < MONTHS_TO_PROCESS; i++) {
			DateTime monthToProcess = prevMonth.minusMonths(i);
			if (!userActivityPerMonthDao.hasRecordForMonth(monthToProcess.toDate())) {
				updateUserActivityForMonth(monthToProcess, BATCH_SIZE);
			} else {
				log.info("Skipping month: "+monthToProcess.toDate().toString());
			}
		}
	}

	public void updateUserActivityForMonth(DateTime month, int batchSize) {
		log.info("Processing UserActivityPerMonth for "+month.toString());
		Iterator<UserActivityPerMonth> it = userActivityPerClientPerDayDao.getUserActivityPerMonth(month.toDate());
		List<UserActivityPerMonth> batch = new ArrayList<UserActivityPerMonth>();
		while (it.hasNext()) {
			batch.add(it.next());
			if (batch.size() == batchSize) {
				userActivityPerMonthDao.insert(batch);
				log.info("Inserted "+batch.size()+" records for "+month.toString());
				batch.clear();
			}
		}
		if (batch.size() > 0) {
			userActivityPerMonthDao.insert(batch);
			log.info("Inserted "+batch.size()+" records for "+month.toString());
		}
	}

}
