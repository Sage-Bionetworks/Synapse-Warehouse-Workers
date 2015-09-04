package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class TeamSnapshotDaoImpl implements TeamSnapshotDao {

	public static final String TEAM_SNAPSHOT_DDL_SQL = "TeamSnapshot.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_TEAM_SNAPSHOT,
			TEAM_SNAPSHOT_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_TEAM_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_TEAM_SNAPSHOT
			+ " ("
			+ COL_TEAM_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_TEAM_SNAPSHOT_ID
			+ ","
			+ COL_TEAM_SNAPSHOT_CREATED_ON
			+ ","
			+ COL_TEAM_SNAPSHOT_CREATED_BY
			+ ","
			+ COL_TEAM_SNAPSHOT_MODIFIED_ON
			+ ","
			+ COL_TEAM_SNAPSHOT_MODIFIED_BY
			+ ","
			+ COL_TEAM_SNAPSHOT_NAME
			+ ","
			+ COL_TEAM_SNAPSHOT_CAN_PUBLIC_JOIN
			+ ") VALUES (?,?,?,?,?,?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_TEAM_SNAPSHOT
			+ " WHERE "
			+ COL_TEAM_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_TEAM_SNAPSHOT_ID
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<TeamSnapshot> rowMapper = new RowMapper<TeamSnapshot>() {

		public TeamSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			TeamSnapshot snapshot = new TeamSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_TEAM_SNAPSHOT_TIMESTAMP));
			snapshot.setId("" + rs.getLong(COL_TEAM_SNAPSHOT_ID));
			snapshot.setCreatedOn(new Date(rs.getLong(COL_TEAM_SNAPSHOT_CREATED_ON)));
			snapshot.setCreatedBy("" + rs.getLong(COL_TEAM_SNAPSHOT_CREATED_BY));
			snapshot.setModifiedOn(new Date(rs.getLong(COL_TEAM_SNAPSHOT_MODIFIED_ON)));
			snapshot.setModifiedBy("" + rs.getLong(COL_TEAM_SNAPSHOT_MODIFIED_BY));
			snapshot.setName(rs.getString(COL_TEAM_SNAPSHOT_NAME));
			snapshot.setCanPublicJoin(rs.getBoolean(COL_TEAM_SNAPSHOT_CAN_PUBLIC_JOIN));
			return snapshot;
		}
	};

	@Inject
	TeamSnapshotDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		creator.createTable(TEAM_SNAPSHOT_DDL_SQL);
	}

	@Override
	public void insert(final List<TeamSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				TeamSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, Long.parseLong(snapshot.getId()));
				ps.setLong(3, snapshot.getCreatedOn().getTime());
				ps.setLong(4, Long.parseLong(snapshot.getCreatedBy()));
				ps.setLong(5, snapshot.getModifiedOn().getTime());
				ps.setLong(6, Long.parseLong(snapshot.getModifiedBy()));
				ps.setString(7, snapshot.getName());
				ps.setBoolean(8, snapshot.getCanPublicJoin());
			}
		});
	}

	@Override
	public TeamSnapshot get(Long timestamp, Long teamId) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, teamId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
