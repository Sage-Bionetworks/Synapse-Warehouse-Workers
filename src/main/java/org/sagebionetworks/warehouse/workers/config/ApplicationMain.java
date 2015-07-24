package org.sagebionetworks.warehouse.workers.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.WorkerStack;
import org.sagebionetworks.warehouse.workers.WorkerStackConfigurationProvider;
import org.sagebionetworks.warehouse.workers.WorkerStackImpl;
import org.sagebionetworks.warehouse.workers.WorkerStackList;
import org.sagebionetworks.warehouse.workers.db.ConnectionPool;

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
	
	public ApplicationMain(Injector injector){
		this.injector = injector;
	}
	
	/**
	 * Called when the application starts.
	 */
	public void startup(){
		log.info("Starting worker application...");
		if(injector == null){
			log.error("Injector is null.  Cannot start the application.");
			return;
		}
		// Get all of the worker stacks and start them.
		stacks = injector.getInstance(WorkerStackList.class).getList();
		for(WorkerStack stack: stacks){
			log.info("Starting stack: "+stack.getWorketName()+"...");
			stack.start();
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

}
