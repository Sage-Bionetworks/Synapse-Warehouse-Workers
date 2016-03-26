package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACL_SNAPSHOT_CREATED_ON;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACL_SNAPSHOT_OWNER_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACL_SNAPSHOT_OWNER_TYPE;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACL_SNAPSHOT_RESOURCE_ACCESS;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACL_SNAPSHOT_TIMESTAMP;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_ACL_SNAPSHOT;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.utils.CompressionUtils;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotUtils;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class AclSnapshotDaoImpl implements AclSnapshotDao{

	public static final String ACL_SNAPSHOT_DDL_SQL = "AclSnapshot.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_ACL_SNAPSHOT,
			ACL_SNAPSHOT_DDL_SQL,
			false,
			null,
			null);

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_ACL_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_ACL_SNAPSHOT
			+ " ("
			+ COL_ACL_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_ACL_SNAPSHOT_OWNER_ID
			+ ","
			+ COL_ACL_SNAPSHOT_OWNER_TYPE
			+ ","
			+ COL_ACL_SNAPSHOT_CREATED_ON
			+ ","
			+ COL_ACL_SNAPSHOT_RESOURCE_ACCESS
			+ ") VALUES (?,?,?,?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_ACL_SNAPSHOT
			+ " WHERE "
			+ COL_ACL_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_ACL_SNAPSHOT_OWNER_ID
			+ " = ? AND "
			+ COL_ACL_SNAPSHOT_OWNER_TYPE
			+ " = ?";
	private JdbcTemplate template;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<AclSnapshot> rowMapper = new RowMapper<AclSnapshot>() {

		public AclSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			AclSnapshot snapshot = new AclSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_ACL_SNAPSHOT_TIMESTAMP));
			snapshot.setId("" + rs.getLong(COL_ACL_SNAPSHOT_OWNER_ID));
			snapshot.setOwnerType(ObjectType.valueOf(rs.getString(COL_ACL_SNAPSHOT_OWNER_TYPE)));
			Long createdOn = rs.getLong(COL_ACL_SNAPSHOT_CREATED_ON);
			if (!rs.wasNull()) {
				snapshot.setCreationDate(new Date(createdOn));
			}
			Blob resourceAccessBlob = rs.getBlob(COL_ACL_SNAPSHOT_RESOURCE_ACCESS);
			if (resourceAccessBlob != null && resourceAccessBlob.length() > 0) {
				String resourceAccess = CompressionUtils.decompressUTF8(resourceAccessBlob.getBytes(1, (int) resourceAccessBlob.length()));
				snapshot.setResourceAccess(XMLUtils.fromXML(resourceAccess, Set.class, AclSnapshot.RESOURCE_ACCESS_ALIAS));
			}
			return snapshot;
		}
	};

	@Inject
	AclSnapshotDaoImpl(JdbcTemplate template) throws SQLException {
		super();
		this.template = template;
	}

	@Override
	public void insert(final List<AclSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				AclSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, ObjectSnapshotUtils.convertSynapseIdToLong(snapshot.getId()));
				ps.setString(3, snapshot.getOwnerType().name());
				if (snapshot.getCreationDate() != null) {
					ps.setLong(4, snapshot.getCreationDate().getTime());
				} else {
					ps.setNull(4, Types.BIGINT);
				}
				if (snapshot.getResourceAccess() != null) {
					String xml = XMLUtils.toXML(snapshot.getResourceAccess(), AclSnapshot.RESOURCE_ACCESS_ALIAS);
					ps.setBytes(5, CompressionUtils.compressStringUTF8(xml));
				} else {
					ps.setNull(5, Types.BLOB);
				}
			}
		});
	}

	@Override
	public AclSnapshot get(Long timestamp, Long ownerId, ObjectType ownerType) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, ownerId, ownerType.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

}
