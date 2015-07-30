package org.sagebionetworks.warehouse.workers.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.sagebionetworks.warehouse.workers.utils.ProcessedAccessRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

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
				ps.setString(0, par.getSessionId());
				ps.setString(1, par.getEntityId());
				ps.setString(2, par.getClient().name());
				ps.setString(3, par.getSynapseApi());
				ps.setString(4, par.getEntityId());
				ps.setString(5, par.getClient().name());
				ps.setString(6, par.getSynapseApi());
			}
			
		});
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
