package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;

public interface UserActivityPerMonthDao extends SnapshotDao<UserActivityPerMonth>{

	/**
	 * 
	 * @return a UserActivityPerMonth given the userId and month
	 */
	public UserActivityPerMonth get(Long userId, String month);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
