package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionStateRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.VerificationSubmissionStateRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class VerificationSubmissionStateRecordDaoImplTest {
	VerificationSubmissionStateRecordDao dao = TestContext.singleton().getInstance(VerificationSubmissionStateRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(VerificationSubmissionStateRecordDaoImpl.VERIFICATION_SUBMISSION_STATE_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		VerificationSubmissionStateRecord record1 = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		VerificationSubmissionStateRecord record2 = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getId(), record1.getState()));
		assertEquals(record2, dao.get(record2.getId(), record2.getState()));

		VerificationSubmissionStateRecord record3 = ObjectSnapshotTestUtil.createValidVerificationSubmissionStateRecord();
		record3.setId(record2.getId());
		dao.insert(Arrays.asList(record3));
		assertEquals(record2, dao.get(record3.getId(), record3.getState()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), VerificationStateEnum.SUBMITTED);
	}
}
