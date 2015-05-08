package org.sagebionetworks.warehouse.workers.db;

/**
 * SQL constants.
 *
 */
public class Sql {

	// FILE_STATE
	public static final String TABLE_FILE_STATE = 				"FILE_STATE";
	public static final String COL_TABLE_STATE_BUCKET = 		"S3_BUCKET";
	public static final String COL_TABLE_STATE_KEY = 			"S3_KEY";
	public static final String COL_TABLE_STATE_STATE = 			"STATE";
	public static final String COL_TABLE_STATE_UPDATED_ON =		"UPDATED_ON";
	public static final String COL_TABLE_STATE_ERROR = 			"ERROR_MESSAGE";
	public static final String COL_TABLE_STATE_ERROR_DETAILS = 	"ERROR_DETAILS";
	
	// SEMAPHORE_MASTER
	public static final String TABLE_SEMAPHORE_MASTER = "SEMAPHORE_MASTER";
	public static final String COL_TABLE_SEM_MAST_KEY = "LOCK_KEY";
	
	// SEMAPHORE_LOCK
	public static final String TABLE_SEMAPHORE_LOCK = 			"SEMAPHORE_LOCK";
	public static final String COL_TABLE_SEM_LOCK_LOCK_KEY =	"LOCK_KEY";
	public static final String COL_TABLE_SEM_LOCK_TOKEN =		"TOKEN";
	public static final String COL_TABLE_SEM_LOCK_EXPIRES_ON =	"EXPIRES_ON";

}
