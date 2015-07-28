package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.AwsModule;
import org.sagebionetworks.warehouse.workers.WorkerStack;
import org.sagebionetworks.warehouse.workers.WorkerStackList;
import org.sagebionetworks.warehouse.workers.WorkersModule;
import org.sagebionetworks.warehouse.workers.servlet.WorkersServletModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A simple singleton for tests to reuse an application context.
 * 
 *
 */
public class TestContext {

	private static Injector SINGLETON = Guice.createInjector(
			new WorkersServletModule(), new DatabaseModule(),
			new WorkersModule(), new AwsModule());

	/**
	 * Get the singleton Injector.
	 * 
	 * @return
	 */
	public static Injector singleton() {
		return SINGLETON;
	}
	
	/**
	 * Find a configured worker stack by name.
	 * @param providerClass
	 * @return
	 */
	public static WorkerStack findWorkerStackByName(String stackName){
		List<WorkerStack> stacks = singleton().getInstance(WorkerStackList.class).getList();
		for(WorkerStack stack: stacks){
			if(stackName.equals(stack.getWorketName())){
				return stack;
			}
		}
		throw new IllegalArgumentException("Cannot find a worker stack named: "+stackName);
	}

}
