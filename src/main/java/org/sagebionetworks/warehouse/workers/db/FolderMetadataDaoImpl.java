package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_FOLDER_STATE_BUCKET;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_FOLDER_STATE_PATH;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_FOLDER_STATE_STATE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_FOLDER_STATE_UPDATED_ON;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_FOLDER_STATE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.sagebionetworks.warehouse.workers.db.FolderState.State;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

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
	public void createOfUpdateFolderState(FolderState state) {
		if(state == null){
			throw new IllegalArgumentException("State cannot be null");
		}
		if(state.getBucket() == null){
			throw new IllegalArgumentException("Bucket cannot be null");
		}
		String stateString = FolderState.State.ROLLING.name();
		template.update(SQL_INSERT_DUPLICATE_UPDATE, state.getBucket(), state.getPath(), stateString,
				state.getUpdatedOn(), stateString, state.getUpdatedOn());
	}

	@Override
	public void truncateTable() {
		template.update(SQL_TRUCATE);
	}

	@Override
	public Iterator<FolderState> listFolders(String bucketName, State state) {
		// return the query with an iterator.
		return new QueryIterator<FolderState>(1000L, template, SQL_LIST_ROLLING_FOR_BUCKET, rowMapper, bucketName, FolderState.State.ROLLING.name());
	}
	



}
