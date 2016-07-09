package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;

public interface UserActivityPerClientPerDayDao extends HasPartitions, SnapshotDao<UserActivityPerClientPerDay>{

	/**
	 * 
	 * @return a UserActivityPerClientPerDay given the userId, date and client
	 */
	public UserActivityPerClientPerDay get(Long userId, String date, Client client);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
