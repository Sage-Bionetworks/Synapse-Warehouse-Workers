package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.repo.model.ACCESS_TYPE;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.model.ResourceAccessSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class ResourceAccessSnapshotDaoImpl implements ResourceAccessSnapshotDao{

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_RESOURCE_ACCESS_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_RESOURCE_ACCESS_SNAPSHOT
			+ " ("
			+ COL_RESOURCE_ACCESS_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_ID
			+ ","
			+ COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_TYPE
			+ ","
			+ COL_RESOURCE_ACCESS_SNAPSHOT_PRINCIPAL_ID
			+ ","
			+ COL_RESOURCE_ACCESS_SNAPSHOT_ACCESS_TYPE
			+ ") VALUES (?,?,?,?,?)";

	private static final String RESOURCE_ACCESS_SNAPSHOT_DDL_SQL = "ResourceAccessSnapshot.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_RESOURCE_ACCESS_SNAPSHOT
			+ " WHERE "
			+ COL_RESOURCE_ACCESS_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_ID
			+ " = ? AND "
			+ COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_TYPE
			+ " = ? AND "
			+ COL_RESOURCE_ACCESS_SNAPSHOT_PRINCIPAL_ID
			+ " = ? AND "
			+ COL_RESOURCE_ACCESS_SNAPSHOT_ACCESS_TYPE
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<ResourceAccessSnapshot> rowMapper = new RowMapper<ResourceAccessSnapshot>() {

		public ResourceAccessSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			ResourceAccessSnapshot snapshot = new ResourceAccessSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_RESOURCE_ACCESS_SNAPSHOT_TIMESTAMP));
			snapshot.setOwnerId(rs.getLong(COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_ID));
			snapshot.setOwnerType(ObjectType.valueOf(rs.getString(COL_RESOURCE_ACCESS_SNAPSHOT_OWNER_TYPE)));
			snapshot.setPrincipalId(rs.getLong(COL_RESOURCE_ACCESS_SNAPSHOT_PRINCIPAL_ID));
			snapshot.setAccessType(ACCESS_TYPE.valueOf(rs.getString(COL_RESOURCE_ACCESS_SNAPSHOT_ACCESS_TYPE)));
			return snapshot;
		}
	};

	@Inject
	ResourceAccessSnapshotDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
		this.template.update(ClasspathUtils.loadStringFromClassPath(RESOURCE_ACCESS_SNAPSHOT_DDL_SQL));
	}

	@Override
	public void insert(final List<ResourceAccessSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ResourceAccessSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, snapshot.getOwnerId());
				ps.setString(3, snapshot.getOwnerType().name());
				ps.setLong(4, snapshot.getPrincipalId());
				ps.setString(5, snapshot.getAccessType().name());
			}
		});
	}

	@Override
	public ResourceAccessSnapshot get(Long timestamp, Long ownerId, ObjectType ownerType, Long principalId, ACCESS_TYPE accessType) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, ownerId, ownerType.name(), principalId, accessType.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
