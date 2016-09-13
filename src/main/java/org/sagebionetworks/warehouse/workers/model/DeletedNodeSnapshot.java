package org.sagebionetworks.warehouse.workers.model;

public class DeletedNodeSnapshot {

	long id;
	long timestamp;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		DeletedNodeSnapshot other = (DeletedNodeSnapshot) obj;
		if (id != other.id)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "DeletedNodeSnapshot [id=" + id + ", timestamp=" + timestamp + "]";
	}
}
