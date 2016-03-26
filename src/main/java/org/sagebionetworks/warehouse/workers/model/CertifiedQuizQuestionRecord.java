package org.sagebionetworks.warehouse.workers.model;

/**
 * This class represents the certified quiz question record.
 * 
 * While capturing the passing record snapshot in a json format,
 * there are many fields that are not being used to generate meaningful metrics.
 * This class only contains the fields that are used to generate metrics.
 * 
 * A unique passing record contains 10 certified quiz question record.
 * 
 * A certified quiz question record represents user answer to one question in
 * the certified user quiz. It has information about the quiz, the question,
 * and either the user answered the question correct or not.
 * 
 * @author kimyentruong
 *
 */
public class CertifiedQuizQuestionRecord {

	private Long responseId;
	private Long questionIndex;
	private Boolean isCorrect;

	public Long getResponseId() {
		return responseId;
	}
	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}
	public Long getQuestionIndex() {
		return questionIndex;
	}
	public void setQuestionIndex(Long questionIndex) {
		this.questionIndex = questionIndex;
	}
	public Boolean getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((isCorrect == null) ? 0 : isCorrect.hashCode());
		result = prime * result
				+ ((questionIndex == null) ? 0 : questionIndex.hashCode());
		result = prime * result
				+ ((responseId == null) ? 0 : responseId.hashCode());
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
		CertifiedQuizQuestionRecord other = (CertifiedQuizQuestionRecord) obj;
		if (isCorrect == null) {
			if (other.isCorrect != null)
				return false;
		} else if (!isCorrect.equals(other.isCorrect))
			return false;
		if (questionIndex == null) {
			if (other.questionIndex != null)
				return false;
		} else if (!questionIndex.equals(other.questionIndex))
			return false;
		if (responseId == null) {
			if (other.responseId != null)
				return false;
		} else if (!responseId.equals(other.responseId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CertifiedQuizQuestionRecord [responseId=" + responseId
				+ ", questionIndex=" + questionIndex + ", isCorrect="
				+ isCorrect + "]";
	}
}
