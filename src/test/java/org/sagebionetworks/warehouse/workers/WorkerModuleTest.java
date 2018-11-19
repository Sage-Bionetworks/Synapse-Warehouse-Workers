package org.sagebionetworks.warehouse.workers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.warehouse.workers.config.Configuration;

public class WorkerModuleTest {
	@Mock
	Configuration config;
	WorkersModule module;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		module = new WorkersModule();
	}
	
	@Test
	public void testNullUsecase() {
		when(config.getProperty(WorkersModule.INSTANCE_USECASE_KEY)).thenReturn(null);
		assertEquals(module.getWorkerStackConfigurationProviderList(config).getList(),
				WorkersModule.getSnapshotWorkerStackConfigurationProviderList().getList());
	}
	
	@Test
	public void testDefaultUsecase() {
		when(config.getProperty(WorkersModule.INSTANCE_USECASE_KEY)).thenReturn("some string");
		assertEquals(module.getWorkerStackConfigurationProviderList(config).getList(),
				WorkersModule.getSnapshotWorkerStackConfigurationProviderList().getList());
	}
	
	@Test
	public void testAllUsecase() {
		when(config.getProperty(WorkersModule.INSTANCE_USECASE_KEY)).thenReturn(WorkersModule.ALL_INSTANCE);
		assertEquals(module.getWorkerStackConfigurationProviderList(config).getList(),
				WorkersModule.getAllWorkerStackConfigurationProviderList().getList());
	}
	
	@Test
	public void testDownloadReportUsecase() {
		when(config.getProperty(WorkersModule.INSTANCE_USECASE_KEY)).thenReturn(WorkersModule.DOWNLOAD_REPORT_INSTANCE);
		assertEquals(module.getWorkerStackConfigurationProviderList(config).getList(),
				WorkersModule.getDownloadReportWorkerStackConfigurationProviderList().getList());
	}
	
	@Test
	public void testCollatorUsecase() {
		when(config.getProperty(WorkersModule.INSTANCE_USECASE_KEY)).thenReturn(WorkersModule.COLLATOR_INSTANCE);
		assertEquals(module.getWorkerStackConfigurationProviderList(config).getList(),
				WorkersModule.getCollatorWorkerStackConfigurationProviderList().getList());
	}
}
