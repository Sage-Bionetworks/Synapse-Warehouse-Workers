package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.SQLException;

import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.inject.Inject;

public class WarehouseWorkersStateDaoImpl implements WarehouseWorkersStateDao {

	private static final String WAREHOUSE_WORKERS_STATE_DDL_SQL = "WarehouseWorkersState.ddl.sql";
	private JdbcTemplate template;

	private static final String SET_STATE = "INSERT IGNORE INTO "
			+ TABLE_WAREHOUSE_WORKERS_STATE
			+ " ("
			+ COL_WAREHOUSE_WORKERS_STATE_STATE
			+ ","
			+ COL_WAREHOUSE_WORKERS_STATE_TIMESTAMP
			+ ") VALUES (?,?)";

	private static final String GET_STATE = "SELECT "
			+ COL_WAREHOUSE_WORKERS_STATE_STATE
			+ " FROM "
			+ TABLE_WAREHOUSE_WORKERS_STATE
			+ " WHERE "
			+ COL_WAREHOUSE_WORKERS_STATE_TIMESTAMP
			+ " = (SELECT MAX("
			+ COL_WAREHOUSE_WORKERS_STATE_TIMESTAMP
			+ ") FROM "
			+ TABLE_WAREHOUSE_WORKERS_STATE
			+ ")";

	private static final String TRUNCATE = "TRUNCATE TABLE " + COL_WAREHOUSE_WORKERS_STATE_STATE;

	@Inject
	WarehouseWorkersStateDaoImpl(JdbcTemplate template, TableCreator creator) throws SQLException {
		super();
		this.template = template;
		// Create the table
		creator.createTable(WAREHOUSE_WORKERS_STATE_DDL_SQL);
		setState(WarehouseWorkersState.NORMAL);
	}

	@Override
	public WarehouseWorkersState getState() {
		return WarehouseWorkersState.valueOf(template.queryForObject(GET_STATE, String.class));
	}

	@Override
	public void setState(WarehouseWorkersState state) {
		template.update(SET_STATE, state.name(), System.currentTimeMillis());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}
}
