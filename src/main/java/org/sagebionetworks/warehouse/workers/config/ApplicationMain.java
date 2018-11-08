package org.sagebionetworks.warehouse.workers.config;

import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.WorkerStack;
import org.sagebionetworks.warehouse.workers.WorkerStackList;
import org.sagebionetworks.warehouse.workers.db.ConnectionPool;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TableConfigurationList;

import com.google.inject.Injector;

/**
 * The main startup and shutdown hooks for the entire application.
 * 
 * @author John
 *
 */
public class ApplicationMain {
	
	private static final Logger log = LogManager.getLogger(ApplicationMain.class);
	
	Injector injector;
	List<WorkerStack> stacks;
	
	@Inject
	public ApplicationMain(Injector injector){
		this.injector = injector;
	}
	
	/**
	 * Called when the application starts.
	 */
	public void startup(){
		try {
			log.info("Starting worker application...");
			if(injector == null){
				log.error("Injector is null.  Cannot start the application.");
				return;
			}
			// Get the list of table configurations and create tables based on their config
			List<TableConfiguration> tableConfigs = injector.getInstance(TableConfigurationList.class).getList();
			TableCreator creator = injector.getInstance(TableCreator.class);
			for (TableConfiguration config : tableConfigs) {
				log.info("Creating table: " + config.getTableName() + "...");
				creator.createTable(config);
			}
			// Get all of the worker stacks and start them.
			stacks = injector.getInstance(WorkerStackList.class).getList();
			for(WorkerStack stack: stacks){
				log.info("Starting stack: "+stack.getWorketName()+"...");
				stack.start();
			}
		} catch (Exception e) {
			log.error("Failed to start application: "+e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Application is shutting down.
	 */
	public void shutdown(){
		log.info("Shutting down the worker application");
		if(injector == null){
			// there is nothing to do if we did not start.
			return;
		}
		// Stop each stack
		for(WorkerStack stack: stacks){
			log.info("Shutting down: "+stack.getWorketName());
			stack.shutdown();
		}
		
		ConnectionPool pool = injector.getInstance(ConnectionPool.class);
		pool.close();
	}

	/**
	 * This main can be used to debug the application.
	 * Simply start the application with all of the required configuration options in the "VM Arguments"
	 * <ul>
	 * <li>-Dorg.sagebionetworks.stack.iam.id=your_aws_id</li>
	 * <li>-Dorg.sagebionetworks.stack.iam.key=your_aws_key</li>
	 * <li>-Dorg.sagebionetworks.warehouse.workers.stack=your_stack</li>
	 * <li>-Dorg.sagebionetworks.warehouse.workers.jdbc.connection.url =jdbc:mysql://your_db_host/your_db_schema</li>
	 * <li>-Dorg.sagebionetworks.warehouse.workers.jdbc.user.password =your_password</li>
	 * <li>-Dorg.sagebionetworks.warehouse.workers.jdbc.user.username =your_db_username</li>
	 * </ul>
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException{
		// Setup the container
		Injector injector = ApplicationServletContextListener.createNewGuiceInjector();
		ApplicationMain main = new ApplicationMain(injector);
		// start the application
		main.startup();
		// enter a wait state
		try{
			while(true){
				log.info("Main thread running...");
				Thread.sleep(10*1000);
			}
		}finally{
			main.shutdown();
		}
	}
}
