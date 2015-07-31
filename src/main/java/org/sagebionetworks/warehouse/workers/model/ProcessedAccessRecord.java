package org.sagebionetworks.warehouse.workers.model;

public class ProcessedAccessRecord {

	private String sessionId;
	private String entityId;
	private Client client;
	private String synapseAPI;

	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client agent) {
		this.client = agent;
	}
	public String getSynapseApi() {
		return synapseAPI;
	}
	public void setSynapseApi(String synapseApi) {
		this.synapseAPI = synapseApi;
	}
	@Override
	public String toString() {
		return "ProcessedAccessRecord [sessionId=" + sessionId + ", entityId="
				+ entityId + ", client=" + client + ", synapseApi="
				+ synapseAPI + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result
				+ ((synapseAPI == null) ? 0 : synapseAPI.hashCode());
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
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (synapseAPI == null) {
			if (other.synapseAPI != null)
				return false;
		} else if (!synapseAPI.equals(other.synapseAPI))
			return false;
		return true;
	}
}
