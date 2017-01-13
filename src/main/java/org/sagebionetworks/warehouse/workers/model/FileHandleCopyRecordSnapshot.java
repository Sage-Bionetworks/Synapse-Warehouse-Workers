package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;

public class FileHandleCopyRecordSnapshot{

	long timestamp;
	long userId;
	long originalFileHandleId;
	long associationObjectId;
	FileHandleAssociateType associationObjectType;
	long newFileHandleId;
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getOriginalFileHandleId() {
		return originalFileHandleId;
	}
	public void setOriginalFileHandleId(long originalFileHandleId) {
		this.originalFileHandleId = originalFileHandleId;
	}
	public long getAssociationObjectId() {
		return associationObjectId;
	}
	public void setAssociationObjectId(long associationObjectId) {
		this.associationObjectId = associationObjectId;
	}
	public FileHandleAssociateType getAssociationObjectType() {
		return associationObjectType;
	}
	public void setAssociationObjectType(FileHandleAssociateType associationObjectType) {
		this.associationObjectType = associationObjectType;
	}
	public long getNewFileHandleId() {
		return newFileHandleId;
	}
	public void setNewFileHandleId(long newFileHandleId) {
		this.newFileHandleId = newFileHandleId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (associationObjectId ^ (associationObjectId >>> 32));
		result = prime * result + ((associationObjectType == null) ? 0 : associationObjectType.hashCode());
		result = prime * result + (int) (newFileHandleId ^ (newFileHandleId >>> 32));
		result = prime * result + (int) (originalFileHandleId ^ (originalFileHandleId >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		FileHandleCopyRecordSnapshot other = (FileHandleCopyRecordSnapshot) obj;
		if (associationObjectId != other.associationObjectId)
			return false;
		if (associationObjectType != other.associationObjectType)
			return false;
		if (newFileHandleId != other.newFileHandleId)
			return false;
		if (originalFileHandleId != other.originalFileHandleId)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileHandleCopyRecordSnapshot [timestamp=" + timestamp + ", userId=" + userId + ", originalFileHandleId="
				+ originalFileHandleId + ", fileHandleAssociationId=" + associationObjectId
				+ ", fileHandleAssociationType=" + associationObjectType + ", newFileHandleId=" + newFileHandleId
				+ "]";
	}
}
