package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.Team;

public class TeamSnapshot extends Team {

	private static final long serialVersionUID = 1L;
	private Long timestamp;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
