package org.sagebionetworks.warehouse.workers.db.snapshot;

import java.util.Date;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;

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

	/**
	 * @param month
	 * @return a batch of UserActivityPerMonth for the given month
	 */
	public List<UserActivityPerMonth> getUserActivityPerMonth(Date month);
}
