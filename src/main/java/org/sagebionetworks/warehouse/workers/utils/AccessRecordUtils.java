package org.sagebionetworks.warehouse.workers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;

public class AccessRecordUtils {

	private static final String AUTH_V1 = "/auth/v1";
	private static final String FILE_V1 = "/file/v1";
	private static final String REPO_V1 = "/repo/v1";
	private static final String R_CLIENT = "synapseRClient";
	private static final String PYTHON_CLIENT = "python-requests";
	private static final String WEB_CLIENT = "Synapse-Web-Client";
	private static final String JAVA_CLIENT = "Synpase-Java-Client";
	private static final String COMMAND_LINE_CLIENT = "synapsecommandlineclient";
	private static final String ELB_CLIENT = "ELB-HealthChecker";

	private static final Pattern ENTITY_PATTERN = Pattern.compile("/entity/(syn\\d+|\\d+)");
	private static final Pattern NUMERIC_PARAM_PATTERN = Pattern.compile("/(syn\\d+|\\d+)");
	private static final String NUMBER_REPLACEMENT = "/#";

	/**
	 * Extract useful information from the access record.
	 * 
	 * @param accessRecord
	 * @return processedAccessRecord
	 */
	public static ProcessedAccessRecord processAccessRecord(AccessRecord accessRecord) {
		ProcessedAccessRecord processed = new ProcessedAccessRecord();
		processed.setSessionId(accessRecord.getSessionId());
		processed.setClient(getClient(accessRecord.getUserAgent()));
		processed.setEntityId(getEntityId(accessRecord.getRequestURL()));
		processed.setNormalizedMethodSignature(normalizeMethodSignature(accessRecord.getRequestURL(), accessRecord.getMethod()));
		return processed;
	}

	/**
	 * Normalize the access record's request URL and concatenate it with method
	 * 
	 * @param requestURL
	 * @param method
	 * @return
	 */
	public static String normalizeMethodSignature(String requestURL, String method) {
		requestURL = requestURL.toLowerCase();
		if (requestURL.startsWith(REPO_V1) || requestURL.startsWith(FILE_V1) || requestURL.startsWith(AUTH_V1)) {
			requestURL = requestURL.substring(8);
		}
		Matcher matcher = NUMERIC_PARAM_PATTERN.matcher(requestURL);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, NUMBER_REPLACEMENT);
		}
		matcher.appendTail(buffer);
		return method + " " + buffer;
	}

	/**
	 * Extract the entityId from the access record's request URL
	 * 
	 * @param requestURL
	 * @return
	 */
	public static Long getEntityId(String requestURL) {
		requestURL = requestURL.toLowerCase();
		Matcher matcher = ENTITY_PATTERN.matcher(requestURL);
        if (!matcher.find()) {
        	return null;
        }
        String entityId = matcher.group(1);
        if (entityId.startsWith("syn")) {
        	entityId = entityId.substring(3);
        }
        return Long.parseLong(entityId);
	}

	/**
	 * Determine the client from the access record's userAgent
	 * 
	 * @param userAgent
	 * @return
	 */
	public static Client getClient(String userAgent) {
		/*
		 * The order of web and java client matters since all web client is java client,
		 * but not all java client is web client.
		 */
		if (userAgent.indexOf(WEB_CLIENT) >= 0) return Client.WEB;
		if (userAgent.indexOf(JAVA_CLIENT) >= 0) return Client.JAVA;
		if (userAgent.indexOf(R_CLIENT) >= 0) return Client.R;
		/*
		 * The order of python and command line client matters.
		 */
		if (userAgent.indexOf(PYTHON_CLIENT) >= 0) return Client.PYTHON;
		if (userAgent.indexOf(COMMAND_LINE_CLIENT) >= 0) return Client.COMMAND_LINE;
		if (userAgent.indexOf(ELB_CLIENT) >= 0) return Client.ELB_HEALTHCHECKER;
		return Client.UNKNOWN;
	}

	/**
	 * 
	 * @param ar - the access record to validate
	 * @return true if all NOT NULL fields have valid values,
	 *         false otherwise.
	 */
	public static boolean isValidated(AccessRecord ar) {
		if (ar.getSessionId() 		== null) return false;
		if (ar.getElapseMS() 		== null) return false;
		if (ar.getTimestamp() 		== null) return false;
		if (ar.getThreadId() 		== null) return false;
		if (ar.getDate() 			== null) return false;
		if (ar.getMethod() 			== null) return false;
		if (ar.getVmId() 			== null) return false;
		if (ar.getInstance()		== null) return false;
		if (ar.getStack()			== null) return false;
		if (ar.getSuccess()			== null) return false;
		if (ar.getResponseStatus()	== null) return false;
		return true;
	}
}
