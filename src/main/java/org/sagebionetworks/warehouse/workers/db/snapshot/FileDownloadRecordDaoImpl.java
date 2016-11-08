package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.FileDownload;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class FileDownloadRecordDaoImpl implements FileDownloadRecordDao {

	public static final String FILE_DOWNLOAD_DDL_SQL = "FileDownloadRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_FILE_DOWNLOAD_RECORD,
			FILE_DOWNLOAD_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_FILE_DOWNLOAD_RECORD;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_FILE_DOWNLOAD_RECORD
			+ " ("
			+ COL_FILE_DOWNLOAD_TIMESTAMP
			+ ","
			+ COL_FILE_DOWNLOAD_USER_ID
			+ ","
			+ COL_FILE_DOWNLOAD_FILE_HANDLE_ID
			+ ","
			+ COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_ID
			+ ","
			+ COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_TYPE
			+ ") VALUES (?,?,?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_FILE_DOWNLOAD_RECORD
			+ " WHERE "
			+ COL_FILE_DOWNLOAD_TIMESTAMP
			+ " = ? AND "
			+ COL_FILE_DOWNLOAD_USER_ID
			+ " = ? AND "
			+ COL_FILE_DOWNLOAD_FILE_HANDLE_ID
			+ " = ? AND "
			+ COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_ID
			+ " = ? AND "
			+ COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_TYPE
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<FileDownload> rowMapper = new RowMapper<FileDownload>() {

		public FileDownload mapRow(ResultSet rs, int arg1) throws SQLException {
			FileDownload record = new FileDownload();
			record.setTimestamp(rs.getLong(COL_FILE_DOWNLOAD_TIMESTAMP));
			record.setUserId(rs.getLong(COL_FILE_DOWNLOAD_USER_ID));
			record.setFileHandleId(rs.getLong(COL_FILE_DOWNLOAD_FILE_HANDLE_ID));
			record.setAssociationObjectId(rs.getLong(COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_ID));
			record.setAssociationObjectType(FileHandleAssociateType.valueOf(rs.getString(COL_FILE_DOWNLOAD_ASSOCIATION_OBJECT_TYPE)));
			return record;
		}
	};

	@Inject
	FileDownloadRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<FileDownload> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				FileDownload record = batch.get(i);
				ps.setLong(1, record.getTimestamp());
				ps.setLong(2, record.getUserId());
				ps.setLong(3, record.getFileHandleId());
				ps.setLong(4, record.getAssociationObjectId());
				ps.setString(5, record.getAssociationObjectType().name());
			}
		});
	}

	@Override
	public FileDownload get(Long timestamp, Long userId, Long fileHandleId, Long objectId, FileHandleAssociateType objectType) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, userId, fileHandleId, objectId, objectType.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}
}
