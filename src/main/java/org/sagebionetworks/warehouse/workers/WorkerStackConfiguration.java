package org.sagebionetworks.warehouse.workers;

/**
 * Configuration information for a worker stack. 
 * @author John
 *
 */
public class WorkerStackConfiguration {

	String workerName;
	Runnable runner;
	int startDelayMs;
	int periodMS; 

	/**
	 * The name of the worker.
	 * @return
	 */
	public String getWorkerName() {
		return workerName;
	}
	
	/**
	 * The name of the worker.
	 * @param workerName
	 */
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	/**
	 * The runner that will be run when the worker stack timer fires.
	 * @return
	 */
	public Runnable getRunner() {
		return runner;
	}
	/**
	 * The runner that will be run when the worker stack timer fires.
	 * @param runner
	 */
	public void setRunner(Runnable runner) {
		this.runner = runner;
	}
	/**
	 * The amount of time in MS before the timer starts a worker for the first time.
	 * User different values to stagger workers.
	 * @return
	 */
	public int getStartDelayMs() {
		return startDelayMs;
	}
	
	/**
	 * The amount of time in MS before the timer starts a worker for the first time.
	 * User different values to stagger workers.
	 * @param startDelayMs
	 */
	public void setStartDelayMs(int startDelayMs) {
		this.startDelayMs = startDelayMs;
	}
	
	/**
	 * The period between successive executions of the worker in MS.
	 * @return
	 */
	public int getPeriodMS() {
		return periodMS;
	}
	/**
	 * The period between successive executions of the worker in MS.
	 * @param periodMS
	 */
	public void setPeriodMS(int periodMS) {
		this.periodMS = periodMS;
	}

	@Override
	public String toString() {
		return "WorkerStackConfiguration [workerName=" + workerName
				+ ", runner=" + runner + ", startDelayMs=" + startDelayMs
				+ ", periodMS=" + periodMS + "]";
	}
	
}
