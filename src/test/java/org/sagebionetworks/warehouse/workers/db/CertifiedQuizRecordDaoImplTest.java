package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDao;
import org.sagebionetworks.warehouse.workers.db.snapshot.CertifiedQuizRecordDaoImpl;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class CertifiedQuizRecordDaoImplTest {
	CertifiedQuizRecordDao dao = TestContext.singleton().getInstance(CertifiedQuizRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(CertifiedQuizRecordDaoImpl.CERTIFIED_QUIZ_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		CertifiedQuizRecord record1 = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		CertifiedQuizRecord record2 = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getResponseId()));
		assertEquals(record2, dao.get(record2.getResponseId()));

		CertifiedQuizRecord record3 = ObjectSnapshotTestUtil.createValidCertifiedQuizRecord();
		record3.setResponseId(record2.getResponseId());
		dao.insert(Arrays.asList(record3));
		assertEquals(record2, dao.get(record3.getResponseId()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong());
	}
}
