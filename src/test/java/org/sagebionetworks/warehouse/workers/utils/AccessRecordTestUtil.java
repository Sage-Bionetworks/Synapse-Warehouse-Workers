package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.sagebionetworks.repo.model.audit.AccessRecord;

public class AccessRecordTestUtil {

	static Random random = new Random();

	/**
	 * Create a unique valid access record.
	 * 
	 * @return a validated AccessRecord
	 */
	public static AccessRecord createValidAccessRecord() {
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
		ar.setUserAgent("synapser/1.0 synapseclient/1.7.2 python-requests/2.18.4");
		ar.setUserId(random.nextLong());
		ar.setOauthClientId(Long.toString(random.nextLong()));
		return ar;
	}

	/**
	 * 
	 * @param numberOfRecords
	 * @return a list of numberOfRecords valid access records
	 */
	public static List<AccessRecord> createValidAccessRecordBatch(long numberOfRecords) {
		List<AccessRecord> batch = new ArrayList<AccessRecord>();
		for (int i = 0; i < numberOfRecords; i++) {
			batch.add(createValidAccessRecord());
		}
		return batch;
	}
}
