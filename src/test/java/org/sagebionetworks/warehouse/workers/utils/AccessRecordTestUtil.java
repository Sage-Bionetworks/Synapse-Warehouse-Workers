package org.sagebionetworks.warehouse.workers.utils;

import java.util.UUID;

import org.sagebionetworks.repo.model.audit.AccessRecord;

public class AccessRecordTestUtil {


	/**
	 * 
	 * @return a validated AccessRecord
	 */
	public static AccessRecord createValidatedAccessRecord() {
		AccessRecord ar = new AccessRecord();
		// required fields
		ar.setSessionId(UUID.randomUUID().toString());
		ar.setElapseMS(110L);
		ar.setTimestamp(System.currentTimeMillis());
		ar.setThreadId(7968L);
		ar.setRequestURL("/auth/v1/session");
		ar.setDate("2015-04-03");
		ar.setMethod("POST");
		ar.setVmId("1d4067ff518ed7a4:-3e005a0b:14c713c8d92:-7ffd");
		ar.setInstance("84");
		ar.setStack("prod");
		ar.setSuccess(true);
		ar.setResponseStatus(201L);
		// non required fields
		ar.setHost("repo-staging.prod.sagebase.org");
		ar.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
		return ar;
	}
}
