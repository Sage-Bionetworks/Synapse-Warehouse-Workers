package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.repo.model.UserGroup;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class UserGroupDaoImpl implements UserGroupDao {

	public static final String USER_GROUP_DDL_SQL = "UserGroup.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_USER_GROUP,
			USER_GROUP_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_USER_GROUP;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_USER_GROUP
			+ " ("
			+ COL_USER_GROUP_ID
			+ ","
			+ COL_USER_GROUP_IS_INDIVIDUAL
			+ ","
			+ COL_USER_GROUP_CREATED_ON
			+ ") VALUES (?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_USER_GROUP
			+ " WHERE "
			+ COL_USER_GROUP_ID
			+ " = ? AND "
			+ COL_USER_GROUP_IS_INDIVIDUAL
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<UserGroup> rowMapper = new RowMapper<UserGroup>() {

		public UserGroup mapRow(ResultSet rs, int arg1) throws SQLException {
			UserGroup ug = new UserGroup();
			ug.setId("" + rs.getLong(COL_USER_GROUP_ID));
			ug.setIsIndividual(rs.getBoolean(COL_USER_GROUP_IS_INDIVIDUAL));
			ug.setCreationDate(new Date(rs.getLong(COL_USER_GROUP_CREATED_ON)));
			return ug;
		}
	};

	@Inject
	UserGroupDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<UserGroup> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				UserGroup ug = batch.get(i);
				ps.setLong(1, Long.parseLong(ug.getId()));
				ps.setBoolean(2, ug.getIsIndividual());
				ps.setLong(3, ug.getCreationDate().getTime());
			}
		});
	}

	@Override
	public UserGroup get(Long id, boolean isIndividual) {
		return template.queryForObject(SQL_GET, this.rowMapper, id, isIndividual);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
