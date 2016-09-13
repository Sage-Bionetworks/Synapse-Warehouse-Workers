package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_DELETED_NODE_SNAPSHOT_ID;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_DELETED_NODE_SNAPSHOT_TIMESTAMP;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_DELETED_NODE_SNAPSHOT;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.model.DeletedNodeSnapshot;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.inject.Inject;

public class DeletedNodeSnapshotDaoImpl implements DeletedNodeSnapshotDao {

	public static final String DELETED_NODE_SNAPSHOT_DDL_SQL = "DeletedNodeSnapshot.ddl.sql";
	public static final TableConfiguration CONFIG = new TableConfiguration(
			TABLE_DELETED_NODE_SNAPSHOT,
			DELETED_NODE_SNAPSHOT_DDL_SQL,
			true,
			COL_DELETED_NODE_SNAPSHOT_TIMESTAMP,
			Period.MONTH);
	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_DELETED_NODE_SNAPSHOT;
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO "
			+ TABLE_DELETED_NODE_SNAPSHOT
			+ " ("
			+ COL_DELETED_NODE_SNAPSHOT_TIMESTAMP
			+ ","
			+ COL_DELETED_NODE_SNAPSHOT_ID
			+ ") VALUES (?,?)";
	private static final String SQL_GET = "SELECT * FROM "
			+ TABLE_DELETED_NODE_SNAPSHOT
			+ " WHERE "
			+ COL_DELETED_NODE_SNAPSHOT_TIMESTAMP
			+ " = ? AND "
			+ COL_DELETED_NODE_SNAPSHOT_ID
			+ " = ?";

	private JdbcTemplate template;
	private TableCreator creator;

	/*
	 * Map all columns to the dbo.
	 */
	RowMapper<DeletedNodeSnapshot> rowMapper = new RowMapper<DeletedNodeSnapshot>() {

		public DeletedNodeSnapshot mapRow(ResultSet rs, int arg1) throws SQLException {
			DeletedNodeSnapshot snapshot = new DeletedNodeSnapshot();
			snapshot.setTimestamp(rs.getLong(COL_DELETED_NODE_SNAPSHOT_TIMESTAMP));
			snapshot.setId(rs.getLong(COL_DELETED_NODE_SNAPSHOT_ID));
			return snapshot;
		}
	};

	@Inject
	DeletedNodeSnapshotDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		this.creator = creator;
	}

	@Override
	public void insert(final List<DeletedNodeSnapshot> batch) {
		template.batchUpdate(INSERT_IGNORE, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return batch.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				DeletedNodeSnapshot snapshot = batch.get(i);
				ps.setLong(1, snapshot.getTimestamp());
				ps.setLong(2, snapshot.getId());
			}
		});
	}

	@Override
	public DeletedNodeSnapshot get(Long timestamp, Long nodeId) {
		return template.queryForObject(SQL_GET, this.rowMapper, timestamp, nodeId);
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	@Override
	public boolean doesPartitionExistForTimestamp(long timeMS) {
		return creator.doesPartitionExist(TABLE_DELETED_NODE_SNAPSHOT, timeMS, CONFIG.getPartitionPeriod());
	}
}
