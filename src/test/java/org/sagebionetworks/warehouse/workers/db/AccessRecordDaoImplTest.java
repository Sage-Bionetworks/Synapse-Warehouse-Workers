package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class AccessRecordDaoImplTest {

	AccessRecordDao dao = TestContext.singleton().getInstance(AccessRecordDao.class);

	@Before
	public void before(){
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		AccessRecord ar1 = AccessRecordTestUtil.createValidAccessRecord();
		AccessRecord ar2 = AccessRecordTestUtil.createValidAccessRecord();
		dao.insert(Arrays.asList(ar1, ar2));
		assertEquals(ar1, dao.get(ar1.getSessionId(), ar1.getTimestamp()));
		assertEquals(ar2, dao.get(ar2.getSessionId(), ar2.getTimestamp()));

		// insert another record with sessionId2, db should ignore
		AccessRecord ar3 = AccessRecordTestUtil.createValidAccessRecord();
		ar3.setSessionId(ar2.getSessionId());
		ar3.setTimestamp(ar2.getTimestamp());
		dao.insert(Arrays.asList(ar3));
		assertEquals(ar2, dao.get(ar3.getSessionId(), ar3.getTimestamp()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get("6986eecc-42d2-4d5a-896c-a7d1c0ea951c", System.currentTimeMillis());
	}
}
