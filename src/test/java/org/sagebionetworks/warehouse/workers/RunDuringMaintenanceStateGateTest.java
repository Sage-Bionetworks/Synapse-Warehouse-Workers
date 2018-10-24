package org.sagebionetworks.warehouse.workers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;

public class RunDuringMaintenanceStateGateTest {

	@Mock
	private WarehouseWorkersStateDao mockDao;
	@Mock
	private AmazonLogger mockLogger;
	private RunDuringMaintenanceStateGate gate;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		gate = new RunDuringMaintenanceStateGate(mockDao, mockLogger);
	}

	@Test
	public void testCanRun() {
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		assertFalse(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		assertTrue(gate.canRun());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.READ_ONLY);
		assertFalse(gate.canRun());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRunFail() {
		gate.runFailed(new IllegalArgumentException());
		verify(mockLogger).logNonRetryableError(any(ProgressCallback.class), eq(null), eq("RunDuringMaintenanceStateGate"), any(Throwable.class));
	}
}
