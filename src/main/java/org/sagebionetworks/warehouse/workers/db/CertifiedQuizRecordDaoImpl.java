package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class CertifiedQuizRecordDaoImpl implements CertifiedQuizRecordDao{

	public static final String CERTIFIED_QUIZ_RECORD_DDL_SQL = "CertifiedQuizRecord.ddl.sql";
	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_CERTIFIED_QUIZ_RECORD;
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_CERTIFIED_QUIZ_RECORD
			+ " WHERE "
			+ COL_CERTIFIED_QUIZ_RECORD_RESPONSE_ID
			+ " = ?";
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_CERTIFIED_QUIZ_RECORD
			+ " ("
			+ COL_CERTIFIED_QUIZ_RECORD_RESPONSE_ID
			+ ","
			+ COL_CERTIFIED_QUIZ_RECORD_USER_ID
			+ ","
			+ COL_CERTIFIED_QUIZ_RECORD_PASSED
			+ ","
			+ COL_CERTIFIED_QUIZ_RECORD_PASSED_ON
			+ ") VALUES (?,?,?,?)";
	private JdbcTemplate template;
	RowMapper<CertifiedQuizRecord> rowMapper = new RowMapper<CertifiedQuizRecord>(){

		@Override
		public CertifiedQuizRecord mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CertifiedQuizRecord record = new CertifiedQuizRecord();
			record.setResponseId(rs.getLong(COL_CERTIFIED_QUIZ_RECORD_RESPONSE_ID));
			record.setUserId(rs.getLong(COL_CERTIFIED_QUIZ_RECORD_USER_ID));
			record.setPassed(rs.getBoolean(COL_CERTIFIED_QUIZ_RECORD_PASSED));
			record.setPassedOn(rs.getLong(COL_CERTIFIED_QUIZ_RECORD_PASSED_ON));
			return record;
		}
	};

	@Inject
	CertifiedQuizRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<CertifiedQuizRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				CertifiedQuizRecord record = batch.get(i);
				ps.setLong(1, record.getResponseId());
				ps.setLong(2, record.getUserId());
				ps.setBoolean(3, record.getPassed());
				ps.setLong(4, record.getPassedOn());
			}
		});
	}

	@Override
	public CertifiedQuizRecord get(Long responseId) {
		return template.queryForObject(SQL_GET, this.rowMapper, responseId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
