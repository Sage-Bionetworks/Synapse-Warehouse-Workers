package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;

import org.junit.Test;


public class FileMetadataDaoImplTest {

	FileMetadataDao dao = TestContext.singleton().getInstance(FileMetadataDao.class);
	
	@Test
	public void testCURD(){
		FileState state = dao.getFileState("bucket", "key");
		assertNotNull(state);
	}

}
