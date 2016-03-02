package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class CertifiedQuizQuestionRecordDaoImpl implements CertifiedQuizQuestionRecordDao{

	public static final String CERTIFIED_QUIZ_QUESTION_RECORD_DDL_SQL = "CertifiedQuizQuestionRecord.ddl.sql";
	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_CERTIFIED_QUIZ_QUESTION_RECORD;
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_CERTIFIED_QUIZ_QUESTION_RECORD
			+ " WHERE "
			+ COL_CERTIFIED_QUIZ_QUESTION_RECORD_RESPONSE_ID
			+ " = ? AND "
			+ COL_CERTIFIED_QUIZ_QUESTION_RECORD_QUESTION_INDEX
			+ " = ?";
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_CERTIFIED_QUIZ_QUESTION_RECORD
			+ " ("
			+ COL_CERTIFIED_QUIZ_QUESTION_RECORD_RESPONSE_ID
			+ ","
			+ COL_CERTIFIED_QUIZ_QUESTION_RECORD_QUESTION_INDEX
			+ ","
			+ COL_CERTIFIED_QUIZ_QUESTION_RECORD_IS_CORRECT
			+ ") VALUES (?,?,?)";
	private JdbcTemplate template;
	RowMapper<CertifiedQuizQuestionRecord> rowMapper = new RowMapper<CertifiedQuizQuestionRecord>(){

		@Override
		public CertifiedQuizQuestionRecord mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			CertifiedQuizQuestionRecord record = new CertifiedQuizQuestionRecord();
			record.setResponseId(rs.getLong(COL_CERTIFIED_QUIZ_QUESTION_RECORD_RESPONSE_ID));
			record.setQuestionIndex(rs.getLong(COL_CERTIFIED_QUIZ_QUESTION_RECORD_QUESTION_INDEX));
			record.setIsCorrect(rs.getBoolean(COL_CERTIFIED_QUIZ_QUESTION_RECORD_IS_CORRECT));
			return record;
		}
	};

	@Inject
	CertifiedQuizQuestionRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<CertifiedQuizQuestionRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				CertifiedQuizQuestionRecord record = batch.get(i);
				ps.setLong(1, record.getResponseId());
				ps.setLong(2, record.getQuestionIndex());
				ps.setBoolean(3, record.getIsCorrect());
			}
		});
	}

	@Override
	public CertifiedQuizQuestionRecord get(Long responseId, Long questionIndex) {
		return template.queryForObject(SQL_GET, this.rowMapper, responseId, questionIndex);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
