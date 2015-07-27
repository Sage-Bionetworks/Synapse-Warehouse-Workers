package org.sagebionetworks.warehouse.workers.db;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;


/**
 * State of a file.
 *
 */
public class FileState {
	
	public enum State {
		UNKNOWN,
		SUBMITTED,
		FAILED
	}
	
	private String bucket;
	private String key;
	private State state;
	private Timestamp updatedOn;
	private String error;
	private byte[] errorDetails;
	
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Timestamp getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public byte[] getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(byte[] errorDetails) {
		this.errorDetails = errorDetails;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + Arrays.hashCode(errorDetails);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((updatedOn == null) ? 0 : updatedOn.hashCode());
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
		FileState other = (FileState) obj;
		if (bucket == null) {
			if (other.bucket != null)
				return false;
		} else if (!bucket.equals(other.bucket))
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (!Arrays.equals(errorDetails, other.errorDetails))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (state != other.state)
			return false;
		if (updatedOn == null) {
			if (other.updatedOn != null)
				return false;
		} else if (!updatedOn.equals(other.updatedOn))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileState [bucket=" + bucket + ", key=" + key + ", state="
				+ state + ", updatedOn=" + updatedOn + ", error=" + error
				+ ", errorDetails=" + Arrays.toString(errorDetails) + "]";
	}

}
