package org.sagebionetworks.warehouse.workers;

/**
 * Represent a stack for a single worker.
 * 
 * @author John
 *
 */
public interface WorkerStack {
	
	/**
	 * Start this worker stack.
	 */
	public void start();
	
	/**
	 * Shutdown this worker stack.
	 */
	public void shutdown();

}
