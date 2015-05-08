package org.sagebionetworks.warehouse.workers.semaphore;

import static org.sagebionetworks.warehouse.workers.db.Sql.COL_TABLE_SEM_LOCK_EXPIRES_ON;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_TABLE_SEM_LOCK_LOCK_KEY;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_TABLE_SEM_LOCK_TOKEN;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_TABLE_SEM_MAST_KEY;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_SEMAPHORE_MASTER;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_SEMAPHORE_LOCK;

import java.sql.Connection;
import java.util.UUID;

import javax.sql.DataSource;

import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.inject.Inject;

/**
 * Basic database backed multiple lock semaphore. The semaphore involves two
 * tables, a master table, with a single row per unique lock key, and a lock
 * table that can contain multiple tokens for each lock. All operations on the
 * lock table are gated with a SELECT FOR UPDATE on the master table's key row.
 * This ensure all checks and changes occur serially.
 * 
 * @author John
 * 
 */
public class MultipleLockSemaphoreImpl implements MultipleLockSemaphore {

	private static final String SQL_DELETE_ALL_MASTER = "DELETE FROM "+TABLE_SEMAPHORE_MASTER+" WHERE "+COL_TABLE_SEM_MAST_KEY+" IS NOT NULL";
	private static final String SQL_DELETE_LOCK_WITH_TOKEN = "DELETE FROM "
			+ TABLE_SEMAPHORE_LOCK + " WHERE " + COL_TABLE_SEM_LOCK_TOKEN
			+ " = ?";
	private static final String SQL_INSERT_NEW_LOCK = "INSERT INTO "
			+ TABLE_SEMAPHORE_LOCK + "(" + COL_TABLE_SEM_LOCK_LOCK_KEY + ", "
			+ COL_TABLE_SEM_LOCK_TOKEN + ", " + COL_TABLE_SEM_LOCK_EXPIRES_ON
			+ ") VALUES (?, ?, (CURRENT_TIMESTAMP + INTERVAL ? SECOND))";
	private static final String SQL_COUNT_OUTSTANDING_LOCKS = "SELECT COUNT(*) FROM "
			+ TABLE_SEMAPHORE_LOCK
			+ " WHERE "
			+ COL_TABLE_SEM_LOCK_LOCK_KEY
			+ " = ?";
	private static final String SQL_DELETE_EXPIRED_LOCKS = "DELETE FROM "
			+ TABLE_SEMAPHORE_LOCK + " WHERE " + COL_TABLE_SEM_LOCK_LOCK_KEY
			+ " = ? AND " + COL_TABLE_SEM_LOCK_EXPIRES_ON
			+ " < CURRENT_TIMESTAMP";
	private static final String SQL_SELECT_MASTER_KEY_FOR_UPDATE = "SELECT "
			+ COL_TABLE_SEM_MAST_KEY + " FROM " + TABLE_SEMAPHORE_MASTER
			+ " WHERE " + COL_TABLE_SEM_MAST_KEY + " = ? FOR UPDATE";
	private static final String SQL_INSERT_IGNORE_MASTER = "INSERT IGNORE INTO "
			+ TABLE_SEMAPHORE_MASTER
			+ " ("
			+ COL_TABLE_SEM_MAST_KEY
			+ ") VALUES (?)";
	
	private static final String SEMAPHORE_LOCK_DDL_SQL = "SemaphoreLock.ddl.sql";
	private static final String SEMAPHORE_MASTER_DDL_SQL = "SemaphoreMaster.ddl.sql";
	
	JdbcTemplate template;
	DataSourceTransactionManager transactionManager;
	TransactionTemplate transactionTemplate;

	@Inject
	MultipleLockSemaphoreImpl(JdbcTemplate template, DataSource dataSource) {
		super();
		// This class needs a transaction manager.
		this.transactionManager = new DataSourceTransactionManager(dataSource);
		DefaultTransactionDefinition transactionDef;
		transactionDef = new DefaultTransactionDefinition();
		transactionDef.setIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
		transactionDef.setReadOnly(false);
		transactionDef
				.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionDef.setName("TableIndexDAOImpl");
		this.transactionTemplate = new TransactionTemplate(transactionManager,
				transactionDef);
		this.template = template;

		// Create the tables
		this.template.update(ClasspathUtils
				.loadStringFromClassPath(SEMAPHORE_MASTER_DDL_SQL));
		this.template.update(ClasspathUtils
				.loadStringFromClassPath(SEMAPHORE_LOCK_DDL_SQL));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphore
	 * #attemptToAquireLock(java.lang.String, long, int)
	 */
	public String attemptToAcquireLock(final String key, final long timeoutSec,
			final int maxLockCount) {
		// This need to occur in a transaction.
		return transactionTemplate.execute(new TransactionCallback<String>() {

			public String doInTransaction(TransactionStatus status) {
				// step one, ensure we have a master lock
				template.update(SQL_INSERT_IGNORE_MASTER, key);
				// Now lock the master row. This ensure all operations on this
				// key occur in serially.
				template.queryForObject(SQL_SELECT_MASTER_KEY_FOR_UPDATE,
						String.class, key);
				// delete expired locks
				template.update(SQL_DELETE_EXPIRED_LOCKS, key);
				// Count the remaining locks
				long count = template.queryForObject(
						SQL_COUNT_OUTSTANDING_LOCKS, Long.class, key);
				if (count < maxLockCount) {
					// issue a lock
					String token = UUID.randomUUID().toString();
					template.update(SQL_INSERT_NEW_LOCK, key, token, timeoutSec);
					return token;
				}
				// No token for you!
				return null;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphore
	 * #releaseLock(java.lang.String, java.lang.String)
	 */
	public boolean releaseLock(final String key, final String token) {
		// This need to occur in a transaction.
		return transactionTemplate.execute(new TransactionCallback<Boolean>() {

			public Boolean doInTransaction(TransactionStatus status) {
				// Now lock the master row. This ensure all operations on this
				// key occur in serially.
				template.queryForObject(SQL_SELECT_MASTER_KEY_FOR_UPDATE,
						String.class, key);
				// delete expired locks
				int changes = template
						.update(SQL_DELETE_LOCK_WITH_TOKEN, token);
				return changes > 0;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.semaphore.MultipleLockSemaphore#releaseAllLocks()
	 */
	public void releaseAllLocks() {
		template.update(SQL_DELETE_ALL_MASTER);
	}

}
