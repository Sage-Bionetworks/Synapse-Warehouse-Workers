package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class UserProfileSnapshotDaoImpl implements UserProfileSnapshotDao{

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_USER_PROFILE_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_USER_PROFILE_SNAPSHOT
			+ " ("
			+ COL_USER_PROFILE_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_ID
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_USER_NAME
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_FIRST_NAME
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_LAST_NAME
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_EMAIL
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_LOCATION
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_COMPANY
			+ ","
			+ COL_USER_PROFILE_SNAPSHOT_POSITION
			+ ") VALUES (?,?,?,?,?,?,?,?,?)";

	private static final String USER_PROFILE_SNAPSHOT_DDL_SQL = "UserProfileSnapshot.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_USER_PROFILE_SNAPSHOT
			+ " WHERE "
			+ COL_USER_PROFILE_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_USER_PROFILE_SNAPSHOT_ID
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<UserProfileSnapshot> rowMapper = new RowMapper<UserProfileSnapshot>() {

		public UserProfileSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			UserProfileSnapshot snapshot = new UserProfileSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_USER_PROFILE_SNAPSHOT_TIMESTAMP));
			snapshot.setOwnerId("" + rs.getLong(COL_USER_PROFILE_SNAPSHOT_ID));
			snapshot.setUserName(rs.getString(COL_USER_PROFILE_SNAPSHOT_USER_NAME));
			snapshot.setFirstName(rs.getString(COL_USER_PROFILE_SNAPSHOT_FIRST_NAME));
			snapshot.setLastName(rs.getString(COL_USER_PROFILE_SNAPSHOT_LAST_NAME));
			snapshot.setEmail(rs.getString(COL_USER_PROFILE_SNAPSHOT_EMAIL));
			snapshot.setLocation(rs.getString(COL_USER_PROFILE_SNAPSHOT_LOCATION));
			snapshot.setCompany(rs.getString(COL_USER_PROFILE_SNAPSHOT_COMPANY));
			snapshot.setPosition(rs.getString(COL_USER_PROFILE_SNAPSHOT_POSITION));
			return snapshot;
		}
	};

	@Inject
	UserProfileSnapshotDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		creator.createTable(USER_PROFILE_SNAPSHOT_DDL_SQL);
	}

	@Override
	public void insert(final List<UserProfileSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				UserProfileSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, Long.parseLong(snapshot.getOwnerId()));
				ps.setString(3, snapshot.getUserName());
				ps.setString(4, snapshot.getFirstName());
				ps.setString(5, snapshot.getLastName());
				ps.setString(6, snapshot.getEmail());
				ps.setString(7, snapshot.getLocation());
				ps.setString(8, snapshot.getCompany());
				ps.setString(9, snapshot.getPosition());
			}
		});
	}

	@Override
	public UserProfileSnapshot get(Long timestamp, Long userId) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, userId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
