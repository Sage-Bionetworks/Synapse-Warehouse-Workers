package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil;
import org.sagebionetworks.warehouse.workers.utils.PartitionUtil.Period;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

/**
 * This test ensures that insert ignore a record that does not belong to any
 * partitions cause exception thrown.
 */
public class PartitionsTest {
	private TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);
	private JdbcTemplate template = TestContext.singleton().getInstance(JdbcTemplate.class);
	private final String TABLE_PARTITIONS_TEST = "TEST";
	private final String COL_PARTITIONS_TEST_TIMESTAMP = "TIMESTAMP";
	public final String PARTITIONS_TEST_DDL_SQL = "PartitionsTest.ddl.sql";
	private final String DROP_TABLE = "DROP TABLE " + TABLE_PARTITIONS_TEST;
	private final String INSERT = "INSERT IGNORE INTO "
			+ TABLE_PARTITIONS_TEST
			+ " ("
			+ COL_PARTITIONS_TEST_TIMESTAMP
			+ ") VALUES (?)";
	private final String SELECT = "SELECT COUNT(*) "
			+ "FROM "
			+ TABLE_PARTITIONS_TEST
			+ " WHERE "
			+ COL_PARTITIONS_TEST_TIMESTAMP
			+ " =?";

	@Before
	public void before() {
		creator.createTable(PARTITIONS_TEST_DDL_SQL);
	}

	@After
	public void after() {
		template.update(DROP_TABLE);
	}

	@Test (expected=Exception.class)
	public void createPartitionTwiceTest() {
		DateTime date = new DateTime().plusYears(1);
		String partitionName = PartitionUtil.getPartitionName(TABLE_PARTITIONS_TEST, date, Period.DAY);
		assertFalse(creator.doesPartitionExist(TABLE_PARTITIONS_TEST, partitionName));
		creator.addPartition(TABLE_PARTITIONS_TEST, partitionName, date.getMillis());
		assertTrue(creator.doesPartitionExist(TABLE_PARTITIONS_TEST, partitionName));
		creator.addPartition(TABLE_PARTITIONS_TEST, partitionName, date.getMillis());
	}

	/*
	 * insert a value to the test table
	 */
	private void insert(final long value) {
		template.update(INSERT, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, value);
			}
		});
	}

	@Test
	public void insertInPartitionRangeTest() {
		DateTime today = new DateTime();
		String partitionName = PartitionUtil.getPartitionName(TABLE_PARTITIONS_TEST, today, Period.DAY);
		assertFalse(creator.doesPartitionExist(TABLE_PARTITIONS_TEST, partitionName));
		creator.addPartition(TABLE_PARTITIONS_TEST, partitionName, today.getMillis());
		assertTrue(creator.doesPartitionExist(TABLE_PARTITIONS_TEST, partitionName));
		long timestamp = today.minusDays(1).getMillis();
		insert(timestamp);
		assertTrue(doesValueExist(timestamp));
	}

	@Test
	public void insertNotInPartitionRangeTest() {
		DateTime today = new DateTime();
		String partitionName = PartitionUtil.getPartitionName(TABLE_PARTITIONS_TEST, today, Period.DAY);
		assertFalse(creator.doesPartitionExist(TABLE_PARTITIONS_TEST, partitionName));
		long timestamp = today.minusDays(1).getMillis();
		insert(timestamp);
		assertFalse(doesValueExist(timestamp));
	}

	/*
	 * check to see if the value exists in the test table
	 */
	private boolean doesValueExist(long value) {
		return template.queryForLong(SELECT, value) == 1;
	}
}
