package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;

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
		ProcessedAccessRecord par1 = new ProcessedAccessRecord();
		par1.setSessionId("28a75682-f056-40f7-9a1e-416cb703bed5");
		par1.setEntityId(2600225);
		par1.setClient(Client.WEB);
		par1.setNormalizedMethodSignature("GET /entity/#/descendants");
		ProcessedAccessRecord par2 = new ProcessedAccessRecord();
		par2.setSessionId("2cc63ec3-fc80-4b36-91d8-f381d73650f3");
		par2.setEntityId(1583492);
		par2.setClient(Client.UNKNOWN);
		par2.setNormalizedMethodSignature("GET /repo/v1/entity/#/bundle ");

		dao.insert(Arrays.asList(par1, par2));

		// verify that we have 2 entries in the table
		ProcessedAccessRecord actualPar1 = dao.get("28a75682-f056-40f7-9a1e-416cb703bed5");
		ProcessedAccessRecord actualPar2 = dao.get("2cc63ec3-fc80-4b36-91d8-f381d73650f3");
		assertEquals(par1, actualPar1);
		assertEquals(par2, actualPar2);

		// update
		par2.setClient(Client.R);
		dao.insert(Arrays.asList(par2));
		// validate that the par2 record is updated
		actualPar2 = dao.get("2cc63ec3-fc80-4b36-91d8-f381d73650f3");
		assertEquals(par2, actualPar2);
	}

}
