package org.sagebionetworks.warehouse.workers.model;

public class UserActivityPerMonth {

	private Long userId;
	private String month;
	private Long uniqueDate;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Long getUniqueDate() {
		return uniqueDate;
	}
	public void setUniqueDate(Long uniqueDate) {
		this.uniqueDate = uniqueDate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + ((uniqueDate == null) ? 0 : uniqueDate.hashCode());
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
		UserActivityPerMonth other = (UserActivityPerMonth) obj;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (uniqueDate == null) {
			if (other.uniqueDate != null)
				return false;
		} else if (!uniqueDate.equals(other.uniqueDate))
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
		return "UserActivityPerMonth [userId=" + userId + ", month=" + month + ", uniqueDate=" + uniqueDate + "]";
	}
}
