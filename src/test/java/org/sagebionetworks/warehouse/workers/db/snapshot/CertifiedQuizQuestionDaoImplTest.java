package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizQuestionRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizQuestionRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class CertifiedQuizQuestionDaoImplTest {
	CertifiedQuizQuestionRecordDao dao = TestContext.singleton().getInstance(CertifiedQuizQuestionRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(CertifiedQuizQuestionRecordDaoImpl.CERTIFIED_QUIZ_QUESTION_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		CertifiedQuizQuestionRecord record1 = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		CertifiedQuizQuestionRecord record2 = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getResponseId(), record1.getQuestionIndex()));
		assertEquals(record2, dao.get(record2.getResponseId(), record2.getQuestionIndex()));

		CertifiedQuizQuestionRecord record3 = ObjectSnapshotTestUtil.createValidCertifiedQuizQuestionRecord();
		record3.setResponseId(record2.getResponseId());
		record3.setQuestionIndex(record2.getQuestionIndex());
		dao.insert(Arrays.asList(record3));
		assertEquals(record2, dao.get(record3.getResponseId(), record3.getQuestionIndex()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), new Random().nextLong());
	}
}
