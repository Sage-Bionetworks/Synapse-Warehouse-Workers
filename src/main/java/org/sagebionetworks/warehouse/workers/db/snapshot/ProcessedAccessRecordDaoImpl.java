package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_CLIENT;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_CLIENT_VERSION;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_ENTITY_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_SESSION_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_PROCESSED_ACCESS_RECORD_TIMESTAMP;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_PROCESSED_ACCESS_RECORD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.transaction.RequiresNew;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProcessedAccessRecordDaoImpl implements ProcessedAccessRecordDao {

	public static final String PROCESSED_ACCESS_RECORD_DDL_SQL = "ProcessedAccessRecord.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_PROCESSED_ACCESS_RECORD,
			PROCESSED_ACCESS_RECORD_DDL_SQL,
			true,
			COL_PROCESSED_ACCESS_RECORD_TIMESTAMP,
			Period.DAY);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_PROCESSED_ACCESS_RECORD;
	private static final String INSERT = "INSERT INTO "
			+ TABLE_PROCESSED_ACCESS_RECORD
			+ " ("
			+ COL_PROCESSED_ACCESS_RECORD_SESSION_ID
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_TIMESTAMP
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_ENTITY_ID
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT_VERSION
			+ ","
			+ COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE
			+ ") VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ COL_PROCESSED_ACCESS_RECORD_ENTITY_ID
			+ " = ?, "
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT
			+ " = ?, "
			+ COL_PROCESSED_ACCESS_RECORD_CLIENT_VERSION
			+ " = ?, "
			+ COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE
			+ " = ?";
	private static final int INSERT_SESSION_ID_INDEX 		= 1;
	private static final int INSERT_TIMESTAMP_INDEX 		= 2;
	private static final int INSERT_ENTITY_ID_INDEX 		= 3;
	private static final int INSERT_CLIENT_INDEX 			= 4;
	private static final int INSERT_CLIENT_VERSION_INDEX 	= 5;
	private static final int INSERT_NMS_INDEX 				= 6;
	private static final int UPDATE_ENTITY_ID_INDEX 		= 7;
	private static final int UPDATE_CLIENT_INDEX 			= 8;
	private static final int UPDATE_CLIENT_VERSION_INDEX 	= 9;
	private static final int UPDATE_NMS_INDEX 				= 10;
	
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_PROCESSED_ACCESS_RECORD
			+ " WHERE "
			+ COL_PROCESSED_ACCESS_RECORD_SESSION_ID
			+ " = ?"
			+ " AND "
			+ COL_PROCESSED_ACCESS_RECORD_TIMESTAMP
			+ " = ?";

	private JdbcTemplate template;
	private TransactionTemplate transactionTemplate;
	private TableCreator creator;

	@Inject
	ProcessedAccessRecordDaoImpl(JdbcTemplate template, @RequiresNew TransactionTemplate transactionTemplate, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		this.transactionTemplate = transactionTemplate;
		this.creator = creator;
	}

	@Override
	public void insert(final List<ProcessedAccessRecord> batch) {
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				template.batchUpdate(INSERT, new BatchPreparedStatementSetter() {

					@Override
					public int getBatchSize() {
						return batch.size();
					}
		
					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ProcessedAccessRecord par = batch.get(i);
						ps.setString(INSERT_SESSION_ID_INDEX, par.getSessionId());
						ps.setLong(INSERT_TIMESTAMP_INDEX, par.getTimestamp());

						if (par.getEntityId() != null) {
							ps.setLong(INSERT_ENTITY_ID_INDEX, par.getEntityId());
							ps.setLong(UPDATE_ENTITY_ID_INDEX, par.getEntityId());
						} else {
							ps.setNull(INSERT_ENTITY_ID_INDEX, Types.BIGINT);
							ps.setNull(UPDATE_ENTITY_ID_INDEX, Types.BIGINT);
						}

						ps.setString(INSERT_CLIENT_INDEX, par.getClient().name());
						ps.setString(UPDATE_CLIENT_INDEX, par.getClient().name());

						if (par.getClientVersion() != null) {
							ps.setString(INSERT_CLIENT_VERSION_INDEX, par.getClientVersion());
							ps.setString(UPDATE_CLIENT_VERSION_INDEX, par.getClientVersion());
						} else {
							ps.setNull(INSERT_CLIENT_VERSION_INDEX, Types.CHAR);
							ps.setNull(UPDATE_CLIENT_VERSION_INDEX, Types.CHAR);
						}

						ps.setString(INSERT_NMS_INDEX, par.getNormalizedMethodSignature());
						ps.setString(UPDATE_NMS_INDEX, par.getNormalizedMethodSignature());
					}
				});
				return null;
			}
		});
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	@Override
	public ProcessedAccessRecord get(String sessionId, Long timestamp) {
		return template.queryForObject(SQL_GET, this.rowMapper, sessionId, timestamp);
	}

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<ProcessedAccessRecord> rowMapper = new RowMapper<ProcessedAccessRecord>() {

		public ProcessedAccessRecord mapRow(ResultSet rs, int arg1) throws SQLException {
			ProcessedAccessRecord par = new ProcessedAccessRecord();
			par.setSessionId(rs.getString(COL_PROCESSED_ACCESS_RECORD_SESSION_ID));
			par.setTimestamp(rs.getLong(COL_PROCESSED_ACCESS_RECORD_TIMESTAMP));
			long entityId = rs.getLong(COL_PROCESSED_ACCESS_RECORD_ENTITY_ID);
			if (!rs.wasNull()) {
				par.setEntityId(entityId);
			}
			par.setClient(Client.valueOf(rs.getString(COL_PROCESSED_ACCESS_RECORD_CLIENT)));
			String clientVersion = rs.getString(COL_PROCESSED_ACCESS_RECORD_CLIENT_VERSION);
			if (!rs.wasNull()) {
				par.setClientVersion(clientVersion);
			}
			par.setNormalizedMethodSignature(rs.getString(COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE));
			return par;
		}
	};

	@Override
	public boolean doesPartitionExistForTimestamp(long timeMS) {
		return creator.doesPartitionExist(TABLE_PROCESSED_ACCESS_RECORD, timeMS, CONFIG.getPartitionPeriod());
	}
}
