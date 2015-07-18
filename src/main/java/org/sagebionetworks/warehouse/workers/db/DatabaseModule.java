package org.sagebionetworks.warehouse.workers.db;

import java.sql.Connection;

import javax.sql.DataSource;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.database.semaphore.CountingSemaphoreImpl;
import org.sagebionetworks.warehouse.workers.db.transaction.Required;
import org.sagebionetworks.warehouse.workers.db.transaction.RequiresNew;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Bindings for database related object.
 * 
 * @author jhill
 *
 */
public class DatabaseModule extends AbstractModule {

	@Override
	protected void configure() {
		// Dao binding
		bind(ConnectionPool.class).to(ConnectionPoolImpl.class);
		bind(FileMetadataDao.class).to(FileMetadataDaoImpl.class);
		bind(FolderMetadataDao.class).to(FolderMetadataDaoImpl.class);
	}
	
	/**
	 * Create a new JdbcTemplate that uses the connection pool.
	 * 
	 * @param pool
	 * @return
	 */
	@Provides
	public JdbcTemplate createJdbcTemplate(ConnectionPool pool){
		return new JdbcTemplate(pool.getDataSource());
	}
	
	/**
	 * Gain direct access to the connection pool datasouce.s
	 * 
	 * @param pool
	 * @return
	 */
	@Provides
	public DataSource getDataSource(ConnectionPool pool){
		return pool.getDataSource();
	}
	
	/**
	 * The singleton transaction manager.
	 * @param datasource
	 * @return
	 */
	@Provides @Singleton
	public PlatformTransactionManager getTransactionManager(DataSource datasource){
		return new DataSourceTransactionManager(datasource);
	}
	
	/**
	 * Creates a new PROPAGATION_REQUIRES_NEW transaction template.
	 * @param pool
	 * @return
	 */
	@Provides @RequiresNew
	public TransactionTemplate createRequiresNewTransactionTemplate(PlatformTransactionManager transactionManager){
		DefaultTransactionDefinition transactionDef;
		transactionDef = new DefaultTransactionDefinition();
		transactionDef.setIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
		transactionDef.setReadOnly(false);
		transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionDef.setName("RequiresNewTemplate");
		return new TransactionTemplate(transactionManager,	transactionDef);
	}
	
	/**
	 * Creates a new PROPAGATION_REQUIRED transaction template.
	 * @param transactionManager
	 * @return
	 */
	@Provides @Required
	public TransactionTemplate createTransactionTemplate(PlatformTransactionManager transactionManager){
		DefaultTransactionDefinition transactionDef;
		transactionDef = new DefaultTransactionDefinition();
		transactionDef.setIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
		transactionDef.setReadOnly(false);
		transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionDef.setName("RequiredTemplate");
		return new TransactionTemplate(transactionManager,	transactionDef);
	}
	
	@Provides @Singleton
	public CountingSemaphore createCountingSemaphore(PlatformTransactionManager trxManager, DataSource datasource){
		return new CountingSemaphoreImpl(datasource, trxManager);
	}

}
