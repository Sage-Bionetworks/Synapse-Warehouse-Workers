package org.sagebionetworks.warehouse.workers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.Sql;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableConfigurationList;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.sagebionetworks.common.util.progress.ProgressCallback;

public class TablePartitionWorkerTest {
	@Mock
	private TableConfigurationList mockTableConfigList;
	@Mock
	private TableCreator mockCreator;
	@Mock
	private Configuration mockConfig;
	@Mock
	private ProgressCallback<Void> mockProgressCallback;
	@Mock
	private AmazonLogger mockLogger;
	private DateTime startDate;
	private DateTime endDate;
	private TablePartitionWorker worker;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);

		startDate = new DateTime().minusWeeks(1);
		endDate = new DateTime().plusDays(1);
		Mockito.when(mockConfig.getPartitionStartDate()).thenReturn(startDate);
		Mockito.when(mockConfig.getEndDate()).thenReturn(endDate);

		List<TableConfiguration> list = new LinkedList<TableConfiguration>();
		list.add(AccessRecordDaoImpl.CONFIG);
		when(mockTableConfigList.getList()).thenReturn(list);

		worker = new TablePartitionWorker(mockTableConfigList, mockCreator, mockConfig, mockLogger);
	}

	@Test
	public void allPartitionsExistTest() throws Exception {
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, startDate, endDate).keySet();
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		worker.run(mockProgressCallback);
		verify(mockProgressCallback).progressMade(null);
		verify(mockCreator, never()).addPartition(any(String.class), any(String.class), anyLong());
		verify(mockCreator, never()).dropPartition(any(String.class), any(String.class));
	}

	@Test
	public void offByOneTest() throws Exception {
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, startDate.minusDays(1), endDate.minusDays(1)).keySet();
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		worker.run(mockProgressCallback);
		verify(mockProgressCallback).progressMade(null);
		DateTime addDate = endDate.plusDays(1);
		String toAdd = PartitionUtil.getPartitionName(Sql.TABLE_ACCESS_RECORD, addDate, Period.DAY);
		verify(mockCreator).addPartition(eq(Sql.TABLE_ACCESS_RECORD), eq(toAdd), eq(PartitionUtil.floorDateByPeriod(addDate, Period.DAY).getMillis()));
		DateTime dateToDrop = startDate.minusDays(1);
		String toDrop = PartitionUtil.getPartitionName(Sql.TABLE_ACCESS_RECORD, dateToDrop, Period.DAY);
		verify(mockCreator).dropPartition(eq(Sql.TABLE_ACCESS_RECORD), eq(toDrop));
	}

	@Test
	public void dropNullTest() throws Exception {
		Set<String> nullSet = new HashSet<String>();
		nullSet.add("null");
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(nullSet);
		worker.run(mockProgressCallback);
		verify(mockProgressCallback).progressMade(null);
		verify(mockCreator, never()).dropPartition(any(String.class), any(String.class));
	}

	@Test
	public void missingOldPartitionsTest() throws Exception {
		startDate = new DateTime().minusWeeks(2);
		DateTime existStartDate = new DateTime().minusWeeks(1);
		endDate = new DateTime().plusDays(1);
		when(mockConfig.getPartitionStartDate()).thenReturn(startDate);
		when(mockConfig.getEndDate()).thenReturn(endDate);
		Set<String> existed = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, existStartDate, endDate).keySet();
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(existed);
		worker.run(mockProgressCallback);
		verify(mockCreator, times(7)).addPartition(any(String.class), any(String.class), anyLong());
	}

	@Test
	public void dropPartitionError() throws Exception{
		DateTime existingDate = startDate.minusDays(1);
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, existingDate, endDate).keySet();;
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		RuntimeException e = new RuntimeException();
		doThrow(e).when(mockCreator).dropPartition(eq(Sql.TABLE_ACCESS_RECORD), any(String.class));
		worker.run(mockProgressCallback);
		verify(mockCreator).dropPartition(eq(Sql.TABLE_ACCESS_RECORD), any(String.class));
		verify(mockLogger).logNonRetryableError(mockProgressCallback, null, TablePartitionWorker.class.getSimpleName(), e);
	}

	@Test
	public void addPartitionError() throws Exception{
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, startDate, new DateTime()).keySet();;
		when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		RuntimeException e = new RuntimeException();
		doThrow(e).when(mockCreator).addPartition(eq(Sql.TABLE_ACCESS_RECORD), any(String.class), anyLong());
		worker.run(mockProgressCallback);
		verify(mockCreator).addPartition(eq(Sql.TABLE_ACCESS_RECORD), any(String.class), anyLong());
		verify(mockLogger).logNonRetryableError(mockProgressCallback, null, TablePartitionWorker.class.getSimpleName(), e);
	}
}
