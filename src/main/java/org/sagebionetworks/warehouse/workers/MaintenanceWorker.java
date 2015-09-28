package org.sagebionetworks.warehouse.workers;

import org.joda.time.DateTime;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

import com.google.inject.Inject;

public class MaintenanceWorker implements ProgressingRunner<Void> {

	private Configuration config;
	private WarehouseWorkersStateDao dao;

	@Inject
	MaintenanceWorker (WarehouseWorkersStateDao dao, Configuration config) {
		this.dao = dao;
		this.config = config;
	}

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		DateTime time = new DateTime();
		if ((time.getHourOfDay() == config.getMaintenanceStartTime()) &&
				(dao.getState() == WarehouseWorkersState.NORMAL)){
			dao.setState(WarehouseWorkersState.MAINTENANCE);
		}
		if ((time.getHourOfDay() == config.getMaintenanceEndTime()) &&
				(dao.getState() == WarehouseWorkersState.MAINTENANCE)){
			dao.setState(WarehouseWorkersState.NORMAL);
		}
	}

}
