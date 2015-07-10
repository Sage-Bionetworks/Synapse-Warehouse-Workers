package org.sagebionetworks.warehouse.workers;

import java.util.concurrent.ScheduledExecutorService;

public class AccessRecordBootstrapImpl implements AccessRecordBootstrap {

	ScheduledExecutorService scheduler;
	
	public void start() {
//		scheduler = Executors.newScheduledThreadPool(1);
//		ScheduledFuture future = scheduler.scheduleAtFixedRate(new Runnable() {
//			
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		}, 987, 1013, TimeUnit.MILLISECONDS);
		
		
	}

	public void shutdown() {
		scheduler.shutdown();
	}

}
