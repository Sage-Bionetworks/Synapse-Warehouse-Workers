package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.Node;

public class NodeSnapshot extends Node {

	private static final long serialVersionUID = 1L;
	private Long timestamp;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
