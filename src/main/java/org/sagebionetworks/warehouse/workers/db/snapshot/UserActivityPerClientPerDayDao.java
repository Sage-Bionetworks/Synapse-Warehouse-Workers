package org.sagebionetworks.warehouse.workers.db.snapshot;

import java.util.Date;
import java.util.Iterator;

import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;

public interface UserActivityPerClientPerDayDao extends SnapshotDao<UserActivityPerClientPerDay>{

	/**
	 * 
	 * @return a UserActivityPerClientPerDay given the userId, date and client
	 */
	public UserActivityPerClientPerDay get(Long userId, String xForwardedFor, String date, Client client);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();

	/**
	 * @param month
	 * @return an iterator of all UserActivityPerMonth for the given month
	 */
	public Iterator<UserActivityPerMonth> getUserActivityPerMonth(Date month);
}
