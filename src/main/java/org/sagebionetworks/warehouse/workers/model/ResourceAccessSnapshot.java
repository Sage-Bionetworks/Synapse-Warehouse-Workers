package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.ObjectType;

public class ResourceAccessSnapshot {

	Long timestamp;
	ObjectType ownerType;
	Long ownerId;
	Long principalId;
	ACCESS_TYPE accessType;
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public ObjectType getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(ObjectType ownerType) {
		this.ownerType = ownerType;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}
	public ACCESS_TYPE getAccessType() {
		return accessType;
	}
	public void setAccessType(ACCESS_TYPE accessType) {
		this.accessType = accessType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessType == null) ? 0 : accessType.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result
				+ ((ownerType == null) ? 0 : ownerType.hashCode());
		result = prime * result
				+ ((principalId == null) ? 0 : principalId.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
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
		ResourceAccessSnapshot other = (ResourceAccessSnapshot) obj;
		if (accessType != other.accessType)
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (ownerType != other.ownerType)
			return false;
		if (principalId == null) {
			if (other.principalId != null)
				return false;
		} else if (!principalId.equals(other.principalId))
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
		return "ResourceAccessSnapshot [timestamp=" + timestamp
				+ ", ownerType=" + ownerType + ", ownerId=" + ownerId
				+ ", principalId=" + principalId + ", accessType=" + accessType
				+ "]";
	}
}
