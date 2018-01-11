package org.sagebionetworks.warehouse.workers.model;

public class ProcessedAccessRecord {

	private String sessionId;
	private Long timestamp;
	private Long entityId;
	private Client client;
	private String clientVersion;
	private String normalizedMethodSignature;

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
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

	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((clientVersion == null) ? 0 : clientVersion.hashCode());
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((normalizedMethodSignature == null) ? 0 : normalizedMethodSignature.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		if (clientVersion == null) {
			if (other.clientVersion != null)
				return false;
		} else if (!clientVersion.equals(other.clientVersion))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (normalizedMethodSignature == null) {
			if (other.normalizedMethodSignature != null)
				return false;
		} else if (!normalizedMethodSignature.equals(other.normalizedMethodSignature))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ProcessedAccessRecord [sessionId=" + sessionId + ", timestamp=" + timestamp + ", entityId=" + entityId
				+ ", client=" + client + ", clientVersion=" + clientVersion + ", normalizedMethodSignature="
				+ normalizedMethodSignature + "]";
	}
}
