package org.sagebionetworks.warehouse.workers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.FolderState;

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
		dao.createOrUpdateFolderState(new FolderState(bucket, path, FolderState.State.ROLLING, new Timestamp(now)));
		
		Iterator<FolderState> iterator = dao.listFolders(bucket, FolderState.State.ROLLING);
		List<FolderState> list = createListFromIterator(iterator);
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
		dao.createOrUpdateFolderState(new FolderState(bucket, path, FolderState.State.ROLLING, new Timestamp(nowPlus)));
		
		iterator = dao.listFolders(bucket, FolderState.State.ROLLING);
		list = createListFromIterator(iterator);
		assertNotNull(list);
		assertEquals(1, list.size());
		
		state = list.get(0);
		assertNotNull(state);
		assertEquals(bucket, state.getBucket());
		assertEquals(path, state.getPath());
		assertEquals(nowPlus, state.getUpdatedOn().getTime());
		assertEquals(FolderState.State.ROLLING, state.getState());
	}
	
	/**
	 * Convert an interator to a list.
	 * @param iterator
	 * @return
	 */
	public static <T> List<T> createListFromIterator(Iterator<T> iterator){
		List<T> list = new ArrayList<T>();
		while(iterator.hasNext()){
			list.add(iterator.next());
		}
		return list;
	}

}
