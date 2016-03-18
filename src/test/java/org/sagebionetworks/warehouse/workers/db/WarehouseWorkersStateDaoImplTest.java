package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.snapshot.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public class WarehouseWorkersStateDaoImplTest {

	WarehouseWorkersStateDao dao = TestContext.singleton().getInstance(WarehouseWorkersStateDao.class);

	@Before
	public void before() {
		dao.truncateAll();
	}

	@After
	public void after() {
		dao.truncateAll();
	}

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(1000);
		dao.setState(WarehouseWorkersState.MAINTENANCE);
		assertEquals(WarehouseWorkersState.MAINTENANCE, dao.getState());
		Thread.sleep(1000);
		dao.setState(WarehouseWorkersState.READ_ONLY);
		assertEquals(WarehouseWorkersState.READ_ONLY, dao.getState());
		Thread.sleep(1000);
		dao.setState(WarehouseWorkersState.NORMAL);
		assertEquals(WarehouseWorkersState.NORMAL, dao.getState());
	}

}
