package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;
import static org.sagebionetworks.warehouse.workers.db.Sql.COL_ACCESS_RECORD_TIMESTAMP;
import static org.sagebionetworks.warehouse.workers.db.Sql.TABLE_ACCESS_RECORD;
import static org.sagebionetworks.warehouse.workers.db.AccessRecordDaoImpl.ACCESS_RECORD_DDL_SQL;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sagebionetworks.warehouse.workers.config.Configuration;
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
		Mockito.when(mockConfig.getStartDate()).thenReturn(today);
		Mockito.when(mockConfig.getEndDate()).thenReturn(nextWeek);
		creator = new TableCreatorImpl(mockTemplate, mockConfig);
	}

	@Test
	public void test() {
		creator.createTableWithPartition(ACCESS_RECORD_DDL_SQL, TABLE_ACCESS_RECORD, COL_ACCESS_RECORD_TIMESTAMP, Period.DAY);
		ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
		Mockito.verify(mockTemplate).update(argument.capture());
		assertTrue(argument.getValue().contains(TABLE_ACCESS_RECORD));
		assertTrue(argument.getValue().contains(""+today.getMillis()));
		assertTrue(argument.getValue().contains(""+nextWeek.getMillis()));
	}

}
