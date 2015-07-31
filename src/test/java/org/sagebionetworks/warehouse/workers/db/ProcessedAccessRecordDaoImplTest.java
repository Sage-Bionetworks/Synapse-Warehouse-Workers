package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;

public class ProcessedAccessRecordDaoImplTest {

	ProcessedAccessRecordDao dao = TestContext.singleton().getInstance(ProcessedAccessRecordDao.class);

	@Before
	public void before(){
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void testInsertOnDuplicate() {
		List<ProcessedAccessRecord> unknownClient = dao.getUnknownClient();
		assertEquals(0, unknownClient.size());

		ProcessedAccessRecord par = new ProcessedAccessRecord();
		par.setSessionId("28a75682-f056-40f7-9a1e-416cb703bed5");
		par.setEntityId("2600225");
		par.setClient(Client.WEB);
		par.setSynapseApi("GET /entity/#/descendants");
		ProcessedAccessRecord par2 = new ProcessedAccessRecord();
		par2.setSessionId("2cc63ec3-fc80-4b36-91d8-f381d73650f3");
		par2.setEntityId("1583492");
		par2.setClient(Client.UNKNOWN);
		par2.setSynapseApi("GET /repo/v1/entity/#/bundle ");

		dao.insert(Arrays.asList(par, par2));
		unknownClient = dao.getUnknownClient();
		// validate that there is one unknown client record
		assertEquals(1, unknownClient.size());
		assertEquals(par2, unknownClient.get(0));

		par2.setClient(Client.R);
		dao.insert(Arrays.asList(par2));
		unknownClient = dao.getUnknownClient();
		// validate that the par2 record is updated
		assertEquals(0, unknownClient.size());
	}

}
