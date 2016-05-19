package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class BulkFileDownloadRecordDaoImpl implements BulkFileDownloadRecordDao {

	public static final String BULK_FILE_DOWNLOAD_RECORD_DDL_SQL = "BulkFileDownloadRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_BULK_FILE_DOWNLOAD_RECORD,
			BULK_FILE_DOWNLOAD_RECORD_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_BULK_FILE_DOWNLOAD_RECORD;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_BULK_FILE_DOWNLOAD_RECORD
			+ " ("
			+ COL_BULK_FILE_DOWNLOAD_RECORD_USER_ID
			+ ","
			+ COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_ID
			+ ","
			+ COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_TYPE
			+ ") VALUES (?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_BULK_FILE_DOWNLOAD_RECORD
			+ " WHERE "
			+ COL_BULK_FILE_DOWNLOAD_RECORD_USER_ID
			+ " = ? AND "
			+ COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_ID
			+ " = ? AND "
			+ COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_TYPE
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<BulkFileDownloadRecord> rowMapper = new RowMapper<BulkFileDownloadRecord>() {

		public BulkFileDownloadRecord mapRow(ResultSet rs, int arg1) throws SQLException {
			BulkFileDownloadRecord record = new BulkFileDownloadRecord();
			record.setUserId(rs.getLong(COL_BULK_FILE_DOWNLOAD_RECORD_USER_ID));
			record.setObjectId(rs.getLong(COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_ID));
			record.setObjectType(FileHandleAssociateType.valueOf(rs.getString(COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_TYPE)));
			return record;
		}
	};

	@Inject
	BulkFileDownloadRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<BulkFileDownloadRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				BulkFileDownloadRecord record = batch.get(i);
				ps.setLong(1, record.getUserId());
				ps.setLong(2, record.getObjectId());
				ps.setString(3, record.getObjectType().name());
			}
		});
	}

	@Override
	public BulkFileDownloadRecord get(Long userId, Long objectId, FileHandleAssociateType objectType) {
		return template.queryForObject(SQL_GET, this.rowMapper, userId, objectId, objectType.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
