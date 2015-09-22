package org.sagebionetworks.warehouse.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

public class HealthCheckWorker implements ProgressingRunner<Void>{
	private static Logger log = LogManager.getLogger(HealthCheckWorker.class);

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		log.info("ping");
		System.out.println("printing from health check worker");
	}

}
