package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.repo.model.audit.AccessRecord;

public class AccessRecordTestUtil {


	/**
	 * 
	 * @return a validated AccessRecord
	 */
	public static AccessRecord createValidatedAccessRecord() {
		AccessRecord ar = new AccessRecord();
		// required fields
		ar.setSessionId("036f8cda-9a08-46ec-a06e-29f08dad44f6");
		ar.setElapseMS(110L);
		ar.setTimestamp(1428067735912L);
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
