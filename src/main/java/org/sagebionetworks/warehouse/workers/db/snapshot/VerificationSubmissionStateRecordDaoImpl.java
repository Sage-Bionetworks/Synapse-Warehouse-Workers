package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class VerificationSubmissionStateRecordDaoImpl implements VerificationSubmissionStateRecordDao{

	public static final String VERIFICATION_SUBMISSION_STATE_RECORD_DDL_SQL = "VerificationSubmissionStateRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_VERIFICATION_SUBMISSION_STATE_RECORD,
			VERIFICATION_SUBMISSION_STATE_RECORD_DDL_SQL,
			false,
			null,
			null);
	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_VERIFICATION_SUBMISSION_STATE_RECORD;
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_VERIFICATION_SUBMISSION_STATE_RECORD
			+ " WHERE "
			+ COL_VERIFICATION_SUBMISSION_STATE_RECORD_ID
			+ " = ? AND "
			+COL_VERIFICATION_SUBMISSION_STATE_RECORD_STATE
			+ " = ?";
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_VERIFICATION_SUBMISSION_STATE_RECORD
			+ " ("
			+ COL_VERIFICATION_SUBMISSION_STATE_RECORD_ID
			+ ","
			+ COL_VERIFICATION_SUBMISSION_STATE_RECORD_STATE
			+ ","
			+ COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_ON
			+ ","
			+ COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_BY
			+ ") VALUES (?,?,?,?)";
	private JdbcTemplate template;
	RowMapper<VerificationSubmissionStateRecord> rowMapper = new RowMapper<VerificationSubmissionStateRecord>(){

		@Override
		public VerificationSubmissionStateRecord mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			VerificationSubmissionStateRecord record = new VerificationSubmissionStateRecord();
			record.setId(rs.getLong(COL_VERIFICATION_SUBMISSION_STATE_RECORD_ID));
			record.setState(VerificationStateEnum.valueOf(rs.getString(COL_VERIFICATION_SUBMISSION_STATE_RECORD_STATE)));
			record.setCreatedOn(rs.getLong(COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_ON));
			record.setCreatedBy(rs.getLong(COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_BY));
			return record;
		}
	};

	@Inject
	VerificationSubmissionStateRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<VerificationSubmissionStateRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				VerificationSubmissionStateRecord record = batch.get(i);
				ps.setLong(1, record.getId());
				ps.setString(2, record.getState().name());
				ps.setLong(3, record.getCreatedOn());
				ps.setLong(4, record.getCreatedBy());
			}
		});
	}

	@Override
	public VerificationSubmissionStateRecord get(Long id, VerificationStateEnum state) {
		return template.queryForObject(SQL_GET, this.rowMapper, id, state.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}
}
