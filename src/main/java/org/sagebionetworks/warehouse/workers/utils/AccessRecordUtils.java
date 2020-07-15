package org.sagebionetworks.warehouse.workers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sagebionetworks.common.util.PathNormalizer;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;

public class AccessRecordUtils {

	private static final String SYNAPSER_CLIENT = "synapser";
	private static final String R_CLIENT = "synapseRClient";
	private static final String PYTHON_CLIENT = "synapseclient";
	private static final String WEB_CLIENT = "Synapse-Web-Client";
	private static final String OLD_JAVA_CLIENT = "Synpase-Java-Client"; // delete when old client is no longer in use
	private static final String JAVA_CLIENT = "Synapse-Java-Client";
	private static final String COMMAND_LINE_CLIENT = "synapsecommandlineclient";
	private static final String ELB_CLIENT = "ELB-HealthChecker";
	private static final String STACK_CLIENT = "SynapseRepositoryStack";
	
	private static final String CLIENT_REGEX = "/(\\S+)";
	private static final Pattern SYNAPSER_CLIENT_PATTERN = Pattern.compile(SYNAPSER_CLIENT+CLIENT_REGEX);
	private static final Pattern R_CLIENT_PATTERN = Pattern.compile(R_CLIENT+CLIENT_REGEX);
	private static final Pattern PYTHON_CLIENT_PATTERN = Pattern.compile(PYTHON_CLIENT+CLIENT_REGEX);
	private static final Pattern WEB_CLIENT_PATTERN = Pattern.compile(WEB_CLIENT+CLIENT_REGEX);
	private static final Pattern OLD_JAVA_CLIENT_PATTERN = Pattern.compile(OLD_JAVA_CLIENT+CLIENT_REGEX); // delete when old client is no longer in use
	private static final Pattern JAVA_CLIENT_PATTERN = Pattern.compile(JAVA_CLIENT+CLIENT_REGEX);
	private static final Pattern COMMAND_LINE_CLIENT_PATTERN = Pattern.compile(COMMAND_LINE_CLIENT+CLIENT_REGEX);
	private static final Pattern ELB_CLIENT_PATTERN = Pattern.compile(ELB_CLIENT+CLIENT_REGEX);
	private static final Pattern STACK_CLIENT_PATTERN = Pattern.compile(STACK_CLIENT+CLIENT_REGEX);
	
	public static final Long ANONYMOUS_ID = 273950L;
	
	public static final String NON_NORMALIZABLE_SIGNATURE = "NON_NORMALIZABLE";

	private static final Pattern ENTITY_PATTERN = Pattern.compile("/entity/(syn\\d+|\\d+)");

	/**
	 * Extract useful information from the access record.
	 * 
	 * @param accessRecord
	 * @return processedAccessRecord
	 */
	public static ProcessedAccessRecord processAccessRecord(AccessRecord accessRecord) {
		ProcessedAccessRecord processed = new ProcessedAccessRecord();
		processed.setSessionId(accessRecord.getSessionId());
		processed.setTimestamp(accessRecord.getTimestamp());
		processed.setClient(getClient(accessRecord.getUserAgent()));
		processed.setClientVersion(getClientVersion(processed.getClient(), accessRecord.getUserAgent()));
		processed.setEntityId(getEntityId(accessRecord.getRequestURL()));
		try {
			processed.setNormalizedMethodSignature(accessRecord.getMethod() + " " +PathNormalizer.normalizeMethodSignature(accessRecord.getRequestURL()));
		} catch (IllegalArgumentException e) {
			processed.setNormalizedMethodSignature(NON_NORMALIZABLE_SIGNATURE);
		}
		return processed;
	}

	/**
	 * Extract the entityId from the access record's request URL
	 * 
	 * @param requestURL
	 * @return
	 */
	public static Long getEntityId(String requestURL) {
		if (requestURL == null) {
			return null;
		}
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
	 * Determine the client version from the access record's userAgent
	 * 
	 * @param userAgent
	 * @return
	 */
	public static String getClientVersion(Client client, String userAgent) {
		if (userAgent == null) {
			return null;
		}
		Matcher matcher = null;
		switch(client) {
		case WEB:
			matcher = WEB_CLIENT_PATTERN.matcher(userAgent);
			break;
		case JAVA:
			if (userAgent.startsWith("Synpase")) { // delete this condition when old client is no longer in use
				matcher = OLD_JAVA_CLIENT_PATTERN.matcher(userAgent);
			} else {
				matcher = JAVA_CLIENT_PATTERN.matcher(userAgent);
			}
			break;
		case SYNAPSER:
			matcher = SYNAPSER_CLIENT_PATTERN.matcher(userAgent);
			break;
		case R:
			matcher = R_CLIENT_PATTERN.matcher(userAgent);
			break;
		case PYTHON:
			matcher = PYTHON_CLIENT_PATTERN.matcher(userAgent);
			break;
		case ELB_HEALTHCHECKER:
			matcher = ELB_CLIENT_PATTERN.matcher(userAgent);
			break;
		case COMMAND_LINE:
			matcher = COMMAND_LINE_CLIENT_PATTERN.matcher(userAgent);
			break;
		case STACK:
			matcher = STACK_CLIENT_PATTERN.matcher(userAgent);
			break;
		default:
			return null;
		}
		if (!matcher.find()) {
			return null;
		}
		return matcher.group(1);
	}

	/**
	 * Determine the client from the access record's userAgent
	 * 
	 * @param userAgent
	 * @return
	 */
	public static Client getClient(String userAgent) {
		if (userAgent == null) {
			return Client.UNKNOWN;
		}
		/*
		 * The order of web and java client matters since some web client call
		 * go through Java client, therefore, the USER_AGENT contains both keys
		 * for WEB and JAVA client.
		 */
		if (userAgent.indexOf(WEB_CLIENT) >= 0) return Client.WEB;
		if (userAgent.indexOf(JAVA_CLIENT) >= 0) return Client.JAVA;
		if (userAgent.indexOf(OLD_JAVA_CLIENT) >= 0) return Client.JAVA; // delete when old client is no longer in use
		if (userAgent.indexOf(SYNAPSER_CLIENT) >= 0) return Client.SYNAPSER;
		if (userAgent.indexOf(R_CLIENT) >= 0) return Client.R;
		/*
		 * The order of python and command line client matters since command
		 * line client's USER_AGENT contains python client's key.
		 */
		if (userAgent.indexOf(COMMAND_LINE_CLIENT) >= 0) return Client.COMMAND_LINE;
		if (userAgent.indexOf(PYTHON_CLIENT) >= 0) return Client.PYTHON;
		if (userAgent.indexOf(ELB_CLIENT) >= 0) return Client.ELB_HEALTHCHECKER;
		if (userAgent.indexOf(STACK_CLIENT) >= 0) return Client.STACK;
		return Client.UNKNOWN;
	}

	/**
	 * 
	 * @param ar - the access record to validate
	 * @return true if all NOT NULL fields have valid values,
	 *         false otherwise.
	 */
	public static boolean isValidAccessRecord(AccessRecord ar) {
		if (ar.getSessionId() 		== null) return false;
		if (ar.getElapseMS() 		== null) return false;
		if (ar.getTimestamp() 		== null) return false;
		if (ar.getThreadId() 		== null) return false;
		if (ar.getRequestURL()		== null) return false;
		if (ar.getDate() 			== null) return false;
		if (ar.getMethod() 			== null) return false;
		if (ar.getVmId() 			== null) return false;
		if (ar.getInstance()		== null) return false;
		if (ar.getStack()			== null) return false;
		if (ar.getSuccess()			== null) return false;
		if (ar.getResponseStatus()	== null) return false;
		return true;
	}

	public static UserActivityPerClientPerDay getUserActivityPerClientPerDay(AccessRecord accessRecord) {
		UserActivityPerClientPerDay uar = new UserActivityPerClientPerDay();
		uar.setClient(getClient(accessRecord.getUserAgent()));
		if (accessRecord.getUserId() == null) {
			uar.setUserId(ANONYMOUS_ID);
		} else {
			uar.setUserId(accessRecord.getUserId());
		}
		uar.setDate(accessRecord.getDate());
		return uar;
	}
}
