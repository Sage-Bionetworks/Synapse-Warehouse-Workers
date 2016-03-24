package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class VerificationSubmissionRecordDaoImplTest {
	VerificationSubmissionRecordDao dao = TestContext.singleton().getInstance(VerificationSubmissionRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(VerificationSubmissionRecordDaoImpl.VERIFICATION_SUBMISSION_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		VerificationSubmissionRecord record1 = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		VerificationSubmissionRecord record2 = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getId()));
		assertEquals(record2, dao.get(record2.getId()));

		VerificationSubmissionRecord record3 = ObjectSnapshotTestUtil.createValidVerificationSubmissionRecord();
		record3.setId(record2.getId());
		dao.insert(Arrays.asList(record3));
		assertEquals(record2, dao.get(record3.getId()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong());
	}
}
