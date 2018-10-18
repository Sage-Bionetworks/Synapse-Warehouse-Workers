package org.sagebionetworks.warehouse.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.sagebionetworks.workers.util.Gate;

import com.google.inject.Inject;

public class RunDuringNormalStateGate implements Gate{

	private WarehouseWorkersStateDao dao;
	private AmazonLogger amazonLogger;
	private static Logger log = LogManager.getLogger(RunDuringNormalStateGate.class);

	@Inject
	RunDuringNormalStateGate(WarehouseWorkersStateDao dao, AmazonLogger amazonLogger) {
		this.dao = dao;
		this.amazonLogger = amazonLogger;
	}

	@Override
	public boolean canRun() {
		return dao.getState() == WarehouseWorkersState.NORMAL;
	}

	@Override
	public void runFailed(Exception error) {
		log.error(error.toString());
		amazonLogger.logNonRetryableError(null, null, this.getClass().getSimpleName(),
				error.getClass().getSimpleName(), error.getStackTrace().toString());
	}

}
