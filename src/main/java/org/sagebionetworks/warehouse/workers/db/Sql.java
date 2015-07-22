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
	
	// FOLDER_STATE
	public static final String TABLE_FOLDER_STATE = 			"FOLDER_STATE";
	public static final String COL_FOLDER_STATE_BUCKET = 		"S3_BUCKET";
	public static final String COL_FOLDER_STATE_PATH = 			"S3_PATH";
	public static final String COL_FOLDER_STATE_STATE = 		"STATE";
	public static final String COL_FOLDER_STATE_UPDATED_ON =	"UPDATED_ON";

}
