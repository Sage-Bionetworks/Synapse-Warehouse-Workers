package org.sagebionetworks.warehouse.workers.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.WorkerStack;
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
	List<WorkerStack> stackes;
	
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
		Configuration config = injector.getInstance(Configuration.class);
		List<Class<? extends WorkerStack>> stacksInterfaces = config.listAllWorkerStackInterfaces();
		stackes = new LinkedList<WorkerStack>();
		for(Class<? extends WorkerStack> clazz: stacksInterfaces){
			WorkerStack stack = injector.getInstance(clazz);
			log.info("Starting stack: "+clazz.getName()+"...");
			stackes.add(stack);
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
		for(WorkerStack stack: stackes){
			log.info("Shutting down: "+stack.getClass().getName());
			stack.shutdown();
		}
		
		ConnectionPool pool = injector.getInstance(ConnectionPool.class);
		pool.close();
	}

}
