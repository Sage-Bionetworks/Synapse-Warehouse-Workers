package org.sagebionetworks.warehouse.workers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The worker stack is simply a runnable worker driven from a timer.
 *
 */
public class WorkerStackImpl implements WorkerStack {
	
	private static final Logger log = LogManager.getLogger(WorkerStackImpl.class);
	
	final ScheduledExecutorService scheduler;
	final Runnable runner;
	final int startDalayMS;
	final int periodMS;
	final String workerName;
	
	public WorkerStackImpl(final WorkerStackConfiguration config){
		if(config == null){
			throw new IllegalArgumentException("Configuration cannot be null");
		}
		if(config.getRunner() == null){
			throw new IllegalArgumentException("Runner cannot be null");
		}
		if(config.getWorkerName() == null){
			throw new IllegalArgumentException("Worker name cannot be null");
		}
		this.workerName = config.getWorkerName();
		this.scheduler = Executors.newScheduledThreadPool(1);
		// Since exceptions will terminate the timer trap and log any exceptions.
		this.runner = new ExceptionLoggingRunner(config.getRunner());
		this.startDalayMS = config.getStartDelayMs();
		this.periodMS = config.getPeriodMS();
	}

	@Override
	public void start() {
		// Start the worker.
		scheduler.scheduleAtFixedRate(runner, startDalayMS, periodMS,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() {
		scheduler.shutdown();
	}

	@Override
	public String getWorketName() {
		return workerName;
	}

}
