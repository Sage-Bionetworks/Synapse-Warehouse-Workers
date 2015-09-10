package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public interface WarehouseWorkersStateDao {

	/**
	 * 
	 * @return the state of the system
	 */
	public WarehouseWorkersState getState();

	/**
	 * 
	 * @param state - the system state to set to
	 */
	public void setState(WarehouseWorkersState state);

	/**
	 * Truncate all data
	 */
	public void truncateAll();
}
