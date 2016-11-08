package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;

/**
 * This class represents a file download record.
 * 
 * A file download record includes user ID who performed the download, the
 * timestamp of the download, fileHandleId, associationObjectType and
 * associationObjectId that the user had downloaded.
 * 
 * @author kimyentruong
 *
 */
public class FileDownload {

	Long userId;
	Long timestamp;
	Long fileHandleId;
	Long associationObjectId;
	FileHandleAssociateType associationObjectType;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Long getFileHandleId() {
		return fileHandleId;
	}
	public void setFileHandleId(Long fileHandleId) {
		this.fileHandleId = fileHandleId;
	}
	public Long getAssociationObjectId() {
		return associationObjectId;
	}
	public void setAssociationObjectId(Long associationObjectId) {
		this.associationObjectId = associationObjectId;
	}
	public FileHandleAssociateType getAssociationObjectType() {
		return associationObjectType;
	}
	public void setAssociationObjectType(FileHandleAssociateType associationObjectType) {
		this.associationObjectType = associationObjectType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associationObjectId == null) ? 0 : associationObjectId.hashCode());
		result = prime * result + ((associationObjectType == null) ? 0 : associationObjectType.hashCode());
		result = prime * result + ((fileHandleId == null) ? 0 : fileHandleId.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		FileDownload other = (FileDownload) obj;
		if (associationObjectId == null) {
			if (other.associationObjectId != null)
				return false;
		} else if (!associationObjectId.equals(other.associationObjectId))
			return false;
		if (associationObjectType != other.associationObjectType)
			return false;
		if (fileHandleId == null) {
			if (other.fileHandleId != null)
				return false;
		} else if (!fileHandleId.equals(other.fileHandleId))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
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
		return "FileDownload [userId=" + userId + ", timestamp=" + timestamp + ", fileHandleId=" + fileHandleId
				+ ", associationObjectId=" + associationObjectId + ", associationObjectType=" + associationObjectType
				+ "]";
	}
}
