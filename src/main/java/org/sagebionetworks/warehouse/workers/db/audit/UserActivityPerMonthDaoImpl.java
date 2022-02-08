package org.sagebionetworks.warehouse.workers.db.audit;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_MONTH_MONTH;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_MONTH_ACTIVE_DAY_COUNT;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_MONTH_USER_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_MONTH_XFORWARDEDFOR;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_USER_ACTIVITY_PER_MONTH;

import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.transaction.RequiresNew;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerMonth;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UserActivityPerMonthDaoImpl implements UserActivityPerMonthDao {

	public static final String USER_ACTIVITY_PER_MONTH_DDL_SQL = "UserActivityPerMonth.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_USER_ACTIVITY_PER_MONTH,
			USER_ACTIVITY_PER_MONTH_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_USER_ACTIVITY_PER_MONTH;
	private static final String INSERT = "INSERT INTO " + TABLE_USER_ACTIVITY_PER_MONTH + " ("
			+ COL_USER_ACTIVITY_PER_MONTH_USER_ID + ","
			+ COL_USER_ACTIVITY_PER_MONTH_XFORWARDEDFOR + ","
			+ COL_USER_ACTIVITY_PER_MONTH_MONTH + ","
			+ COL_USER_ACTIVITY_PER_MONTH_ACTIVE_DAY_COUNT + ")"
			+ " VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ COL_USER_ACTIVITY_PER_MONTH_ACTIVE_DAY_COUNT + " = ?";
	private static final String SQL_GET = "SELECT *"
			+ " FROM " + TABLE_USER_ACTIVITY_PER_MONTH
			+ " WHERE " + COL_USER_ACTIVITY_PER_MONTH_USER_ID + " = ?"
			+ " AND " + COL_USER_ACTIVITY_PER_MONTH_XFORWARDEDFOR + " = ?"
			+ " AND " + COL_USER_ACTIVITY_PER_MONTH_MONTH + " = ?";
	private static final String SQL_CHECK_RECORD_FOR_MONTH =
			"SELECT " + COL_USER_ACTIVITY_PER_MONTH_MONTH
			+ " FROM " + TABLE_USER_ACTIVITY_PER_MONTH
			+ " WHERE " + COL_USER_ACTIVITY_PER_MONTH_MONTH + " = ?"
			+ " LIMIT 1";

	private JdbcTemplate template;
	private TransactionTemplate transactionTemplate;

	@Inject
	UserActivityPerMonthDaoImpl(JdbcTemplate template, @RequiresNew TransactionTemplate transactionTemplate) throws SQLException {
		super();
		this.template = template;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public void insert(final List<UserActivityPerMonth> batch) {
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
						UserActivityPerMonth uar = batch.get(i);
						ps.setLong(1, uar.getUserId());
						ps.setString(2, uar.getXForwardedFor());
						ps.setString(3, uar.getMonth());
						ps.setLong(4, uar.getUniqueDate());
						ps.setLong(5, uar.getUniqueDate());
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
	public UserActivityPerMonth get(Long userId, String xForwardedFor, String month) {
		return template.queryForObject(SQL_GET, this.rowMapper, userId, xForwardedFor, month);
	}

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<UserActivityPerMonth> rowMapper = new RowMapper<UserActivityPerMonth>() {

		public UserActivityPerMonth mapRow(ResultSet rs, int arg1) throws SQLException {
			UserActivityPerMonth uar = new UserActivityPerMonth();
			uar.setUserId(rs.getLong(COL_USER_ACTIVITY_PER_MONTH_USER_ID));
			uar.setXForwardedFor(rs.getString(COL_USER_ACTIVITY_PER_MONTH_XFORWARDEDFOR));
			uar.setMonth(rs.getString(COL_USER_ACTIVITY_PER_MONTH_MONTH));
			uar.setUniqueDate(rs.getLong(COL_USER_ACTIVITY_PER_MONTH_ACTIVE_DAY_COUNT));
			return uar;
		}
	};

	@Override
	public boolean hasRecordForMonth(Date month) {
		try {
			template.queryForObject(SQL_CHECK_RECORD_FOR_MONTH, String.class, month);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
}
