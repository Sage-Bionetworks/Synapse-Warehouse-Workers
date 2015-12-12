package org.sagebionetworks.warehouse.workers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.WarehouseWorkersStateDao;
import org.sagebionetworks.warehouse.workers.model.WarehouseWorkersState;
import org.sagebionetworks.common.util.progress.ProgressCallback;

public class MaintenanceWorkerTest {
	private WarehouseWorkersStateDao mockDao;
	private Configuration mockConfig;
	private MaintenanceWorker worker;
	private ProgressCallback<Void> mockProgressCallback;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		mockDao = Mockito.mock(WarehouseWorkersStateDao.class);
		mockConfig = Mockito.mock(Configuration.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		worker = new MaintenanceWorker(mockDao, mockConfig);
	}

	@Test
	public void beforeMaintenanceTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay() + 2);
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay() + 3);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.NORMAL);
	}

	@Test
	public void duringMaintenanceStartTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay());
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay() + 1);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.NORMAL);
	}

	@Test
	public void duringMaintenanceStartTimeSecondTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay());
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay() + 1);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		worker.run(mockProgressCallback);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.NORMAL);
	}

	@Test
	public void duringMaintenanceEndTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay() - 1);
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao).setState(WarehouseWorkersState.NORMAL);
	}

	@Test
	public void duringMaintenanceEndTimeSecondTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay() - 1);
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay());
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.MAINTENANCE);
		worker.run(mockProgressCallback);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao).setState(WarehouseWorkersState.NORMAL);
	}

	@Test
	public void afterMaintenanceTimeTest() throws Exception {
		DateTime time = new DateTime();
		Mockito.when(mockConfig.getMaintenanceStartTime()).thenReturn(time.getHourOfDay() - 3);
		Mockito.when(mockConfig.getMaintenanceEndTime()).thenReturn(time.getHourOfDay() - 2);
		Mockito.when(mockDao.getState()).thenReturn(WarehouseWorkersState.NORMAL);
		worker.run(mockProgressCallback);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.MAINTENANCE);
		Mockito.verify(mockDao, Mockito.never()).setState(WarehouseWorkersState.NORMAL);
	}

}
