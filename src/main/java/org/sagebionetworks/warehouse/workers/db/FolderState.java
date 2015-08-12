package org.sagebionetworks.warehouse.workers.db;

import java.sql.Timestamp;

/**
 * Represents the state of a single folder (or path).
 *
 */
public class FolderState {
	
	public enum State {
		// A rolling folder contains non-collated files.
		ROLLING,
		// All files in this folder are collated.
		COLLATED,
	}
	
	private String bucket;
	private String path;
	private State state;
	private Timestamp updatedOn;
	
	public FolderState(){
	}
	
	/**
	 * 
	 * @param bucket The name of the bucket containing the folder.
	 * @param path The full path of the folder.
	 * @param state The current state of the folder.
	 * @param updatedOn  When this folder last updated.
	 */
	public FolderState(String bucket, String path, State state,
			Timestamp updatedOn) {
		super();
		this.bucket = bucket;
		this.path = path;
		this.state = state;
		this.updatedOn = updatedOn;
	}

	/**
	 * The name of the bucket containing the folder.
	 * @return
	 */
	public String getBucket() {
		return bucket;
	}
	
	/**
	 * The name of the bucket containing the folder.
	 * @param bucket
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	
	/**
	 * The full path of the folder.
	 * @return
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * The full path of the folder.
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * The current state of the folder.
	 * @return
	 */
	public State getState() {
		return state;
	}
	/**
	 * The current state of the folder.
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * When was this folder last updated.
	 * @return
	 */
	public Timestamp getUpdatedOn() {
		return updatedOn;
	}
	
	/**
	 * When was this folder last updated.
	 * @param updatedOn
	 */
	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		FolderState other = (FolderState) obj;
		if (bucket == null) {
			if (other.bucket != null)
				return false;
		} else if (!bucket.equals(other.bucket))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
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
		return "FolderState [bucket=" + bucket + ", path=" + path + ", state="
				+ state + ", updatedOn=" + updatedOn + "]";
	}
	
	
}
