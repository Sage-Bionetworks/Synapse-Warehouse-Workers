package org.sagebionetworks.warehouse.workers.db;

import org.sagebionetworks.repo.model.UserGroup;

public interface UserGroupDao extends SnapshotDao<UserGroup>{

	/**
	 * 
	 * @return an UserGroup given the id and isIndividual value
	 */
	public UserGroup get(Long id, boolean isIndividual);

	/**
	 * Truncate all of the data.
	 */
	public void truncateAll();
}
