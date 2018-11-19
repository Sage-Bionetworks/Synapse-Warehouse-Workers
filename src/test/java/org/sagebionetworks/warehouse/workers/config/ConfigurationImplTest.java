package org.sagebionetworks.warehouse.workers.config;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationImplTest {
	
	@Test
	public void testGetPartitionMonths(){
		assertEquals("null test",
				ConfigurationImpl.DEFAULT_PARTITION_MONTHS,
				ConfigurationImpl.getPartitionMonths(null).intValue());
		assertEquals("lower bound test",
				ConfigurationImpl.DEFAULT_PARTITION_MONTHS,
				ConfigurationImpl.getPartitionMonths("0").intValue());
		assertEquals("upper bound test",
				ConfigurationImpl.DEFAULT_PARTITION_MONTHS,
				ConfigurationImpl.getPartitionMonths("61").intValue());
		assertEquals("valid input test",
				30,
				ConfigurationImpl.getPartitionMonths("30").intValue());
	}
	
	@Test
	public void testGetBackfillMonths(){
		assertEquals("null test",
				ConfigurationImpl.DEFAULT_BACKFILL_MONTHS,
				ConfigurationImpl.getBackfillMonths(null, ConfigurationImpl.DEFAULT_PARTITION_MONTHS).intValue());
		assertEquals("lower bound test",
				ConfigurationImpl.DEFAULT_BACKFILL_MONTHS,
				ConfigurationImpl.getBackfillMonths("0", ConfigurationImpl.DEFAULT_PARTITION_MONTHS).intValue());
		assertEquals("upper bound test",
				ConfigurationImpl.DEFAULT_BACKFILL_MONTHS,
				ConfigurationImpl.getBackfillMonths("25", ConfigurationImpl.DEFAULT_PARTITION_MONTHS).intValue());
		assertEquals("valid input test",
				30,
				ConfigurationImpl.getBackfillMonths("30", 30).intValue());
	}

}
