package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.google.inject.Inject;

public class TeamMemberSnapshotDaoImpl implements TeamMemberSnapshotDao{

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_TEAM_MEMBER_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_TEAM_MEMBER_SNAPSHOT
			+ " ("
			+ COL_TEAM_MEMBER_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_TEAM_MEMBER_SNAPSHOT_TEAM_ID
			+ ","
			+ COL_TEAM_MEMBER_SNAPSHOT_MEMBER_ID
			+ ","
			+ COL_TEAM_MEMBER_SNAPSHOT_IS_ADMIN
			+ ") VALUES (?,?,?,?)";

	private static final String TEAM_MEMBER_SNAPSHOT_DDL_SQL = "TeamMemberSnapshot.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_TEAM_MEMBER_SNAPSHOT
			+ " WHERE "
			+ COL_TEAM_MEMBER_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_TEAM_MEMBER_SNAPSHOT_TEAM_ID
			+ " = ? AND "
			+ COL_TEAM_MEMBER_SNAPSHOT_MEMBER_ID
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<TeamMemberSnapshot> rowMapper = new RowMapper<TeamMemberSnapshot>() {

		public TeamMemberSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			TeamMemberSnapshot snapshot = new TeamMemberSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_TEAM_MEMBER_SNAPSHOT_TIMESTAMP));
			snapshot.setTeamId(rs.getLong(COL_TEAM_MEMBER_SNAPSHOT_TEAM_ID));
			snapshot.setMemberId(rs.getLong(COL_TEAM_MEMBER_SNAPSHOT_MEMBER_ID));
			Boolean isAdmin = rs.getBoolean(COL_TEAM_MEMBER_SNAPSHOT_IS_ADMIN);
			if (!rs.wasNull()) {
				snapshot.setIsAdmin(isAdmin);
			}
			return snapshot;
		}
	};

	@Inject
	TeamMemberSnapshotDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		creator.createTable(TEAM_MEMBER_SNAPSHOT_DDL_SQL);
	}

	@Transactional
	@Override
	public void insert(final List<TeamMemberSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				TeamMemberSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, snapshot.getTeamId());
				ps.setLong(3, snapshot.getMemberId());
				if (snapshot.getIsAdmin() != null) {
					ps.setBoolean(4, snapshot.getIsAdmin());
				} else {
					ps.setNull(4, Types.BOOLEAN);
				}
			}
		});
	}

	@Override
	public TeamMemberSnapshot get(Long timestamp, Long teamId, Long memberId) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, teamId, memberId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
