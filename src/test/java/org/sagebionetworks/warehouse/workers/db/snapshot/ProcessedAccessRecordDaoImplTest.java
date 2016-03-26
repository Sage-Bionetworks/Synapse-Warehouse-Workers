package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.ProcessedAccessRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.ProcessedAccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.model.Client;
import org.sagebionetworks.warehouse.workers.model.ProcessedAccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

public class ProcessedAccessRecordDaoImplTest {

	ProcessedAccessRecordDao dao = TestContext.singleton().getInstance(ProcessedAccessRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(ProcessedAccessRecordDaoImpl.PROCESSED_ACCESS_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		ProcessedAccessRecord par1 = AccessRecordUtils.processAccessRecord(AccessRecordTestUtil.createValidAccessRecord());
		ProcessedAccessRecord par2 = AccessRecordUtils.processAccessRecord(AccessRecordTestUtil.createValidAccessRecord());

		dao.insert(Arrays.asList(par1, par2));

		// verify that we have 2 entries in the table
		ProcessedAccessRecord actualPar1 = dao.get(par1.getSessionId(), par1.getTimestamp());
		ProcessedAccessRecord actualPar2 = dao.get(par2.getSessionId(), par2.getTimestamp());
		assertEquals(par1, actualPar1);
		assertEquals(par2, actualPar2);

		// update
		par2.setClient(Client.R);
		dao.insert(Arrays.asList(par2));
		// validate that the par2 record is updated
		actualPar2 = dao.get(par2.getSessionId(), par2.getTimestamp());
		assertEquals(par2, actualPar2);
	}

}
