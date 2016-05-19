package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;

/**
 * This class represents a bulk file download record.
 * 
 * While capturing the bulk file download response snapshot in a json format,
 * our goal is to answer the question who download what. This record captures
 * the information needed to answer that question.
 * 
 * A unique download record includes user ID who performed the download, the objectID
 * and objectType that the user had download.
 * 
 * @author kimyentruong
 *
 */
public class BulkFileDownloadRecord {

	Long userId;
	Long objectId;
	FileHandleAssociateType objectType;

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public FileHandleAssociateType getObjectType() {
		return objectType;
	}
	public void setObjectType(FileHandleAssociateType objectType) {
		this.objectType = objectType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectId == null) ? 0 : objectId.hashCode());
		result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		BulkFileDownloadRecord other = (BulkFileDownloadRecord) obj;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (objectType != other.objectType)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BulkFileDownloadRecord [userId=" + userId + ", objectId=" + objectId + ", objectType=" + objectType
				+ "]";
	}
}
