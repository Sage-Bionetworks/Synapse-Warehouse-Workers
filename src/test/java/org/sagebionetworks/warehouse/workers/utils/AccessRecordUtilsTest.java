package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.model.UserActivityPerClientPerDay;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

public class AccessRecordUtilsTest {

	/*
	 * Client Tests
	 */
	@Test
	public void synapserClientTest() {
		assertEquals(Client.SYNAPSER, AccessRecordUtils.getClient("synapser/1.0 synapseclient/1.7.2 python-requests/2.18.4"));
	}

	@Test
	public void rClientTest() {
		assertEquals(Client.R, AccessRecordUtils.getClient("synapseRClient/1.3-0"));
	}

	@Test
	public void pythonClientTest() {
		assertEquals(Client.PYTHON, AccessRecordUtils.getClient("synapseclient/1.0.1 python-requests/2.1.0 CPython/2.7.3 Linux/3.2.0-54-virtual"));
	}

	@Test
	public void webClientTest() {
		assertEquals(Client.WEB, AccessRecordUtils.getClient("Synpase-Java-Client/64.0  Synapse-Web-Client/67.0"));
	}

	@Test
	public void javaClientTest() {
		assertEquals(Client.JAVA, AccessRecordUtils.getClient("Synpase-Java-Client/64.0"));
	}

	@Test
	public void commandLineClientTest() {
		assertEquals(Client.COMMAND_LINE,AccessRecordUtils. getClient("synapsecommandlineclient synapseclient/1.4 python-requests/1.2.3 CPython/2.7.10 Linux/4.1.13-19.30.amzn1.x86_64"));
	}

	@Test
	public void elbHealthCheckerClientTest() {
		assertEquals(Client.ELB_HEALTHCHECKER, AccessRecordUtils.getClient("ELB-HealthChecker/1.0"));
	}

	@Test
	public void unknownClientTest() {
		assertEquals(Client.UNKNOWN, AccessRecordUtils.getClient(""));
	}

	/*
	 * Entity ID Tests
	 */
	@Test
	public void entityIdWithPrefixTest() {
		Long entityId = 4623841L;
		assertEquals(entityId, AccessRecordUtils.getEntityId("/repo/v1/entity/syn4623841/bundle"));
	}

	@Test
	public void entityIdWithUperCasePrefixTest() {
		assertEquals((Long)4623841L, AccessRecordUtils.getEntityId("/repo/v1/entity/SYN4623841/bundle"));
	}

	@Test
	public void entityIdWithProperCasePrefixTest() {
		assertEquals((Long)4623841L, AccessRecordUtils.getEntityId("/repo/v1/entity/Syn4623841/bundle"));
	}

	@Test
	public void entityIdWithoutPrefixTest() {
		assertEquals((Long)4623841L, AccessRecordUtils.getEntityId("/repo/v1/entity/4623841/bundle"));
	}

	@Test
	public void endingEntityIdTest() {
		assertEquals((Long)4623841L, AccessRecordUtils.getEntityId("/repo/v1/entity/syn4623841"));
	}

	@Test
	public void nullEntityIdTest() {
		assertNull(AccessRecordUtils.getEntityId("/repo/v1/version"));
	}

	/*
	 * processAccessRecord() Test
	 */
	@Test
	public void processAccessRecordTest() {
		AccessRecord ar = new AccessRecord();
		ar.setUserAgent("Synpase-Java-Client/64.0  Synapse-Web-Client/67.0");
		ar.setMethod("GET");
		ar.setRequestURL("/repo/v1/entity/syn2600225/descendants");
		ar.setSessionId("28a75682-f056-40f7-9a1e-416cb703bed5");

		ProcessedAccessRecord expected = new ProcessedAccessRecord();
		expected.setSessionId("28a75682-f056-40f7-9a1e-416cb703bed5");
		expected.setEntityId(2600225L);
		expected.setClient(Client.WEB);
		expected.setNormalizedMethodSignature("GET /entity/#/descendants");

		assertEquals(expected, AccessRecordUtils.processAccessRecord(ar));
	}
	
	@Test
	public void testNonNormalizableMethodSignature(){
		AccessRecord accessRecord = AccessRecordTestUtil.createValidAccessRecord();
		accessRecord.setRequestURL("/some/fake/path/");
		ProcessedAccessRecord processedAccessRecord = AccessRecordUtils.processAccessRecord(accessRecord);
		assertEquals(AccessRecordUtils.NON_NORMALIZABLE_SIGNATURE, processedAccessRecord.getNormalizedMethodSignature());
	}

	/*
	 * getUserAccessRecord() Test
	 */
	@Test
	public void getUserAccessRecordTest() {
		AccessRecord ar = new AccessRecord();
		ar.setUserAgent("Synpase-Java-Client/64.0  Synapse-Web-Client/67.0");
		ar.setUserId(123L);
		ar.setDate("date");

		UserActivityPerClientPerDay expected = new UserActivityPerClientPerDay();
		expected.setUserId(123L);
		expected.setDate("date");
		expected.setClient(Client.WEB);

		assertEquals(expected, AccessRecordUtils.getUserActivityPerClientPerDay(ar));
	}

	/*
	 * isValidated() Test
	 */
	@Test
	public void validatedARTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		assertTrue(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullSessionIdTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setSessionId(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullElapseMsTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setElapseMS(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullTimestampTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setTimestamp(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullThreadIdTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setThreadId(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullRequestUrlTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setRequestURL(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullDateTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setDate(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullMethodTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setMethod(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullVmIdTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setVmId(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullInstanceTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setInstance(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullStackTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setStack(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullSuccessTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setSuccess(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	@Test
	public void nullResponseStatusTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setResponseStatus(null);
		assertFalse(AccessRecordUtils.isValidAccessRecord(ar));
	}

	/*
	 * isValidUserAccessRecord()
	 */

	@Test
	public void invalidUserARTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		ar.setUserId(null);
		assertFalse(AccessRecordUtils.isValidUserAccessRecord(ar));
	}

	@Test
	public void validUserARTest() {
		AccessRecord ar = AccessRecordTestUtil.createValidAccessRecord();
		assertTrue(AccessRecordUtils.isValidUserAccessRecord(ar));
	}
}
