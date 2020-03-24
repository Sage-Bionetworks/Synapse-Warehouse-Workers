package org.sagebionetworks.warehouse.workers.db;

import static org.sagebionetworks.warehouse.workers.db.Sql.*;

import java.sql.SQLException;

import org.sagebionetworks.warehouse.workers.db.transaction.RequiresNew;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.inject.Inject;

public class WarehouseWorkersStateDaoImpl implements WarehouseWorkersStateDao {

	private static final String WAREHOUSE_WORKERS_STATE_DDL_SQL = "WarehouseWorkersState.ddl.sql";
	private JdbcTemplate template;
	private TransactionTemplate transactionTemplate;

	private static final String SET_STATE = "INSERT IGNORE INTO "
			+ TABLE_WAREHOUSE_WORKERS_STATE
			+ " ("
			+ COL_WAREHOUSE_WORKERS_STATE_STATE
			+ ") VALUES (?)";

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

	private static final String COUNT = "SELECT COUNT(*) FROM " + TABLE_WAREHOUSE_WORKERS_STATE;

	private static final String TRUNCATE = "TRUNCATE TABLE " + TABLE_WAREHOUSE_WORKERS_STATE;

	@Inject
	WarehouseWorkersStateDaoImpl(JdbcTemplate template, TableCreator creator, @RequiresNew TransactionTemplate transactionTemplate) throws SQLException {
		super();
		this.template = template;
		this.transactionTemplate = transactionTemplate;
		// Create the table
		creator.createTable(WAREHOUSE_WORKERS_STATE_DDL_SQL);
		setInitialState();
	}

	@Override
	public WarehouseWorkersState getState() {
		return WarehouseWorkersState.valueOf(template.queryForObject(GET_STATE, String.class));
	}

	@Override
	public void setState(WarehouseWorkersState state) {
		template.update(SET_STATE, state.name());
	}

	@Override
	public void truncateAll() {
		template.update(TRUNCATE);
	}

	private void setInitialState() {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				if (template.queryForObject(COUNT, Integer.class) == 0) {
					// set the inital state
					setState(WarehouseWorkersState.NORMAL);
				}
				return null;
			}
		});
	}
}
