package org.sagebionetworks.warehouse.workers.model;

/**
 * This class represents a single verification submission record.
 * 
 * While capturing the verification submission snapshot in a json format,
 * there are many fields that are not being used to generate metrics.
 * This class only contains fields that used to generate metrics.
 * 
 * A unique verification submission snapshot contains a unique verification
 * submission record.
 * 
 * A verification submission record represents a user submit a request to become
 * verified user. It contains information about the user and the time the submission
 * was made.
 * 
 * @author kimyentruong
 *
 */
public class VerificationSubmissionRecord {

	private Long id;
	private Long createdOn;
	private Long createdBy;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Long createdOn) {
		this.createdOn = createdOn;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		VerificationSubmissionRecord other = (VerificationSubmissionRecord) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "VerificationSubmissionRecord [id=" + id + ", createdOn="
				+ createdOn + ", createdBy=" + createdBy + "]";
	}
}
