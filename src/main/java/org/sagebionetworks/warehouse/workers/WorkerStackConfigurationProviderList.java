package org.sagebionetworks.warehouse.workers;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper for a worker stack configuration.
 *
 */
public class WorkerStackConfigurationProviderList {
	
	List<Class<? extends WorkerStackConfigurationProvider>> list = new LinkedList<Class<? extends WorkerStackConfigurationProvider>>();
	
	/**
	 * Get the worker stack list.
	 * @return
	 */
	public List<Class<? extends WorkerStackConfigurationProvider>> getList(){
		return list;
	}
	
	/**
	 * Add a worker stack to the list.
	 * @param workerStack
	 */
	public void add(Class<? extends WorkerStackConfigurationProvider> providerClass){
		this.list.add(providerClass);
	}
}
