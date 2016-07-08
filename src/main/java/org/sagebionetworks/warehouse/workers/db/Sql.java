package org.sagebionetworks.warehouse.workers.db;

/**
 * SQL constants.
 *
 */
public class Sql {

	// FILE_STATE
	public static final String TABLE_FILE_STATE = 				"FILE_STATE";
	public static final String COL_FILE_STATE_BUCKET = 			"S3_BUCKET";
	public static final String COL_FILE_STATE_KEY = 			"S3_KEY";
	public static final String COL_FILE_STATE_STATE = 			"STATE";
	public static final String COL_FILE_STATE_UPDATED_ON = 		"UPDATED_ON";
	public static final String COL_FILE_STATE_ERROR = 			"ERROR_MESSAGE";
	public static final String COL_FILE_STATE_ERROR_DETAILS = 	"ERROR_DETAILS";

	// FOLDER_STATE
	public static final String TABLE_FOLDER_STATE = 			"FOLDER_STATE";
	public static final String COL_FOLDER_STATE_BUCKET = 		"S3_BUCKET";
	public static final String COL_FOLDER_STATE_PATH = 			"S3_PATH";
	public static final String COL_FOLDER_STATE_STATE = 		"STATE";
	public static final String COL_FOLDER_STATE_UPDATED_ON = 	"UPDATED_ON";

	// WAREHOUSE_WORKERS_STATE
	public static final String TABLE_WAREHOUSE_WORKERS_STATE = 			"WAREHOUSE_WORKERS_STATE";
	public static final String COL_WAREHOUSE_WORKERS_STATE_STATE = 		"STATE";
	public static final String COL_WAREHOUSE_WORKERS_STATE_TIMESTAMP = 	"TIMESTAMP";

	// PROCESSED_ACCESS_RECORD
	public static final String TABLE_PROCESSED_ACCESS_RECORD = 								"PROCESSED_ACCESS_RECORD";
	public static final String COL_PROCESSED_ACCESS_RECORD_SESSION_ID = 					"SESSION_ID";
	public static final String COL_PROCESSED_ACCESS_RECORD_TIMESTAMP = 						"TIMESTAMP";
	public static final String COL_PROCESSED_ACCESS_RECORD_ENTITY_ID = 						"ENTITY_ID";
	public static final String COL_PROCESSED_ACCESS_RECORD_CLIENT = 						"CLIENT";
	public static final String COL_PROCESSED_ACCESS_RECORD_NORMALIZED_METHOD_SIGNATURE = 	"NORMALIZED_METHOD_SIGNATURE";

	// ACCESS_RECORD
	public static final String TABLE_ACCESS_RECORD = 					"ACCESS_RECORD";
	public static final String COL_ACCESS_RECORD_SESSION_ID = 			"SESSION_ID";
	public static final String COL_ACCESS_RECORD_RETURN_OBJECT_ID = 	"RETURN_OBJECT_ID";
	public static final String COL_ACCESS_RECORD_ELAPSE_MS = 			"ELAPSE_MS";
	public static final String COL_ACCESS_RECORD_TIMESTAMP = 			"TIMESTAMP";
	public static final String COL_ACCESS_RECORD_VIA = 					"VIA";
	public static final String COL_ACCESS_RECORD_HOST = 				"HOST";
	public static final String COL_ACCESS_RECORD_THREAD_ID = 			"THREAD_ID";
	public static final String COL_ACCESS_RECORD_USER_AGENT = 			"USER_AGENT";
	public static final String COL_ACCESS_RECORD_QUERY_STRING = 		"QUERY_STRING";
	public static final String COL_ACCESS_RECORD_X_FORWARDED_FOR = 		"X_FORWARDED_FOR";
	public static final String COL_ACCESS_RECORD_REQUEST_URL = 			"REQUEST_URL";
	public static final String COL_ACCESS_RECORD_USER_ID = 				"USER_ID";
	public static final String COL_ACCESS_RECORD_ORIGIN = 				"ORIGIN";
	public static final String COL_ACCESS_RECORD_DATE = 				"DATE";
	public static final String COL_ACCESS_RECORD_METHOD = 				"METHOD";
	public static final String COL_ACCESS_RECORD_VM_ID = 				"VM_ID";
	public static final String COL_ACCESS_RECORD_INSTANCE = 			"INSTANCE";
	public static final String COL_ACCESS_RECORD_STACK = 				"STACK";
	public static final String COL_ACCESS_RECORD_SUCCESS = 				"SUCCESS";
	public static final String COL_ACCESS_RECORD_RESPONSE_STATUS = 		"RESPONSE_STATUS";

	// NODE_SNAPSHOT
	public static final String TABLE_NODE_SNAPSHOT = 				"NODE_SNAPSHOT";
	public static final String COL_NODE_SNAPSHOT_TIMESTAMP = 		"TIMESTAMP";
	public static final String COL_NODE_SNAPSHOT_ID = 				"ID";
	public static final String COL_NODE_SNAPSHOT_BENEFACTOR_ID = 	"BENEFACTOR_ID";
	public static final String COL_NODE_SNAPSHOT_PROJECT_ID = 		"PROJECT_ID";
	public static final String COL_NODE_SNAPSHOT_PARENT_ID = 		"PARENT_ID";
	public static final String COL_NODE_SNAPSHOT_NODE_TYPE = 		"NODE_TYPE";
	public static final String COL_NODE_SNAPSHOT_CREATED_ON = 		"CREATED_ON";
	public static final String COL_NODE_SNAPSHOT_CREATED_BY = 		"CREATED_BY";
	public static final String COL_NODE_SNAPSHOT_MODIFIED_ON = 		"MODIFIED_ON";
	public static final String COL_NODE_SNAPSHOT_MODIFIED_BY = 		"MODIFIED_BY";
	public static final String COL_NODE_SNAPSHOT_VERSION_NUMBER = 	"VERSION_NUMBER";
	public static final String COL_NODE_SNAPSHOT_FILE_HANDLE_ID = 	"FILE_HANDLE_ID";
	public static final String COL_NODE_SNAPSHOT_NAME = 			"NAME";
	public static final String COL_NODE_SNAPSHOT_IS_PUBLIC = 		"IS_PUBLIC";
	public static final String COL_NODE_SNAPSHOT_IS_CONTROLLED = 	"IS_CONTROLLED";
	public static final String COL_NODE_SNAPSHOT_IS_RESTRICTED = 	"IS_RESTRICTED";

	// TEAM_SNAPSHOT
	public static final String TABLE_TEAM_SNAPSHOT =					"TEAM_SNAPSHOT";
	public static final String COL_TEAM_SNAPSHOT_TIMESTAMP = 			"TIMESTAMP";
	public static final String COL_TEAM_SNAPSHOT_ID = 					"ID";
	public static final String COL_TEAM_SNAPSHOT_CREATED_ON = 			"CREATED_ON";
	public static final String COL_TEAM_SNAPSHOT_CREATED_BY = 			"CREATED_BY";
	public static final String COL_TEAM_SNAPSHOT_MODIFIED_ON = 			"MODIFIED_ON";
	public static final String COL_TEAM_SNAPSHOT_MODIFIED_BY = 			"MODIFIED_BY";
	public static final String COL_TEAM_SNAPSHOT_NAME = 				"NAME";
	public static final String COL_TEAM_SNAPSHOT_CAN_PUBLIC_JOIN = 		"CAN_PUBLIC_JOIN";

	// TEAM_MEMBER_SNAPSHOT
	public static final String TABLE_TEAM_MEMBER_SNAPSHOT =				"TEAM_MEMBER_SNAPSHOT";
	public static final String COL_TEAM_MEMBER_SNAPSHOT_TIMESTAMP = 	"TIMESTAMP";
	public static final String COL_TEAM_MEMBER_SNAPSHOT_TEAM_ID = 		"TEAM_ID";
	public static final String COL_TEAM_MEMBER_SNAPSHOT_MEMBER_ID = 	"MEMBER_ID";
	public static final String COL_TEAM_MEMBER_SNAPSHOT_IS_ADMIN = 		"IS_ADMIN";

	// USER_PROFILE_SNAPSHOT
	public static final String TABLE_USER_PROFILE_SNAPSHOT = 			"USER_PROFILE_SNAPSHOT";
	public static final String COL_USER_PROFILE_SNAPSHOT_TIMESTAMP = 	"TIMESTAMP";
	public static final String COL_USER_PROFILE_SNAPSHOT_ID = 			"ID";
	public static final String COL_USER_PROFILE_SNAPSHOT_USER_NAME = 	"USER_NAME";
	public static final String COL_USER_PROFILE_SNAPSHOT_FIRST_NAME = 	"FIRST_NAME";
	public static final String COL_USER_PROFILE_SNAPSHOT_LAST_NAME = 	"LAST_NAME";
	public static final String COL_USER_PROFILE_SNAPSHOT_EMAIL = 		"EMAIL";
	public static final String COL_USER_PROFILE_SNAPSHOT_LOCATION = 	"LOCATION";
	public static final String COL_USER_PROFILE_SNAPSHOT_COMPANY = 		"COMPANY";
	public static final String COL_USER_PROFILE_SNAPSHOT_POSITION = 	"POSITION";

	// ACL_SNAPSHOT
	public static final String TABLE_ACL_SNAPSHOT = 				"ACL_SNAPSHOT";
	public static final String COL_ACL_SNAPSHOT_TIMESTAMP = 		"TIMESTAMP";
	public static final String COL_ACL_SNAPSHOT_OWNER_ID = 			"OWNER_ID";
	public static final String COL_ACL_SNAPSHOT_OWNER_TYPE = 		"OWNER_TYPE";
	public static final String COL_ACL_SNAPSHOT_CREATED_ON = 		"CREATED_ON";
	public static final String COL_ACL_SNAPSHOT_RESOURCE_ACCESS = 	"RESOURCE_ACCESS";

	// USER_GROUP
	public static final String TABLE_USER_GROUP = 				"USER_GROUP";
	public static final String COL_USER_GROUP_ID = 				"ID";
	public static final String COL_USER_GROUP_IS_INDIVIDUAL = 	"IS_INDIVIDUAL";
	public static final String COL_USER_GROUP_CREATED_ON = 		"CREATED_ON";

	// CERTIFIED_QUIZ_RECORD
	public static final String TABLE_CERTIFIED_QUIZ_RECORD = 				"CERTIFIED_QUIZ_RECORD";
	public static final String COL_CERTIFIED_QUIZ_RECORD_RESPONSE_ID = 		"RESPONSE_ID";
	public static final String COL_CERTIFIED_QUIZ_RECORD_USER_ID = 			"USER_ID";
	public static final String COL_CERTIFIED_QUIZ_RECORD_PASSED = 			"PASSED";
	public static final String COL_CERTIFIED_QUIZ_RECORD_PASSED_ON = 		"PASSED_ON";

	// CERTIFIED_QUIZ_QUESTION_RECORD
	public static final String TABLE_CERTIFIED_QUIZ_QUESTION_RECORD = 					"CERTIFIED_QUIZ_QUESTION_RECORD";
	public static final String COL_CERTIFIED_QUIZ_QUESTION_RECORD_RESPONSE_ID = 		"RESPONSE_ID";
	public static final String COL_CERTIFIED_QUIZ_QUESTION_RECORD_QUESTION_INDEX = 		"QUESTION_INDEX";
	public static final String COL_CERTIFIED_QUIZ_QUESTION_RECORD_IS_CORRECT = 			"IS_CORRECT";

	// VERIFICATION_SUBMISSION_RECORD
	public static final String TABLE_VERIFICATION_SUBMISSION_RECORD = "VERIFICATION_SUBMISSION_RECORD";
	public static final String COL_VERIFICATION_SUBMISSION_RECORD_ID = "ID";
	public static final String COL_VERIFICATION_SUBMISSION_RECORD_CREATED_ON = "CREATED_ON";
	public static final String COL_VERIFICATION_SUBMISSION_RECORD_CREATED_BY = "CREATED_BY";

	// VERIFICATION_SUBMISSION_RECORD_STATE
	public static final String TABLE_VERIFICATION_SUBMISSION_STATE_RECORD = "VERIFICATION_SUBMISSION_STATE_RECORD";
	public static final String COL_VERIFICATION_SUBMISSION_STATE_RECORD_ID = "ID";
	public static final String COL_VERIFICATION_SUBMISSION_STATE_RECORD_STATE = "STATE";
	public static final String COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_ON = "CREATED_ON";
	public static final String COL_VERIFICATION_SUBMISSION_STATE_RECORD_CREATED_BY = "CREATED_BY";

	// BULK_FILE_DOWNLOAD_RECORD
	public static final String TABLE_BULK_FILE_DOWNLOAD_RECORD = "BULK_FILE_DOWNLOAD_RECORD";
	public static final String COL_BULK_FILE_DOWNLOAD_RECORD_USER_ID = "USER_ID";
	public static final String COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_ID = "OBJECT_ID";
	public static final String COL_BULK_FILE_DOWNLOAD_RECORD_OBJECT_TYPE = "OBJECT_TYPE";

	// USER_ACCESS_RECORD
	public static final String TABLE_USER_ACCESS_RECORD = "USER_ACCESS_RECORD";
	public static final String COL_USER_ACCESS_RECORD_USER_ID = "USER_ID";
	public static final String COL_USER_ACCESS_RECORD_DATE = "DATE";
	public static final String COL_USER_ACCESS_RECORD_CLIENT = "CLIENT";
}
