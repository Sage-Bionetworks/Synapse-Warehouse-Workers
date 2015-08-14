package org.sagebionetworks.warehouse.workers;

/**
 * The enumeration of semaphore keys used by this application.
 *
 */
public enum SemaphoreKey {
	
	BUCKET_SCANNING_WORKER,
	REALTIME_BUCKET_LISTENER_WORKER,
	PERIODIC_ROLLING_FOLDER_MESSAGE_GENERATOR,
	FOLDER_COLLATE_WORKER,
	ACCESS_RECORD_WORKER,
	PROCESS_ACCESS_RECORD_WORKER,
}
