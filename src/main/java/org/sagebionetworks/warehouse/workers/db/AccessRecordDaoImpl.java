package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_DATE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_ELAPSE_MS;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_HOST;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_INSTANCE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_METHOD;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_ORIGIN;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_QUERY_STRING;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_REQUEST_URL;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_RESPONSE_STATUS;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_RETURN_OBJECT_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_SESSION_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_STACK;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_SUCCESS;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_THREAD_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_TIMESTAMP;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_USER_AGENT;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_USER_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_VIA;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_VM_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_X_FORWARDED_FOR;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_ACCESS_RECORD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class AccessRecordDaoImpl implements AccessRecordDao {

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_ACCESS_RECORD;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_ACCESS_RECORD
			+ " ("
			+ COL_ACCESS_RECORD_SESSION_ID
			+ ","
			+ COL_ACCESS_RECORD_RETURN_OBJECT_ID
			+ ","
			+ COL_ACCESS_RECORD_ELAPSE_MS
			+ ","
			+ COL_ACCESS_RECORD_TIMESTAMP
			+ ","
			+ COL_ACCESS_RECORD_VIA
			+ ","
			+ COL_ACCESS_RECORD_HOST
			+ ","
			+ COL_ACCESS_RECORD_THREAD_ID
			+ ","
			+ COL_ACCESS_RECORD_USER_AGENT
			+ ","
			+ COL_ACCESS_RECORD_QUERY_STRING
			+ ","
			+ COL_ACCESS_RECORD_X_FORWARDED_FOR
			+ ","
			+ COL_ACCESS_RECORD_REQUEST_URL
			+ ","
			+ COL_ACCESS_RECORD_USER_ID
			+ ","
			+ COL_ACCESS_RECORD_ORIGIN
			+ ","
			+ COL_ACCESS_RECORD_DATE
			+ ","
			+ COL_ACCESS_RECORD_METHOD
			+ ","
			+ COL_ACCESS_RECORD_VM_ID
			+ ","
			+ COL_ACCESS_RECORD_INSTANCE
			+ ","
			+ COL_ACCESS_RECORD_STACK
			+ ","
			+ COL_ACCESS_RECORD_SUCCESS
			+ ","
			+ COL_ACCESS_RECORD_RESPONSE_STATUS
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String ACCESS_RECORD_DDL_SQL = "AccessRecord.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_ACCESS_RECORD
			+ " WHERE "
			+ COL_ACCESS_RECORD_SESSION_ID
			+ " = ?"
			+ " AND "
			+ COL_ACCESS_RECORD_TIMESTAMP
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<AccessRecord> rowMapper = new RowMapper<AccessRecord>() {

		public AccessRecord mapRow(ResultSet rs, int arg1) throws SQLException {
			AccessRecord ar = new AccessRecord();
			ar.setSessionId(rs.getString(COL_ACCESS_RECORD_SESSION_ID));
			ar.setReturnObjectId(rs.getString(COL_ACCESS_RECORD_RETURN_OBJECT_ID));
			ar.setElapseMS(rs.getLong(COL_ACCESS_RECORD_ELAPSE_MS));
			ar.setTimestamp(rs.getLong(COL_ACCESS_RECORD_TIMESTAMP));
			ar.setVia(rs.getString(COL_ACCESS_RECORD_VIA));
			ar.setHost(rs.getString(COL_ACCESS_RECORD_HOST));
			ar.setThreadId(rs.getLong(COL_ACCESS_RECORD_THREAD_ID));
			ar.setUserAgent(rs.getString(COL_ACCESS_RECORD_USER_AGENT));
			ar.setQueryString(rs.getString(COL_ACCESS_RECORD_QUERY_STRING));
			ar.setXForwardedFor(rs.getString(COL_ACCESS_RECORD_X_FORWARDED_FOR));
			ar.setRequestURL(rs.getString(COL_ACCESS_RECORD_REQUEST_URL));
			Long userId = rs.getLong(COL_ACCESS_RECORD_USER_ID);
			if (!rs.wasNull()) {
				ar.setUserId(userId);
			}
			ar.setOrigin(rs.getString(COL_ACCESS_RECORD_ORIGIN));
			ar.setDate(rs.getString(COL_ACCESS_RECORD_DATE));
			ar.setMethod(rs.getString(COL_ACCESS_RECORD_METHOD));
			ar.setVmId(rs.getString(COL_ACCESS_RECORD_VM_ID));
			ar.setInstance(rs.getString(COL_ACCESS_RECORD_INSTANCE));
			ar.setStack(rs.getString(COL_ACCESS_RECORD_STACK));
			ar.setSuccess(rs.getBoolean(COL_ACCESS_RECORD_SUCCESS));
			ar.setResponseStatus(rs.getLong(COL_ACCESS_RECORD_RESPONSE_STATUS));
			return ar;
		}
	};

	@Inject
	AccessRecordDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		creator.createTableWithPartition(ACCESS_RECORD_DDL_SQL, TABLE_ACCESS_RECORD, COL_ACCESS_RECORD_TIMESTAMP, Period.DAY);
	}

	@Override
	public void insert(final List<AccessRecord> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AccessRecord ar = batch.get(i);
				ps.setString(1, ar.getSessionId());
				ps.setString(2, ar.getReturnObjectId());
				ps.setLong(3, ar.getElapseMS());
				ps.setLong(4, ar.getTimestamp());
				ps.setString(5, ar.getVia());
				ps.setString(6, ar.getHost());
				ps.setLong(7, ar.getThreadId());
				ps.setString(8, ar.getUserAgent());
				ps.setString(9, ar.getQueryString());
				ps.setString(10, ar.getXForwardedFor());
				ps.setString(11, ar.getRequestURL());
				if (ar.getUserId() != null) {
					ps.setLong(12, ar.getUserId());
				} else {
					ps.setNull(12, Types.BIGINT);
				}
				ps.setString(13, ar.getOrigin());
				ps.setString(14, ar.getDate());
				ps.setString(15, ar.getMethod());
				ps.setString(16, ar.getVmId());
				ps.setString(17, ar.getInstance());
				ps.setString(18, ar.getStack());
				ps.setBoolean(19, ar.getSuccess());
				ps.setLong(20, ar.getResponseStatus());
			}
		});
	}

	@Override
	public AccessRecord get(String sessionId, Long timestamp) {
		return template.queryForObject(SQL_GET, this.rowMapper, sessionId, timestamp);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
