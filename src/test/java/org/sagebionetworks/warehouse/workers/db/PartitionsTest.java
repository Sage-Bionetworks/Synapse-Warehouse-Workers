package org.sagebionetworks.warehouse.workers.db;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This test ensures that insert ignore a record that does not belong to any
 * partitions cause exception thrown.
 */
public class PartitionsTest {
	private TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);
	private JdbcTemplate template = TestContext.singleton().getInstance(JdbcTemplate.class);
	private final String TABLE_PARTITIONS_TEST = "PARTITIONS_TEST";
	private final String COL_PARTITIONS_TEST_TIMESTAMP = "TIMESTAMP";
	public final String PARTITIONS_TEST_DDL_SQL = "PartitionsTest.ddl.sql";
	private final String TRUNCATE = "TRUNCATE TABLE " + TABLE_PARTITIONS_TEST;
	private final String INSERT = "INSERT IGNORE INTO "
			+ TABLE_PARTITIONS_TEST
			+ " ("
			+ COL_PARTITIONS_TEST_TIMESTAMP
			+ ") VALUES (?)";
	private final String CREATE_PARTITION = "ALTER TABLE "
			+ TABLE_PARTITIONS_TEST
			+ " PARTITION BY RANGE ("
			+ COL_PARTITIONS_TEST_TIMESTAMP
			+") ( PARTITION ? VALUES LESS THAN (?))";
	private final String DROP_PARTITION = "ALTER TABLE "
			+ TABLE_PARTITIONS_TEST
			+ " DROP PARTITION ?";
	private List<String> partitionsToDrop = new LinkedList<String>();

	@Before
	public void before() {
		creator.createTable(PARTITIONS_TEST_DDL_SQL);
		template.update(TRUNCATE);
	}

	@After
	public void after() {
		for (String partitionName : partitionsToDrop) {
			template.update(DROP_PARTITION, partitionName);
		}
		template.update(TRUNCATE);
	}

	@Test
	public void createPartitionTwiceTest() {
		DateTime today = new DateTime(2015, 9, 2, 0, 0);
		String partitionName = TABLE_PARTITIONS_TEST + "_" + today.getYear() + "_" + today.getMonthOfYear() + "_" + today.getDayOfMonth();
		template.update(CREATE_PARTITION, partitionName, today.getMillis());
		template.update(CREATE_PARTITION, partitionName, today.getMillis());
	}

}
