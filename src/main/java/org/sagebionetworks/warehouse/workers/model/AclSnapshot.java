package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.audit.AclRecord;

public class AclSnapshot extends AclRecord {

	private static final long serialVersionUID = 1L;
	Long timestamp;
	public static final String RESOURCE_ACCESS_ALIAS = "ResourceAccessSet";

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
