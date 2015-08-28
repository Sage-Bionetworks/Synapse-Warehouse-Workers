package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class NodeSnapshotDaoImpl implements NodeSnapshotDao {

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_NODE_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_NODE_SNAPSHOT
			+ " ("
			+ COL_NODE_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_NODE_SNAPSHOT_ID
			+ ","
			+ COL_NODE_SNAPSHOT_BENEFACTOR_ID
			+ ","
			+ COL_NODE_SNAPSHOT_PROJECT_ID
			+ ","
			+ COL_NODE_SNAPSHOT_PARENT_ID
			+ ","
			+ COL_NODE_SNAPSHOT_NODE_TYPE
			+ ","
			+ COL_NODE_SNAPSHOT_CREATED_ON
			+ ","
			+ COL_NODE_SNAPSHOT_CREATED_BY
			+ ","
			+ COL_NODE_SNAPSHOT_MODIFIED_ON
			+ ","
			+ COL_NODE_SNAPSHOT_MODIFIED_BY
			+ ","
			+ COL_NODE_SNAPSHOT_VERSION_NUMBER
			+ ","
			+ COL_NODE_SNAPSHOT_FILE_HANDLE_ID
			+ ","
			+ COL_NODE_SNAPSHOT_NAME
			+ ","
			+ COL_NODE_SNAPSHOT_IS_PUBLIC
			+ ","
			+ COL_NODE_SNAPSHOT_IS_CONTROLLED
			+ ","
			+ COL_NODE_SNAPSHOT_IS_RESTRICTED
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String NODE_SNAPSHOT_DDL_SQL = "NodeSnapshot.ddl.sql";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_NODE_SNAPSHOT
			+ " WHERE "
			+ COL_NODE_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_NODE_SNAPSHOT_ID
			+ " = ?";

	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<NodeSnapshot> rowMapper = new RowMapper<NodeSnapshot>() {

		public NodeSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			NodeSnapshot snapshot = new NodeSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_NODE_SNAPSHOT_TIMESTAMP));
			snapshot.setId("" + rs.getLong(COL_NODE_SNAPSHOT_ID));
			Long benefactorId = rs.getLong(COL_NODE_SNAPSHOT_BENEFACTOR_ID);
			if (!rs.wasNull()) {
				snapshot.setBenefactorId("" + benefactorId);
			}
			Long projectId = rs.getLong(COL_NODE_SNAPSHOT_PROJECT_ID);
			if (!rs.wasNull()) {
				snapshot.setProjectId("" + projectId);
			}
			Long parentId = rs.getLong(COL_NODE_SNAPSHOT_PARENT_ID);
			if (!rs.wasNull()) {
				snapshot.setParentId("" + parentId);
			}
			snapshot.setNodeType(EntityType.valueOf(rs.getString(COL_NODE_SNAPSHOT_NODE_TYPE)));
			snapshot.setCreatedOn(new Date(rs.getLong(COL_NODE_SNAPSHOT_CREATED_ON)));
			snapshot.setCreatedByPrincipalId(rs.getLong(COL_NODE_SNAPSHOT_CREATED_BY));
			snapshot.setModifiedOn(new Date(rs.getLong(COL_NODE_SNAPSHOT_MODIFIED_ON)));
			snapshot.setModifiedByPrincipalId(rs.getLong(COL_NODE_SNAPSHOT_MODIFIED_BY));
			Long versionNumber = rs.getLong(COL_NODE_SNAPSHOT_VERSION_NUMBER);
			if (!rs.wasNull()) {
				snapshot.setVersionNumber(versionNumber);
			}
			Long fileHandleId = rs.getLong(COL_NODE_SNAPSHOT_FILE_HANDLE_ID);
			if (!rs.wasNull()) {
				snapshot.setFileHandleId("" + fileHandleId);
			}
			snapshot.setName(rs.getString(COL_NODE_SNAPSHOT_NAME));
			snapshot.setIsPublic(rs.getBoolean(COL_NODE_SNAPSHOT_IS_PUBLIC));
			snapshot.setIsControlled(rs.getBoolean(COL_NODE_SNAPSHOT_IS_CONTROLLED));
			snapshot.setIsRestricted(rs.getBoolean(COL_NODE_SNAPSHOT_IS_RESTRICTED));
			return snapshot;
		}
	};

	@Inject
	NodeSnapshotDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
		this.template.update(ClasspathUtils.loadStringFromClassPath(NODE_SNAPSHOT_DDL_SQL));
	}

	@Override
	public void insert(final List<NodeSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				NodeSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, Long.parseLong(snapshot.getId()));
				if (snapshot.getBenefactorId() != null) {
					ps.setLong(3, Long.parseLong(snapshot.getBenefactorId()));
				} else {
					ps.setNull(3, Types.BIGINT);
				}
				if (snapshot.getProjectId() != null) {
					ps.setLong(4, Long.parseLong(snapshot.getProjectId()));
				} else {
					ps.setNull(4, Types.BIGINT);
				}
				if (snapshot.getParentId() != null) {
					ps.setLong(5, Long.parseLong(snapshot.getParentId()));
				} else {
					ps.setNull(5, Types.BIGINT);
				}
				ps.setString(6, snapshot.getNodeType().name());
				ps.setLong(7, snapshot.getCreatedOn().getTime());
				ps.setLong(8, snapshot.getCreatedByPrincipalId());
				ps.setLong(9, snapshot.getModifiedOn().getTime());
				ps.setLong(10, snapshot.getModifiedByPrincipalId());
				if (snapshot.getVersionNumber() != null) {
					ps.setLong(11, snapshot.getVersionNumber());
				} else {
					ps.setNull(11, Types.BIGINT);
				}
				if (snapshot.getFileHandleId() != null) {
					ps.setLong(12, Long.parseLong(snapshot.getFileHandleId()));
				} else {
					ps.setNull(12, Types.BIGINT);
				}
				ps.setString(13, snapshot.getName());
				ps.setBoolean(14, snapshot.getIsPublic());
				ps.setBoolean(15, snapshot.getIsControlled());
				ps.setBoolean(16, snapshot.getIsRestricted());
			}
		});
	}

	@Override
	public NodeSnapshot get(Long timestamp, Long nodeId) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, nodeId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
