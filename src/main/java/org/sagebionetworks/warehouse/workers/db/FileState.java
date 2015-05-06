package org.sagebionetworks.warehouse.workers.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;


/**
 * State of a file.
 *
 */
public class FileState implements DatabaseObject<FileState> {
	
	public enum State {
		UNKNOWN,
		PROCESSING,
		COMPLETE,
		FAILED
	}
	
	private String bucket;
	private String key;
	private State state;
	private Date updatedOn;
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
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
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
	public String getCreateTableStatement() {
		return ClasspathUtils.loadStringFromClassPath("FileState.ddl.sql");
	}
	
	public String getInsertStatement() {
		return "INSERT IGNORE INTO "+Sql.TABLE_FILE_STATE+" ("+Sql.COL_TABLE_STATE_BUCKET+","+Sql.COL_TABLE_STATE_KEY+","+Sql.COL_TABLE_STATE_STATE+") VALUES (?,?,?)";
	}

	/**
	 * Map a query to a new object.
	 */
	public RowMapper<FileState> getRowMapper() {
		return new RowMapper<FileState>() {
			
			public FileState mapRow(ResultSet rs, int rowNum) throws SQLException {
				FileState file = new FileState();
				file.setBucket(rs.getString(Sql.COL_TABLE_STATE_BUCKET));
				file.setKey(rs.getString(Sql.COL_TABLE_STATE_KEY));
				file.setState(State.valueOf(rs.getString(Sql.COL_TABLE_STATE_STATE)));
				return file;
			}
		};
	}
	
	/**
	 * Write this object to a prepared statement.
	 */
	public void setValues(PreparedStatement ps) throws SQLException {
		ps.setString(1, bucket);
		ps.setString(2, key);
		ps.setString(3, state.name());
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
