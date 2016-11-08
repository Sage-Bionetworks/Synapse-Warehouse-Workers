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
import org.sagebionetworks.warehouse.workers.model.FileDownload;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class FileDownloadRecordDaoImplTest {
	FileDownloadRecordDao dao = TestContext.singleton().getInstance(FileDownloadRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(FileDownloadRecordDaoImpl.FILE_DOWNLOAD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		FileDownload record1 = ObjectSnapshotTestUtil.createValidFileDownloadRecord();
		FileDownload record2 = ObjectSnapshotTestUtil.createValidFileDownloadRecord();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getTimestamp(), record1.getUserId(),
				record1.getFileHandleId(), record1.getAssociationObjectId(), record1.getAssociationObjectType()));
		assertEquals(record2, dao.get(record2.getTimestamp(), record2.getUserId(),
				record2.getFileHandleId(), record2.getAssociationObjectId(), record2.getAssociationObjectType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), new Random().nextLong(), new Random().nextLong(),
				new Random().nextLong(), FileHandleAssociateType.TableEntity);
	}
}
