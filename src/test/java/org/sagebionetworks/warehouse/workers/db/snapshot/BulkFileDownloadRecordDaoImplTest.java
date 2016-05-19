package org.sagebionetworks.warehouse.workers.db.snapshot;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.warehouse.workers.db.TableCreator;
import org.sagebionetworks.warehouse.workers.db.TestContext;
import org.sagebionetworks.warehouse.workers.model.BulkFileDownloadRecord;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class BulkFileDownloadRecordDaoImplTest {
	BulkFileDownloadRecordDao dao = TestContext.singleton().getInstance(BulkFileDownloadRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(BulkFileDownloadRecordDaoImpl.BULK_FILE_DOWNLOAD_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		BulkFileDownloadRecord record1 = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();
		BulkFileDownloadRecord record2 = ObjectSnapshotTestUtil.createValidBulkFileDownloadRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getUserId(), record1.getObjectId(), record1.getObjectType()));
		assertEquals(record2, dao.get(record2.getUserId(), record2.getObjectId(), record2.getObjectType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), new Random().nextLong(), FileHandleAssociateType.TableEntity);
	}
}
