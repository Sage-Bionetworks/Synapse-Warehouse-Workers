package org.sagebionetworks.warehouse.workers.db;

import java.sql.SQLException;

import org.sagebionetworks.warehouse.workers.db.FileState.State;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class FileMetadataDaoImpl implements FileMetadataDao {
	
	private static final String SQL_SELECT_BY_PRIMARY = "SELECT * FROM "+Sql.TABLE_FILE_STATE+" WHERE "+Sql.COL_TABLE_STATE_BUCKET+" = ? AND "+Sql.COL_TABLE_STATE_KEY+" = ?";
	JdbcTemplate template;
	BasicDao basicDao;
	RowMapper<FileState> rowMapper;
	
	@Inject
	public FileMetadataDaoImpl(ConnectionPool pool, BasicDao basicDao) throws SQLException {
		super();
		this.template = pool.createTempalte();
		this.basicDao = basicDao;
		this.rowMapper = new FileState().getRowMapper();
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.db.FileMetadataDao#getFileState(java.lang.String, java.lang.String)
	 */
	public FileState getFileState(String bucket, String key) {
		// first ensure the row exists
		FileState state = new FileState();
		state.setBucket(bucket);
		state.setKey(key);
		state.setState(State.UNKNOWN);
		basicDao.update(state);
		return template.queryForObject(SQL_SELECT_BY_PRIMARY, this.rowMapper, bucket, key);
	}

}
