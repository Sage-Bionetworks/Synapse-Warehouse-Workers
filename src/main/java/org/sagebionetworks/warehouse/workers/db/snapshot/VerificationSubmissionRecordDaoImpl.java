package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class VerificationSubmissionRecordDaoImpl implements VerificationSubmissionRecordDao{

	public static final String VERIFICATION_SUBMISSION_RECORD_DDL_SQL = "VerificationSubmissionRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_VERIFICATION_SUBMISSION_RECORD,
			VERIFICATION_SUBMISSION_RECORD_DDL_SQL,
			false,
			null,
			null);
	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_VERIFICATION_SUBMISSION_RECORD;
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_VERIFICATION_SUBMISSION_RECORD
			+ " WHERE "
			+ COL_VERIFICATION_SUBMISSION_RECORD_ID
			+ " = ?";
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_VERIFICATION_SUBMISSION_RECORD
			+ " ("
			+ COL_VERIFICATION_SUBMISSION_RECORD_ID
			+ ","
			+ COL_VERIFICATION_SUBMISSION_RECORD_CREATED_ON
			+ ","
			+ COL_VERIFICATION_SUBMISSION_RECORD_CREATED_BY
			+ ") VALUES (?,?,?)";
	private JdbcTemplate template;
	RowMapper<VerificationSubmissionRecord> rowMapper = new RowMapper<VerificationSubmissionRecord>(){

		@Override
		public VerificationSubmissionRecord mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			VerificationSubmissionRecord record = new VerificationSubmissionRecord();
			record.setId(rs.getLong(COL_VERIFICATION_SUBMISSION_RECORD_ID));
			record.setCreatedOn(rs.getLong(COL_VERIFICATION_SUBMISSION_RECORD_CREATED_ON));
			record.setCreatedBy(rs.getLong(COL_VERIFICATION_SUBMISSION_RECORD_CREATED_BY));
			return record;
		}
	};

	@Inject
	VerificationSubmissionRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<VerificationSubmissionRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				VerificationSubmissionRecord record = batch.get(i);
				ps.setLong(1, record.getId());
				ps.setLong(2, record.getCreatedOn());
				ps.setLong(3, record.getCreatedBy());
			}
		});
	}

	@Override
	public VerificationSubmissionRecord get(Long id) {
		return template.queryForObject(SQL_GET, this.rowMapper, id);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
