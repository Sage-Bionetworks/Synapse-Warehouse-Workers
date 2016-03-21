package org.sagebionetworks.warehouse.workers.model;

import org.sagebionetworks.repo.model.verification.VerificationStateEnum;

/**
 * This class represents a single verification submission state record.
 * 
 * While capturing the verification submission snapshot in a json format,
 * there are many fields that are not being used to generate metrics.
 * This class only contains fields that used to generate metrics.
 * 
 * A unique verification submission snapshot contains various number of
 *  verification submission state records.
 * 
 * A verification submission state record represents a state of a submit a
 * verification submission. It contains information about the submission, the
 * state, the time the state was created, and the user who created this state.
 * 
 * @author kimyentruong
 *
 */
public class VerificationSubmissionStateRecord {

	private Long id;
	private Long createdOn;
	private Long createdBy;
	private VerificationStateEnum state;

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
	public VerificationStateEnum getState() {
		return state;
	}
	public void setState(VerificationStateEnum state) {
		this.state = state;
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
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		VerificationSubmissionStateRecord other = (VerificationSubmissionStateRecord) obj;
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
		if (state != other.state)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "VerificationSubmissionStateRecord [id=" + id + ", createdOn="
				+ createdOn + ", createdBy=" + createdBy + ", state=" + state
				+ "]";
	}
}
