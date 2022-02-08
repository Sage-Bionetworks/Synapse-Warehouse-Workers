package org.sagebionetworks.warehouse.workers.model;

public class UserActivityPerClientPerDay {

	private Client client;
	private Long userId;
	private String xForwardedFor;
	private String date;
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getXForwardedFor() { return this.xForwardedFor; }
	public void setXForwardedFor(String xForwardedFor)  { this.xForwardedFor = xForwardedFor; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((xForwardedFor == null) ? 0 : xForwardedFor.hashCode());
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
		UserActivityPerClientPerDay other = (UserActivityPerClientPerDay) obj;
		if (client != other.client)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (xForwardedFor == null) {
			if (other.xForwardedFor != null) {
				return false;
			}
		} else if (!xForwardedFor.equals(other.xForwardedFor))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UserAccessRecord [client=" + client + ", userId=" + userId + ", date=" + date + "xForwardedFor=" + xForwardedFor + "]";
	}
}
