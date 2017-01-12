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
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;
import org.sagebionetworks.warehouse.workers.utils.ObjectSnapshotTestUtil;
import org.springframework.dao.EmptyResultDataAccessException;

public class FileHandleCopyRecordDaoImplTest {
	FileHandleCopyRecordDao dao = TestContext.singleton().getInstance(FileHandleCopyRecordDao.class);
	TableCreator creator = TestContext.singleton().getInstance(TableCreator.class);

	@Before
	public void before(){
		creator.createTableWithoutPartitions(FileHandleCopyRecordDaoImpl.FILE_HANDLE_COPY_RECORD_DDL_SQL);
		dao.truncateAll();
	}

	@After
	public void after(){
		dao.truncateAll();
	}

	@Test
	public void test() {
		FileHandleCopyRecordSnapshot record1 = ObjectSnapshotTestUtil.createValidFileHandleCopyRecordSnapshot();
		FileHandleCopyRecordSnapshot record2 = ObjectSnapshotTestUtil.createValidFileHandleCopyRecordSnapshot();

		dao.insert(Arrays.asList(record1, record2));
		assertEquals(record1, dao.get(record1.getTimestamp(), record1.getUserId(),
				record1.getOriginalFileHandleId(), record1.getAssociationObjectId(), record1.getAssociationObjectType()));
		assertEquals(record2, dao.get(record2.getTimestamp(), record2.getUserId(),
				record2.getOriginalFileHandleId(), record2.getAssociationObjectId(), record2.getAssociationObjectType()));
	}

	@Test (expected=EmptyResultDataAccessException.class)
	public void notFoundTest() {
		dao.get(new Random().nextLong(), new Random().nextLong(), new Random().nextLong(),
				new Random().nextLong(), FileHandleAssociateType.TableEntity);
	}
}
