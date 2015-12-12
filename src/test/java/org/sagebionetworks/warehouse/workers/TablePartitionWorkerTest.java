package org.sagebionetworks.warehouse.workers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.AccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.Sql;
import org.sagebionetworks.warehouse.workers.db.TableConfiguration;
import org.sagebionetworks.warehouse.workers.db.TableConfigurationList;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.sagebionetworks.common.util.progress.ProgressCallback;

public class TablePartitionWorkerTest {
	private TableConfigurationList mockTableConfigList;
	private TableCreator mockCreator;
	private Configuration mockConfig;
	private DateTime startDate;
	private DateTime endDate;
	private TablePartitionWorker worker;
	private ProgressCallback<Void> mockProgressCallback;

	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		mockTableConfigList = Mockito.mock(TableConfigurationList.class);
		mockCreator = Mockito.mock(TableCreator.class);
		mockConfig = Mockito.mock(Configuration.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);

		startDate = new DateTime().minusWeeks(1);
		endDate = new DateTime().plusDays(1);
		Mockito.when(mockConfig.getStartDate()).thenReturn(startDate);
		Mockito.when(mockConfig.getEndDate()).thenReturn(endDate);

		List<TableConfiguration> list = new LinkedList<TableConfiguration>();
		list.add(AccessRecordDaoImpl.CONFIG);
		Mockito.when(mockTableConfigList.getList()).thenReturn(list);

		worker = new TablePartitionWorker(mockTableConfigList, mockCreator, mockConfig);
	}

	@Test
	public void allPartitionsExistTest() throws Exception {
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, startDate, endDate).keySet();
		Mockito.when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		worker.run(mockProgressCallback);
		Mockito.verify(mockProgressCallback).progressMade(null);
		Mockito.verify(mockCreator, Mockito.never()).addPartition(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
		Mockito.verify(mockCreator, Mockito.never()).dropPartition(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void offByOneTest() throws Exception {
		Set<String> set = PartitionUtil.getPartitions(Sql.TABLE_ACCESS_RECORD, Period.DAY, startDate.minusDays(1), endDate.minusDays(1)).keySet();
		Mockito.when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(set);
		worker.run(mockProgressCallback);
		Mockito.verify(mockProgressCallback).progressMade(null);
		DateTime addDate = endDate.plusDays(1);
		String toAdd = PartitionUtil.getPartitionName(Sql.TABLE_ACCESS_RECORD, addDate, Period.DAY);
		Mockito.verify(mockCreator).addPartition(Mockito.eq(Sql.TABLE_ACCESS_RECORD), Mockito.eq(toAdd), Mockito.eq(PartitionUtil.floorDateByPeriod(addDate, Period.DAY).getMillis()));
		DateTime dateToDrop = startDate.minusDays(1);
		String toDrop = PartitionUtil.getPartitionName(Sql.TABLE_ACCESS_RECORD, dateToDrop, Period.DAY);
		Mockito.verify(mockCreator).dropPartition(Mockito.eq(Sql.TABLE_ACCESS_RECORD), Mockito.eq(toDrop));
	}

	@Test
	public void dropNullTest() throws Exception {
		Set<String> nullSet = new HashSet<String>();
		nullSet.add("null");
		Mockito.when(mockCreator.getExistingPartitionsForTable(Sql.TABLE_ACCESS_RECORD)).thenReturn(nullSet);
		worker.run(mockProgressCallback);
		Mockito.verify(mockProgressCallback).progressMade(null);
		Mockito.verify(mockCreator, Mockito.never()).dropPartition(Mockito.anyString(), Mockito.anyString());
	}
}
