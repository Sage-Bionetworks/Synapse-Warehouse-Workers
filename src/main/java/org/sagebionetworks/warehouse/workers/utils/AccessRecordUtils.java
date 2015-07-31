package org.sagebionetworks.warehouse.workers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;

public class AccessRecordUtils {

	private static final String R_CLIENT = "synapseRClient";
	private static final String PYTHON_CLIENT = "python-requests";
	private static final String WEB_CLIENT = "Synapse-Web-Client";
	private static final String JAVA_CLIENT = "Synpase-Java-Client";
	private static final String COMMAND_LINE_CLIENT = "synapsecommandlineclient";
	private static final String ELB_CLIENT = "ELB-HealthChecker";

	private static final String ENTITY_PATTERN = "/entity/(syn\\d+|\\d+)";
	private static final String NUMERIC_PARAM_PATTERN = "/(syn\\d+|\\d+)";
	private static final String NUMBER_REPLACEMENT = "/#";

	/**
	 * Extract useful information from the access record.
	 * 
	 * @param accessRecord
	 * @return processedAccessRecord
	 */
	public static ProcessedAccessRecord categorize(AccessRecord accessRecord) {
		ProcessedAccessRecord processed = new ProcessedAccessRecord();
		processed.setSessionId(accessRecord.getSessionId());
		processed.setClient(getClient(accessRecord.getUserAgent()));
		processed.setEntityId(getEntityId(accessRecord.getRequestURL()));
		processed.setSynapseApi(getSynapseAPI(accessRecord.getRequestURL(), accessRecord.getMethod()));
		return processed;
	}

	/**
	 * Normalize the access record's request URL and concatenate it with method
	 * 
	 * @param requestURL
	 * @param method
	 * @return
	 */
	public static String getSynapseAPI(String requestURL, String method) {
		if (requestURL.startsWith("/repo/v1")) {
			requestURL = requestURL.substring(8);
		}
		Matcher matcher = Pattern.compile(NUMERIC_PARAM_PATTERN).matcher(requestURL);
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
	public static String getEntityId(String requestURL) {
		Pattern pattern = Pattern.compile(ENTITY_PATTERN);
        Matcher matcher = pattern.matcher(requestURL);
        if (!matcher.find()) {
        	return null;
        }
        String entityId = matcher.group(1);
        if (entityId.startsWith("syn")) {
        	entityId = entityId.substring(3);
        }
        return entityId;
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
}
