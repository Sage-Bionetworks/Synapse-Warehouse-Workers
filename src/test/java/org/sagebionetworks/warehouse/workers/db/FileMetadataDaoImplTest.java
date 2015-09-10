package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.*;
import static org.sagebionetworks.warehouse.workers.model.FileState.State.*;

import org.junit.After;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.FileState;


public class FileMetadataDaoImplTest {

	FileMetadataDao dao = TestContext.singleton().getInstance(FileMetadataDao.class);
	
	@After
	public void after(){
		dao.truncateAll();
	}
	
	@Test
	public void testGet(){
		String bucket = "some/bucket";
		String key = "someKey";
		assertFalse(dao.doesFileExist(bucket, key));
		FileState state = dao.getFileState(bucket, key);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(key, state.getKey());
		assertEquals(UNKNOWN, state.getState());
		assertNotNull(state.getUpdatedOn());
		assertEquals(null, state.getError());
		assertEquals(null, state.getErrorDetails());
		//the file should now exist
		assertTrue(dao.doesFileExist(bucket, key));
		// Getting the state for an file that already exists should work.
		FileState clone = dao.getFileState(bucket, key);
		assertEquals(state, clone);
	}
	
	@Test
	public void testSetFailed(){
		String bucket = "some/bucket";
		String key = "someKey";
		// ensure the state exists.
		FileState state = dao.getFileState(bucket, key);
		assertNotNull(state);
		Throwable reason = new Throwable("Something went wrong");
		// Set it to failed.
		dao.setFileStateFailed(bucket, key, reason);
		state = dao.getFileState(bucket, key);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(key, state.getKey());
		assertEquals(FAILED, state.getState());
		assertNotNull(state.getUpdatedOn());
		assertEquals("Something went wrong", state.getError());
		assertNotNull(state.getErrorDetails());
	}
	
	@Test
	public void testSetProcessing(){
		String bucket = "some/bucket";
		String key = "someKey";
		// ensure the state exists.
		FileState state = dao.getFileState(bucket, key);
		assertNotNull(state);
		dao.setFileState(bucket, key, SUBMITTED);
		state = dao.getFileState(bucket, key);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(key, state.getKey());
		assertEquals(SUBMITTED, state.getState());
		assertNotNull(state.getUpdatedOn());
		assertEquals(null, state.getError());
		assertEquals(null, state.getErrorDetails());
	}

}
