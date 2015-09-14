package org.sagebionetworks.warehouse.workers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public class RunDuringNormalStateGateTest {

	private WarehouseWorkersStateDao mockDao;
	private RunDuringNormalStateGate gate;

	@Before
	public void before() {
		mockDao = Mockito.mock(WarehouseWorkersStateDao.class);
		gate = new RunDuringNormalStateGate(mockDao);
	}

	@Test
	public void test() {
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		assertTrue(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		assertFalse(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.READ_ONLY);
		assertFalse(gate.canRun());
	}
}
