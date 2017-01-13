package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class FileHandleCopyRecordDaoImpl implements FileHandleCopyRecordDao {

	public static final String FILE_HANDLE_COPY_RECORD_DDL_SQL = "FileHandleCopyRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_FILE_HANDLE_COPY_RECORD,
			FILE_HANDLE_COPY_RECORD_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_FILE_HANDLE_COPY_RECORD;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_FILE_HANDLE_COPY_RECORD
			+ " ("
			+ COL_FILE_HANDLE_COPY_RECORD_TIMESTAMP
			+ ","
			+ COL_FILE_HANDLE_COPY_RECORD_USER_ID
			+ ","
			+ COL_FILE_HANDLE_COPY_RECORD_ORIGINAL_FILE_HANDLE_ID
			+ ","
			+ COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_ID
			+ ","
			+ COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_TYPE
			+ ","
			+ COL_FILE_HANDLE_COPY_RECORD_NEW_FILE_HANDLE_ID
			+ ") VALUES (?,?,?,?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_FILE_HANDLE_COPY_RECORD
			+ " WHERE "
			+ COL_FILE_HANDLE_COPY_RECORD_TIMESTAMP
			+ " = ? AND "
			+ COL_FILE_HANDLE_COPY_RECORD_USER_ID
			+ " = ? AND "
			+ COL_FILE_HANDLE_COPY_RECORD_ORIGINAL_FILE_HANDLE_ID
			+ " = ? AND "
			+ COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_ID
			+ " = ? AND "
			+ COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_TYPE
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<FileHandleCopyRecordSnapshot> rowMapper = new RowMapper<FileHandleCopyRecordSnapshot>() {

		public FileHandleCopyRecordSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			FileHandleCopyRecordSnapshot record = new FileHandleCopyRecordSnapshot();
			record.setTimestamp(rs.getLong(COL_FILE_HANDLE_COPY_RECORD_TIMESTAMP));
			record.setUserId(rs.getLong(COL_FILE_HANDLE_COPY_RECORD_USER_ID));
			record.setOriginalFileHandleId(rs.getLong(COL_FILE_HANDLE_COPY_RECORD_ORIGINAL_FILE_HANDLE_ID));
			record.setAssociationObjectId(rs.getLong(COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_ID));
			record.setAssociationObjectType(FileHandleAssociateType.valueOf(rs.getString(COL_FILE_HANDLE_COPY_RECORD_ASSOCIATION_OBJECT_TYPE)));
			record.setNewFileHandleId(rs.getLong(COL_FILE_HANDLE_COPY_RECORD_NEW_FILE_HANDLE_ID));
			return record;
		}
	};

	@Inject
	FileHandleCopyRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<FileHandleCopyRecordSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				FileHandleCopyRecordSnapshot record = batch.get(i);
				ps.setLong(1, record.getTimestamp());
				ps.setLong(2, record.getUserId());
				ps.setLong(3, record.getOriginalFileHandleId());
				ps.setLong(4, record.getAssociationObjectId());
				ps.setString(5, record.getAssociationObjectType().name());
				ps.setLong(6, record.getNewFileHandleId());
			}
		});
	}

	@Override
	public FileHandleCopyRecordSnapshot get(Long timestamp, Long userId, Long originalFileHandleId, Long objectId, FileHandleAssociateType objectType) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, userId, originalFileHandleId, objectId, objectType.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}
}
