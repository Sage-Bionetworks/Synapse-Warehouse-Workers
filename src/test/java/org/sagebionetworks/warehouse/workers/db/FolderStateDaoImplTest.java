package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FolderStateDaoImplTest {

	FolderMetadataDao dao = TestContext.singleton().getInstance(FolderMetadataDao.class);
	
	@Before
	public void before(){
		dao.truncateTable();
	}
	
	@Test
	public void testCreate(){
		long now = 1437462333000L;
		String bucket = "someBucket";
		String path = "somePath";
		dao.markFolderAsRolling(bucket, path, now);
		
		List<FolderState> list = dao.listRollingFolders(bucket);
		assertNotNull(list);
		assertEquals(1, list.size());
		FolderState state = list.get(0);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(path, state.getPath());
		assertEquals(now, state.getUpdatedOn().getTime());
		assertEquals(FolderState.State.ROLLING, state.getState());
		
		// we should be able to call it again with a new time.
		long nowPlus = now+5000;
		dao.markFolderAsRolling(bucket, path, nowPlus);
		
		list = dao.listRollingFolders(bucket);
		assertNotNull(list);
		assertEquals(1, list.size());
		
		state = list.get(0);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(path, state.getPath());
		assertEquals(nowPlus, state.getUpdatedOn().getTime());
		assertEquals(FolderState.State.ROLLING, state.getState());
	}

}
