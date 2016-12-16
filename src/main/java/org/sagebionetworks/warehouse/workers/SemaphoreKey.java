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
	NODE_SNAPSHOT_WORKER,
	TEAM_SNAPSHOT_WORKER,
	TEAM_MEMBER_SNAPSHOT_WORKER,
	USER_PROFILE_SNAPSHOT_WORKER,
	ACL_RECORD_SNAPSHOT_WORKER,
	TABLE_PARTITION_WORKER,
	HEALTH_CHECK_WORKER,
	MAINTENANCE_WORKER,
	USER_GROUP_SNAPSHOT_WORKER,
	CERTIFIED_QUIZ_RECORD_WORKER,
	CERTIFIED_QUIZ_QUESTION_RECORD_WORKER,
	VERIFICATION_SUBMISSION_RECORD_WORKER,
	VERIFICATION_SUBMISSION_STATE_RECORD_WORKER,
	BULK_FILE_DOWNLOAD_RECORD_WORKER,
	BULK_FILE_HANDLE_DOWNLOAD_RECORD_WORKER,
	USER_ACTIVITY_PER_CLIENT_PER_DAY_WORKER,
	USER_ACTIVITY_PER_MONTH_WORKER,
	DELETED_NODE_SNAPSHOT_WORKER,
	FILE_HANDLE_RECORD_WORKER,
	FILE_DOWNLOAD_RECORD_WORKER,
	FILE_HANDLE_DOWNLOAD_RECORD_WORKER,
}
