package org.sagebionetworks.warehouse.workers.db;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.sagebionetworks.warehouse.workers.db.FileState.State;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;
import static org.sagebionetworks.warehouse.workers.db.FileState.State.*;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FileMetadataDaoImpl implements FileMetadataDao {

	private static final int ERROR_MESSAGE_MAX_CHARS = 2999;

	private static final String SQL_UPDATE_STATE = "UPDATE " + TABLE_FILE_STATE + " SET "
			+ COL_TABLE_STATE_STATE + " = ? , " + COL_TABLE_STATE_ERROR
			+ " =  ?, " + COL_TABLE_STATE_ERROR_DETAILS + " = ? WHERE "
			+ COL_TABLE_STATE_BUCKET + " = ? AND " + COL_TABLE_STATE_KEY
			+ " = ?";

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_FILE_STATE;

	private static final String FILE_STATE_DDL_SQL = "FileState.ddl.sql";

	private static final String SQL_SELECT_BY_PRIMARY = "SELECT * FROM "
			+ TABLE_FILE_STATE + " WHERE " + COL_TABLE_STATE_BUCKET
			+ " = ? AND " + COL_TABLE_STATE_KEY + " = ?";
	
	private static final String INSERT_IGNORE_FILE_STATE = "INSERT IGNORE INTO "
			+ TABLE_FILE_STATE
			+ " ("
			+ COL_TABLE_STATE_BUCKET
			+ ","
			+ COL_TABLE_STATE_KEY
			+ ","
			+ COL_TABLE_STATE_STATE
			+ ") VALUES (?,?,?)";

	JdbcTemplate template;
	
	/*
	 * Map all columns to the dba.
	 */
	RowMapper<FileState> rowMapper = new RowMapper<FileState>() {

		public FileState mapRow(ResultSet rs, int arg1) throws SQLException {
			FileState file = new FileState();
			file.setBucket(rs.getString(COL_TABLE_STATE_BUCKET));
			file.setKey(rs.getString(COL_TABLE_STATE_KEY));
			file.setState(State.valueOf(rs.getString(COL_TABLE_STATE_STATE)));
			file.setUpdatedOn(rs.getTimestamp(COL_TABLE_STATE_UPDATED_ON));
			file.setError(rs.getString(COL_TABLE_STATE_ERROR));
			file.setErrorDetails(rs.getBytes(COL_TABLE_STATE_ERROR_DETAILS));
			return file;
		}
	};

	@Inject
	FileMetadataDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
		// Create the table
		this.template.update(ClasspathUtils
				.loadStringFromClassPath(FILE_STATE_DDL_SQL));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sagebionetworks.warehouse.workers.db.FileMetadataDao#getFileState
	 * (java.lang.String, java.lang.String)
	 */
	public FileState getFileState(String bucket, String key) {
		// first ensure the row exists
		template.update(INSERT_IGNORE_FILE_STATE, bucket, key, UNKNOWN.name());
		// Return the object from the DB.
		return template.queryForObject(SQL_SELECT_BY_PRIMARY, this.rowMapper,
				bucket, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sagebionetworks.warehouse.workers.db.FileMetadataDao#truncateAll()
	 */
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.FileMetadataDao#setFileStateFailed(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void setFileStateFailed(String bucket, String key, Throwable reason) {
		setFileState(bucket, key, FAILED, reason);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.FileMetadataDao#setFileState(java.lang.String, java.lang.String, org.sagebionetworks.warehouse.workers.db.FileState.State)
	 */
	public void setFileState(String bucket, String key, State state) {
		setFileState(bucket, key, state, null);
	}

	/**
	 * Update the state of a file.
	 * @param bucket Bucket of the file.
	 * @param key Key of the file. 
	 * @param state State of the file.
	 * @param reason If null then error and errorDetails will be null.
	 */
	private void setFileState(String bucket, String key, State state,
			Throwable reason) {
		if(bucket == null){
			throw new IllegalArgumentException("Bucket cannot be null");
		}
		if(key == null){
			throw new IllegalArgumentException("Key cannot be null");
		}
		if(state == null){
			throw new IllegalArgumentException("State cannot be null");
		}
		byte[] errorDetails = null;
		String errorMessage = null;
		if (reason != null) {
			try {
				errorDetails = ExceptionUtils.getStackTrace(reason).getBytes(
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			errorMessage = reason.getMessage();
			if(errorMessage.length() > ERROR_MESSAGE_MAX_CHARS){
				// truncate.
				errorMessage = reason.getMessage().substring(0, ERROR_MESSAGE_MAX_CHARS);
			}
		}
		template.update(SQL_UPDATE_STATE, state.name(), errorMessage, errorDetails, bucket, key);
	}

}
