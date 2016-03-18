package org.sagebionetworks.warehouse.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.db.snapshot.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.sagebionetworks.workers.util.Gate;

import com.google.inject.Inject;

public class RunDuringMaintenanceStateGate implements Gate{

	private WarehouseWorkersStateDao dao;
	private static Logger log = LogManager.getLogger(RunDuringMaintenanceStateGate.class);

	@Inject
	RunDuringMaintenanceStateGate(WarehouseWorkersStateDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean canRun() {
		return dao.getState() == WarehouseWorkersState.MAINTENANCE;
	}

	@Override
	public void runFailed(Exception error) {
		log.error(error.toString());
	}

}
