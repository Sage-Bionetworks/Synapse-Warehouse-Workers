package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class FileHandleRecordDaoImplTest {
	FileHandleRecordDao dao = TestContext.singleton().getInstance(FileHandleRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(FileHandleRecordDaoImpl.FILE_HANDLE_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		FileHandleSnapshot record1 = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		FileHandleSnapshot record2 = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(Long.parseLong(record1.getId())));
		assertEquals(record2, dao.get(Long.parseLong(record2.getId())));

		FileHandleSnapshot record3 = ObjectSnapshotTestUtil.createValidFileHandleSnapshot();
		record3.setId(record2.getId());
		record3.setFileName("new file name");
		dao.insert(Arrays.asList(record3));
		assertEquals(record2, dao.get(Long.parseLong(record3.getId())));
	}

	@Test
	public void testRecordWithAllFields() {
		FileHandleSnapshot record = new FileHandleSnapshot();
		record.setId("1");
		record.setCreatedOn(new Date());
		record.setCreatedBy("2");
		record.setConcreteType("concreteType");
		record.setFileName("fileName");
		// not required fields
		record.setBucket("bucket");
		record.setKey("key");
		record.setContentMd5("contentMd5");
		record.setContentSize(3L);
		record.setStorageLocationId(4L);
		dao.insert(Arrays.asList(record));
		assertEquals(record, dao.get(Long.parseLong(record.getId())));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong());
	}
}
