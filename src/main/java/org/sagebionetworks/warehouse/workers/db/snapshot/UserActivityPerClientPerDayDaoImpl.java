package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_DATE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_USER_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_CLIENT;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.transaction.RequiresNew;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
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
public class UserActivityPerClientPerDayDaoImpl implements UserActivityPerClientPerDayDao {

	public static final String USER_ACCESS_RECORD_DDL_SQL = "UserActivityPerClientPerDay.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY,
			USER_ACCESS_RECORD_DDL_SQL,
			true,
			COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_DATE,
			Period.MONTH);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY;
	private static final String INSERT = "INSERT IGNORE INTO " + TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY + " ("
			+ COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_USER_ID + ","
			+ COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_DATE + ","
			+ COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_CLIENT + ")"
			+ " VALUES (?,?,?)";
	private static final String SQL_GET = "SELECT *"
			+ " FROM " + TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY
			+ " WHERE " + COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_USER_ID + " = ?"
			+ " AND " + COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_DATE + " = ?"
			+ " AND " + COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_CLIENT + " = ?";

	private JdbcTemplate template;
	private TransactionTemplate transactionTemplate;
	private TableCreator creator;

	@Inject
	UserActivityPerClientPerDayDaoImpl(JdbcTemplate template, @RequiresNew TransactionTemplate transactionTemplate, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		this.transactionTemplate = transactionTemplate;
		this.creator = creator;
	}

	@Override
	public void insert(final List<UserActivityPerClientPerDay> batch) {
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
						UserActivityPerClientPerDay uar = batch.get(i);
						ps.setLong(1, uar.getUserId());
						ps.setString(2, uar.getDate());
						ps.setString(3, uar.getClient().name());
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
	public UserActivityPerClientPerDay get(Long userId, String date, Client client) {
		return template.queryForObject(SQL_GET, this.rowMapper, userId, date, client.name());
	}

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<UserActivityPerClientPerDay> rowMapper = new RowMapper<UserActivityPerClientPerDay>() {

		public UserActivityPerClientPerDay mapRow(ResultSet rs, int arg1) throws SQLException {
			UserActivityPerClientPerDay uar = new UserActivityPerClientPerDay();
			uar.setUserId(rs.getLong(COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_USER_ID));
			uar.setDate(rs.getString(COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_DATE));
			uar.setClient(Client.valueOf(rs.getString(COL_USER_ACTIVITY_PER_CLIENT_PER_DAY_CLIENT)));
			return uar;
		}
	};

	@Override
	public boolean doesPartitionExistForTimestamp(long timeMS) {
		return creator.doesPartitionExist(TABLE_USER_ACTIVITY_PER_CLIENT_PER_DAY, timeMS, CONFIG.getPartitionPeriod());
	}
}