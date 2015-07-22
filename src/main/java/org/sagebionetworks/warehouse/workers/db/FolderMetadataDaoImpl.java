package org.sagebionetworks.warehouse.workers.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

public class FolderMetadataDaoImpl implements FolderMetadataDao {

	private static final String SQL_LIST_ROLLING_FOR_BUCKET = "SELECT * FROM "
			+ TABLE_FOLDER_STATE + " WHERE " + COL_FOLDER_STATE_BUCKET
			+ " = ? AND " + COL_FOLDER_STATE_STATE + " = ?";

	private static final String SQL_INSERT_DUPLICATE_UPDATE = "INSERT INTO "
			+ TABLE_FOLDER_STATE + " (" + COL_FOLDER_STATE_BUCKET + ", "
			+ COL_FOLDER_STATE_PATH + ", " + COL_FOLDER_STATE_STATE + ", "
			+ COL_FOLDER_STATE_UPDATED_ON
			+ ") VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ COL_FOLDER_STATE_STATE + "= ? , " + COL_FOLDER_STATE_UPDATED_ON
			+ " = ?";

	private static final String SQL_TRUCATE = "TRUNCATE TABLE "
			+ TABLE_FOLDER_STATE;

	private static final String FOLDER_STATE_DDL_SQL = "FolderState.ddl.sql";
	JdbcTemplate template;

	RowMapper<FolderState> rowMapper = new RowMapper<FolderState>() {

		public FolderState mapRow(ResultSet rs, int arg1) throws SQLException {
			FolderState folder = new FolderState();
			folder.setBucket(rs.getString(COL_FOLDER_STATE_BUCKET));
			folder.setPath(rs.getString(COL_FOLDER_STATE_PATH));
			folder.setState(FolderState.State.valueOf(rs
					.getString(COL_FOLDER_STATE_STATE)));
			folder.setUpdatedOn(rs.getTimestamp(COL_FOLDER_STATE_UPDATED_ON));
			return folder;
		}
	};

	@Inject
	public FolderMetadataDaoImpl(JdbcTemplate template) {
		super();
		this.template = template;
		// Create the table
		this.template.update(ClasspathUtils
				.loadStringFromClassPath(FOLDER_STATE_DDL_SQL));
	}

	@Override
	public void markFolderAsRolling(String bucketName, String path,
			long modifiedOnTimeMS) {
		String state = FolderState.State.ROLLING.name();
		Timestamp modifiedDate = new Timestamp(modifiedOnTimeMS);
		template.update(SQL_INSERT_DUPLICATE_UPDATE, bucketName, path, state,
				modifiedDate, state, modifiedDate);
	}

	@Override
	public List<FolderState> listRollingFolders(String bucketName) {
		return template.query(SQL_LIST_ROLLING_FOR_BUCKET, rowMapper,
				bucketName, FolderState.State.ROLLING.name());
	}

	@Override
	public void truncateTable() {
		template.update(SQL_TRUCATE);
	}

}
