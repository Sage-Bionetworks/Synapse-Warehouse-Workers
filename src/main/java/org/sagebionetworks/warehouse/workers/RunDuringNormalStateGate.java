package org.sagebionetworks.warehouse.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.sagebionetworks.workers.util.Gate;

import com.google.inject.Inject;

public class RunDuringNormalStateGate implements Gate{

	private WarehouseWorkersStateDao dao;
	private static Logger log = LogManager.getLogger(RunDuringNormalStateGate.class);

	@Inject
	RunDuringNormalStateGate(WarehouseWorkersStateDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean canRun() {
		return true;
		//return dao.getState() == WarehouseWorkersState.NORMAL;
	}

	@Override
	public void runFailed(Exception error) {
		log.error(error.toString());
	}

}
