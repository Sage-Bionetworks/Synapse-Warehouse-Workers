package org.sagebionetworks.warehouse.workers.db;

import java.sql.Connection;

import javax.sql.DataSource;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.database.semaphore.CountingSemaphoreImpl;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDao;
import org.sagebionetworks.warehouse.workers.db.audit.UserActivityPerMonthDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileDownloadRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileDownloadRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizQuestionRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizQuestionRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.DeletedNodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.DeletedNodeSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileHandleRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.FileHandleRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.NodeSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.NodeSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.ProcessedAccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamMemberSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamMemberSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.TeamSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserActivityPerClientPerDayDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserGroupDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserGroupDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserProfileSnapshotDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.UserProfileSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionStateRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionStateRecordDaoImpl;
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
		bind(TableCreator.class).to(TableCreatorImpl.class);
		bind(FileMetadataDao.class).to(FileMetadataDaoImpl.class);
		bind(FolderMetadataDao.class).to(FolderMetadataDaoImpl.class);
		bind(AccessRecordDao.class).to(AccessRecordDaoImpl.class);
		bind(ProcessedAccessRecordDao.class).to(ProcessedAccessRecordDaoImpl.class);
		bind(NodeSnapshotDao.class).to(NodeSnapshotDaoImpl.class);
		bind(TeamSnapshotDao.class).to(TeamSnapshotDaoImpl.class);
		bind(TeamMemberSnapshotDao.class).to(TeamMemberSnapshotDaoImpl.class);
		bind(UserProfileSnapshotDao.class).to(UserProfileSnapshotDaoImpl.class);
		bind(AclSnapshotDao.class).to(AclSnapshotDaoImpl.class);
		bind(WarehouseWorkersStateDao.class).to(WarehouseWorkersStateDaoImpl.class);
		bind(UserGroupDao.class).to(UserGroupDaoImpl.class);
		bind(CertifiedQuizRecordDao.class).to(CertifiedQuizRecordDaoImpl.class);
		bind(CertifiedQuizQuestionRecordDao.class).to(CertifiedQuizQuestionRecordDaoImpl.class);
		bind(VerificationSubmissionRecordDao.class).to(VerificationSubmissionRecordDaoImpl.class);
		bind(VerificationSubmissionStateRecordDao.class).to(VerificationSubmissionStateRecordDaoImpl.class);
		bind(FileDownloadRecordDao.class).to(FileDownloadRecordDaoImpl.class);
		bind(UserActivityPerClientPerDayDao.class).to(UserActivityPerClientPerDayDaoImpl.class);
		bind(UserActivityPerMonthDao.class).to(UserActivityPerMonthDaoImpl.class);
		bind(DeletedNodeSnapshotDao.class).to(DeletedNodeSnapshotDaoImpl.class);
		bind(FileHandleRecordDao.class).to(FileHandleRecordDaoImpl.class);
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

	@Provides
	public TableConfigurationList getTableConfigurationList() {
		TableConfigurationList tableConfigList = new TableConfigurationList();
		tableConfigList.add(AccessRecordDaoImpl.CONFIG);
		tableConfigList.add(AclSnapshotDaoImpl.CONFIG);
		tableConfigList.add(NodeSnapshotDaoImpl.CONFIG);
		tableConfigList.add(ProcessedAccessRecordDaoImpl.CONFIG);
		tableConfigList.add(TeamMemberSnapshotDaoImpl.CONFIG);
		tableConfigList.add(TeamSnapshotDaoImpl.CONFIG);
		tableConfigList.add(UserProfileSnapshotDaoImpl.CONFIG);
		tableConfigList.add(UserGroupDaoImpl.CONFIG);
		tableConfigList.add(CertifiedQuizRecordDaoImpl.CONFIG);
		tableConfigList.add(CertifiedQuizQuestionRecordDaoImpl.CONFIG);
		tableConfigList.add(VerificationSubmissionRecordDaoImpl.CONFIG);
		tableConfigList.add(VerificationSubmissionStateRecordDaoImpl.CONFIG);
		tableConfigList.add(FileDownloadRecordDaoImpl.CONFIG);
		tableConfigList.add(UserActivityPerClientPerDayDaoImpl.CONFIG);
		tableConfigList.add(UserActivityPerMonthDaoImpl.CONFIG);
		tableConfigList.add(DeletedNodeSnapshotDaoImpl.CONFIG);
		tableConfigList.add(FileHandleRecordDaoImpl.CONFIG);
		return tableConfigList;
	}
}
