package org.sagebionetworks.warehouse.workers.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProcessedAccessRecordDaoImpl implements ProcessedAccessRecordDao {

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_PROCESSED_ACCESS_RECORD;
	private static final String INSERT = "INSERT INTO "
			+ TABLE_PROCESSED_ACCESS_RECORD
			+ " ("
			+ COL_PROCESSED_ACCESS_RECORD_SESSION_ID
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_ENTITY_ID
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE
			+ ") VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ COL_PROCESSED_ACCESS_RECORD_ENTITY_ID
			+ " = ?, "
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT
			+ " = ?, "
			+ COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE
			+ " = ?";

	private static final String PROCESSED_ACCESS_RECORD_DDL_SQL = "ProcessedAccessRecord.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_PROCESSED_ACCESS_RECORD
			+ " WHERE "
			+ COL_PROCESSED_ACCESS_RECORD_SESSION_ID
			+ " = ?";

	private JdbcTemplate template;

	@Inject
	ProcessedAccessRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
		String[] queries = ClasspathUtils.loadStringFromClassPath(PROCESSED_ACCESS_RECORD_DDL_SQL).split(";");
		for (String query : queries) {
			this.template.update(query);
		}
	}

	@Override
	public void insert(final List<ProcessedAccessRecord> batch) {
		template.batchUpdate(INSERT, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ProcessedAccessRecord par = batch.get(i);
				ps.setString(1, par.getSessionId());
				ps.setLong(2, par.getEntityId());
				ps.setString(3, par.getClient().name());
				ps.setString(4, par.getNormalizedMethodSignature());
				ps.setLong(5, par.getEntityId());
				ps.setString(6, par.getClient().name());
				ps.setString(7, par.getNormalizedMethodSignature());
			}
			
		});
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	@Override
	public ProcessedAccessRecord get(String sessionId) {
		return template.queryForObject(SQL_GET, this.rowMapper, sessionId);
	}

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<ProcessedAccessRecord> rowMapper = new RowMapper<ProcessedAccessRecord>() {

		public ProcessedAccessRecord mapRow(ResultSet rs, int arg1) throws SQLException {
			ProcessedAccessRecord par = new ProcessedAccessRecord();
			par.setSessionId(rs.getString(COL_PROCESSED_ACCESS_RECORD_SESSION_ID));
			par.setEntityId(rs.getLong(COL_PROCESSED_ACCESS_RECORD_ENTITY_ID));
			par.setClient(Client.valueOf(rs.getString(COL_PROCESSED_ACCESS_RECORD_CLIENT)));
			par.setNormalizedMethodSignature(rs.getString(COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE));
			return par;
		}
	};
}
