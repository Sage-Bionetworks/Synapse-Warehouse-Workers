package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public class WarehouseWorkersStateDaoImplTest {

	WarehouseWorkersStateDao dao = TestContext.singleton().getInstance(WarehouseWorkersStateDao.class);

	@Test
	public void test() {
		assertEquals(WarehouseWorkersState.NORMAL, dao.getState());
		dao.setState(WarehouseWorkersState.MAINTENANCE);
		assertEquals(WarehouseWorkersState.MAINTENANCE, dao.getState());
		dao.setState(WarehouseWorkersState.READ_ONLY);
		assertEquals(WarehouseWorkersState.READ_ONLY, dao.getState());
		dao.setState(WarehouseWorkersState.NORMAL);
		assertEquals(WarehouseWorkersState.NORMAL, dao.getState());
	}

}
