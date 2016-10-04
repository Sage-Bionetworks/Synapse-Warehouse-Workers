package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class FileHandleRecordDaoImpl implements FileHandleRecordDao {

	public static final String FILE_HANDLE_RECORD_DDL_SQL = "FileHandleRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_FILE_HANDLE_RECORD,
			FILE_HANDLE_RECORD_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_FILE_HANDLE_RECORD;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_FILE_HANDLE_RECORD
			+ " ("
			+ COL_FILE_HANDLE_RECORD_ID
			+ ", "
			+ COL_FILE_HANDLE_RECORD_CREATED_ON
			+ ", "
			+ COL_FILE_HANDLE_RECORD_CREATED_BY
			+ ", "
			+ COL_FILE_HANDLE_RECORD_CONCRETE_TYPE
			+ ", "
			+ COL_FILE_HANDLE_RECORD_CONTENT_SIZE
			+ ", "
			+ COL_FILE_HANDLE_RECORD_BUCKET
			+ ", "
			+ COL_FILE_HANDLE_RECORD_KEY
			+ ", "
			+ COL_FILE_HANDLE_RECORD_FILE_NAME
			+ ", "
			+ COL_FILE_HANDLE_RECORD_CONTENT_MD5
			+ ", "
			+ COL_FILE_HANDLE_RECORD_STORAGE_LOCATION_ID
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_FILE_HANDLE_RECORD
			+ " WHERE "
			+ COL_FILE_HANDLE_RECORD_ID
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<FileHandleSnapshot> rowMapper = new RowMapper<FileHandleSnapshot>() {

		public FileHandleSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			FileHandleSnapshot record = new FileHandleSnapshot();
			record.setId(rs.getString(COL_FILE_HANDLE_RECORD_ID));
			record.setCreatedOn(new Date(rs.getLong(COL_FILE_HANDLE_RECORD_CREATED_ON)));
			record.setCreatedBy(rs.getString(COL_FILE_HANDLE_RECORD_CREATED_BY));
			record.setConcreteType(rs.getString(COL_FILE_HANDLE_RECORD_CONCRETE_TYPE));
			record.setFileName(rs.getString(COL_FILE_HANDLE_RECORD_FILE_NAME));

			Long size = rs.getLong(COL_FILE_HANDLE_RECORD_CONTENT_SIZE);
			if (!rs.wasNull()) record.setContentSize(size);
			String bucket = rs.getString(COL_FILE_HANDLE_RECORD_BUCKET);
			if (!rs.wasNull()) record.setBucket(bucket);
			String key = rs.getString(COL_FILE_HANDLE_RECORD_KEY);
			if (!rs.wasNull()) record.setKey(key);
			String md5 = rs.getString(COL_FILE_HANDLE_RECORD_CONTENT_MD5);
			if (!rs.wasNull()) record.setContentMd5(md5);
			Long storageLocationId = rs.getLong(COL_FILE_HANDLE_RECORD_STORAGE_LOCATION_ID);
			if (!rs.wasNull()) record.setStorageLocationId(storageLocationId);
			return record;
		}
	};

	@Inject
	FileHandleRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<FileHandleSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				FileHandleSnapshot record = batch.get(i);
				ps.setLong(1, Long.parseLong(record.getId()));
				ps.setLong(2, record.getCreatedOn().getTime());
				ps.setLong(3, Long.parseLong(record.getCreatedBy()));
				ps.setString(4, record.getConcreteType());
				if (record.getContentSize() != null) {
					ps.setLong(5, record.getContentSize());
				} else {
					ps.setNull(5, Types.BIGINT);
				}
				if (record.getBucket() != null) {
					ps.setString(6, record.getBucket());
				} else {
					ps.setNull(6, Types.VARCHAR);
				}
				if (record.getKey() != null) {
					ps.setString(7, record.getKey());
				} else {
					ps.setNull(7, Types.VARCHAR);
				}
				ps.setString(8, record.getFileName());
				if (record.getContentMd5() != null) {
					ps.setString(9, record.getContentMd5());
				} else {
					ps.setNull(9, Types.VARCHAR);
				}
				if (record.getStorageLocationId() != null) {
					ps.setLong(10, record.getStorageLocationId());
				} else {
					ps.setNull(10, Types.BIGINT);
				}
			}
		});
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	@Override
	public FileHandleSnapshot get(Long fileHandleId) {
		return template.queryForObject(SQL_GET, this.rowMapper, fileHandleId);
	}

}
