package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;
import static org.sagebionetworks.warehouse.workers.db.Sql.*;
import static org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDaoImpl.ACCESS_RECORD_DDL_SQL;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.snapshot.AccessRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.db.snapshot.AclSnapshotDaoImpl;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.JdbcTemplate;

public class TableCreatorImplTest {
	private JdbcTemplate mockTemplate;
	private Configuration mockConfig;
	private TableCreatorImpl creator;
	private DateTime today = new DateTime();
	private DateTime nextWeek = today.plusWeeks(1);

	@Before
	public void before(){
		mockTemplate = Mockito.mock(JdbcTemplate.class);
		mockConfig = Mockito.mock(Configuration.class);
		Mockito.when(mockConfig.getPartitionStartDate()).thenReturn(today);
		Mockito.when(mockConfig.getEndDate()).thenReturn(nextWeek);
		Mockito.when(mockConfig.getProperty(Mockito.eq(TableCreatorImpl.WAREHOUSE_WORKERS_SCHEMA_KEY))).thenReturn("warehouse");
		creator = new TableCreatorImpl(mockTemplate, mockConfig);
	}

	@Test
	public void createTableWithPartitionTest() {
		creator.createTableWithPartitions(ACCESS_RECORD_DDL_SQL, TABLE_ACCESS_RECORD, COL_ACCESS_RECORD_TIMESTAMP, Period.DAY);
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockTemplate).update(argument.capture());
		assertTrue(argument.getValue().contains(TABLE_ACCESS_RECORD));
		assertTrue(argument.getValue().contains(""+PartitionUtil.floorDateByPeriod(today, Period.DAY).getMillis()));
		assertTrue(argument.getValue().contains(""+PartitionUtil.floorDateByPeriod(nextWeek, Period.DAY).getMillis()));
	}

	@Test
	public void createTableWithoutPartitionsTest() {
		creator.createTableWithoutPartitions(ACCESS_RECORD_DDL_SQL);
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockTemplate).update(argument.capture());
		assertTrue(argument.getValue().contains(TABLE_ACCESS_RECORD));
		assertFalse(argument.getValue().contains(PartitionUtil.PARTITION));
	}

	@Test
	public void createTableBasedOnTableConfigPartitionTrueTest() {
		creator.createTable(AccessRecordDaoImpl.CONFIG);
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockTemplate).update(argument.capture());
		assertTrue(argument.getValue().contains(TABLE_ACCESS_RECORD));
		assertTrue(argument.getValue().contains("PARTITION BY RANGE"));
	}

	@Test
	public void createTableBasedOnTableConfigPartitionFalseTest() {
		creator.createTable(AclSnapshotDaoImpl.CONFIG);
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockTemplate).update(argument.capture());
		assertTrue(argument.getValue().contains(TABLE_ACL_SNAPSHOT));
		assertFalse(argument.getValue().contains(PartitionUtil.PARTITION));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void doesPartitionExistForDayTest() {
		Mockito.when(mockTemplate.queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1L);
		DateTime today = new DateTime();
		String expectedSameDatePartition = PartitionUtil.getPartitionName("TEST", today, Period.DAY);
		String expectedNextDatePartition = PartitionUtil.getPartitionName("TEST", today.plusDays(1), Period.DAY);
		creator.doesPartitionExist("TEST", today.getMillis(), Period.DAY);
		Mockito.verify(mockTemplate, Mockito.times(2)).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(mockTemplate).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.eq("TEST"), Mockito.eq(expectedSameDatePartition));
		Mockito.verify(mockTemplate).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.eq("TEST"), Mockito.eq(expectedNextDatePartition));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void doesPartitionExistForMonthTest() {
		Mockito.when(mockTemplate.queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1L);
		DateTime today = new DateTime();
		String expectedSameDatePartition = PartitionUtil.getPartitionName("TEST", today, Period.MONTH);
		String expectedNextDatePartition = PartitionUtil.getPartitionName("TEST", today.plusMonths(1), Period.MONTH);
		creator.doesPartitionExist("TEST", today.getMillis(), Period.MONTH);
		Mockito.verify(mockTemplate, Mockito.times(2)).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.verify(mockTemplate).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.eq("TEST"), Mockito.eq(expectedSameDatePartition));
		Mockito.verify(mockTemplate).queryForObject(Mockito.anyString(), Mockito.eq(Long.class), Mockito.anyString(), Mockito.eq("TEST"), Mockito.eq(expectedNextDatePartition));
	}
}
