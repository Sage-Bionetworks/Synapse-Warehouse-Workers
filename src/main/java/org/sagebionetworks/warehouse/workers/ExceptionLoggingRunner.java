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

	public ExceptionLoggingRunner(Runnable wrapped) {
		super();
		if (wrapped == null) {
			throw new IllegalArgumentException("runner cannot be null");
		}
		this.wrapped = wrapped;
	}

	@Override
	public void run() {
		try {
			wrapped.run();
		} catch (Throwable e) {
			log.error("Worker failed:", e);
		}
	}

}
