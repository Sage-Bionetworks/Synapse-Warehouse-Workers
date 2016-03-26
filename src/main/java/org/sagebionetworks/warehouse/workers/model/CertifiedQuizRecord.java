package org.sagebionetworks.warehouse.workers.model;

/**
 * This class represents the certified quiz record.
 * 
 * While capturing the passing record snapshot in a json format,
 * there are many fields that are not being used to generate meaningful metrics.
 * This class only contains the fields that are used to generate metrics.
 * 
 * A unique passing record contains a unique certified quiz record.
 * 
 * A certified quiz record represents a user taking a quiz. It contains
 * information about the user, the time the quiz was taken, and either the user
 * has passed the quiz.
 * 
 * @author kimyentruong
 *
 */
public class CertifiedQuizRecord {

	private Long responseId;
	private Long userId;
	private Boolean passed;
	private Long passedOn;

	public Long getResponseId() {
		return responseId;
	}
	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Boolean getPassed() {
		return passed;
	}
	public void setPassed(Boolean passed) {
		this.passed = passed;
	}
	public Long getPassedOn() {
		return passedOn;
	}
	public void setPassedOn(Long passedBy) {
		this.passedOn = passedBy;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((passed == null) ? 0 : passed.hashCode());
		result = prime * result
				+ ((passedOn == null) ? 0 : passedOn.hashCode());
		result = prime * result
				+ ((responseId == null) ? 0 : responseId.hashCode());
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
		CertifiedQuizRecord other = (CertifiedQuizRecord) obj;
		if (passed == null) {
			if (other.passed != null)
				return false;
		} else if (!passed.equals(other.passed))
			return false;
		if (passedOn == null) {
			if (other.passedOn != null)
				return false;
		} else if (!passedOn.equals(other.passedOn))
			return false;
		if (responseId == null) {
			if (other.responseId != null)
				return false;
		} else if (!responseId.equals(other.responseId))
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
		return "CertifiedQuizRecord [responseId=" + responseId + ", userId="
				+ userId + ", passed=" + passed + ", passedOn=" + passedOn
				+ "]";
	}
}
