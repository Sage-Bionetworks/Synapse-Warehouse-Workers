package org.sagebionetworks.warehouse.workers.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;
import static org.sagebionetworks.warehouse.workers.utils.Client.*;

import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.sagebionetworks.warehouse.workers.utils.Client;
import org.sagebionetworks.warehouse.workers.utils.ProcessedAccessRecord;
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
			+ COL_SESSION_ID
			+ ","
			+ COL_ENTITY_ID
			+ ","
			+ COL_CLIENT
			+ ","
			+ COL_SYNAPSE_API
			+ ") VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ COL_ENTITY_ID
			+ " = ?, "
			+ COL_CLIENT
			+ " = ?, "
			+ COL_SYNAPSE_API
			+ " = ?";
	private static final String SQL_GET_UNKNOWN_CLIENT = "SELECT *"
			+ " FROM " + TABLE_PROCESSED_ACCESS_RECORD
			+ " WHERE " + COL_CLIENT + " = '" + UNKNOWN.name() + "'";

	private static final String PROCESSED_ACCESS_RECORD_DDL_SQL = "ProcessedAccessRecord.ddl.sql";

	private JdbcTemplate template;

	@Inject
	ProcessedAccessRecordDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
		// Create the table
		this.template.update(ClasspathUtils
				.loadStringFromClassPath(PROCESSED_ACCESS_RECORD_DDL_SQL));
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
				ps.setString(2, par.getEntityId());
				ps.setString(3, par.getClient().name());
				ps.setString(4, par.getSynapseApi());
				ps.setString(5, par.getEntityId());
				ps.setString(6, par.getClient().name());
				ps.setString(7, par.getSynapseApi());
			}
			
		});
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	@Override
	public List<ProcessedAccessRecord> getUnknownClient() {
		return template.query(SQL_GET_UNKNOWN_CLIENT, this.rowMapper);
	}

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<ProcessedAccessRecord> rowMapper = new RowMapper<ProcessedAccessRecord>() {

		public ProcessedAccessRecord mapRow(ResultSet rs, int arg1) throws SQLException {
			ProcessedAccessRecord par = new ProcessedAccessRecord();
			par.setSessionId(rs.getString(COL_SESSION_ID));
			par.setEntityId(rs.getString(COL_ENTITY_ID));
			par.setClient(Client.valueOf(rs.getString(COL_CLIENT)));
			par.setSynapseApi(rs.getString(COL_SYNAPSE_API));
			return par;
		}
	};
}
