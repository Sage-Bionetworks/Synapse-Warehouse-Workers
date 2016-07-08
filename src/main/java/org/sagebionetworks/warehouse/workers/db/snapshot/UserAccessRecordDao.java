package org.sagebionetworks.warehouse.workers.db.snapshot;

import org.sagebionetworks.warehouse.workers.db.HasPartitions;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserAccessRecord;

public interface UserAccessRecordDao extends HasPartitions, SnapshotDao<UserAccessRecord>{

	/**
	 * 
	 * @return a UserAccessRecord given the userId, date and client
	 */
	public UserAccessRecord get(Long userId, String date, Client client);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
