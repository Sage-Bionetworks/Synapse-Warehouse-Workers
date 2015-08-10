package org.sagebionetworks.warehouse.workers.model;

public class ProcessedAccessRecord {

	private String sessionId;
	private long entityId;
	private Client client;
	private String normalizedMethodSignature;

	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public String getNormalizedMethodSignature() {
		return normalizedMethodSignature;
	}
	public void setNormalizedMethodSignature(String normalizedMethodSignature) {
		this.normalizedMethodSignature = normalizedMethodSignature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + (int) (entityId ^ (entityId >>> 32));
		result = prime
				* result
				+ ((normalizedMethodSignature == null) ? 0
						: normalizedMethodSignature.hashCode());
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessedAccessRecord other = (ProcessedAccessRecord) obj;
		if (client != other.client)
			return false;
		if (entityId != other.entityId)
			return false;
		if (normalizedMethodSignature == null) {
			if (other.normalizedMethodSignature != null)
				return false;
		} else if (!normalizedMethodSignature
				.equals(other.normalizedMethodSignature))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ProcessedAccessRecord [sessionId=" + sessionId + ", entityId="
				+ entityId + ", client=" + client
				+ ", normalizedMethodSignature=" + normalizedMethodSignature
				+ "]";
	}
}
