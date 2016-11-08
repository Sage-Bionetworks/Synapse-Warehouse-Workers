package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;

/**
 * This class represents a file handle download record.
 * 
 * A file download record includes user ID who performed the download, the
 * timestamp of the download, the actual downloaded fileHandleId, the requested
 * fileHandleId, associationObjectType and associationObjectId.
 * 
 * @author kimyentruong
 *
 */
public class FileHandleDownload {

	Long userId;
	Long timestamp;
	Long downloadedFileHandleId;
	Long requestedFileHandleId;
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
	public Long getDownloadedFileHandleId() {
		return downloadedFileHandleId;
	}
	public void setDownloadedFileHandleId(Long downloadedFileHandleId) {
		this.downloadedFileHandleId = downloadedFileHandleId;
	}
	public Long getRequestedFileHandleId() {
		return requestedFileHandleId;
	}
	public void setRequestedFileHandleId(Long requestedFileHandleId) {
		this.requestedFileHandleId = requestedFileHandleId;
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
		result = prime * result + ((downloadedFileHandleId == null) ? 0 : downloadedFileHandleId.hashCode());
		result = prime * result + ((requestedFileHandleId == null) ? 0 : requestedFileHandleId.hashCode());
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
		FileHandleDownload other = (FileHandleDownload) obj;
		if (associationObjectId == null) {
			if (other.associationObjectId != null)
				return false;
		} else if (!associationObjectId.equals(other.associationObjectId))
			return false;
		if (associationObjectType != other.associationObjectType)
			return false;
		if (downloadedFileHandleId == null) {
			if (other.downloadedFileHandleId != null)
				return false;
		} else if (!downloadedFileHandleId.equals(other.downloadedFileHandleId))
			return false;
		if (requestedFileHandleId == null) {
			if (other.requestedFileHandleId != null)
				return false;
		} else if (!requestedFileHandleId.equals(other.requestedFileHandleId))
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
		return "FileHandleDownload [userId=" + userId + ", timestamp=" + timestamp + ", downloadedFileHandleId="
				+ downloadedFileHandleId + ", requestedFileHandleId=" + requestedFileHandleId + ", associationObjectId="
				+ associationObjectId + ", associationObjectType=" + associationObjectType + "]";
	}

}
