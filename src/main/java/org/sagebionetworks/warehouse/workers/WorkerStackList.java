package org.sagebionetworks.warehouse.workers;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper for the list of worker stacks provided by Guice.
 *
 */
public class WorkerStackList {
	
	List<WorkerStack> list = new LinkedList<WorkerStack>();

	/**
	 * Get the worker stack list.
	 * @return
	 */
	public List<WorkerStack> getList(){
		return list;
	}
	
	/**
	 * Add a worker stack to the list.
	 * @param workerStack
	 */
	public void add(WorkerStack workerStack){
		this.list.add(workerStack);
	}
}
