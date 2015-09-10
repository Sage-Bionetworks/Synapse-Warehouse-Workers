package org.sagebionetworks.warehouse.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple runner wrapper that will trap and log exceptions thrown by the
 * wrapped runner.
 * 
 */
public class ExceptionLoggingRunner implements Runnable {

	private static final Logger log = LogManager
			.getLogger(WorkerStackImpl.class);

	private Runnable wrapped;
	private String workerName;

	public ExceptionLoggingRunner(Runnable wrapped, String workerName) {
		super();
		if (wrapped == null) {
			throw new IllegalArgumentException("runner cannot be null");
		}
		this.wrapped = wrapped;
		this.workerName = workerName;
	}

	@Override
	public void run() {
		try {
			wrapped.run();
		} catch (Throwable e) {
			log.error("Worker "+workerName+" failed:", e);
		}
	}

}
