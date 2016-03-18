package org.sagebionetworks.warehouse.workers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.db.snapshot.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public class RunDuringMaintenanceStateGateTest {

	private WarehouseWorkersStateDao mockDao;
	private RunDuringMaintenanceStateGate gate;

	@Before
	public void before() {
		mockDao = Mockito.mock(WarehouseWorkersStateDao.class);
		gate = new RunDuringMaintenanceStateGate(mockDao);
	}

	@Test
	public void test() {
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		assertFalse(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		assertTrue(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.READ_ONLY);
		assertFalse(gate.canRun());
	}
}
