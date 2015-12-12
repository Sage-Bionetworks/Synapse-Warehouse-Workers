package org.sagebionetworks.warehouse.workers.collate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.db.FolderMetadataDao;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.common.util.progress.ProgressCallback;

public class FolderCollateWorkerTest {

	BucketDaoProvider mockBucketDaoProvider;
	S3ObjectCollator mockCollator;
	BucketInfoList bucketList;
	FolderMetadataDao mockFolderMetadataDao;
	BucketDao mockBucketDao;
	BucketInfo bucketInfo;
	ProgressCallback<Void> mockProgressCallback;
	FolderState folder;
	List<String> keysInBucket;
	FolderCollateWorker worker;

	ArgumentCaptor<ProgressCallback> callbackCapture;
	ArgumentCaptor<String> bucketCapture;
	ArgumentCaptor<List> keysCapture;
	ArgumentCaptor<String> destinationKeyCapture;
	ArgumentCaptor<Integer> sortIndexCapture;
	ArgumentCaptor<FolderState> stateCapture;

	@Before
	public void before() {
		mockBucketDaoProvider = Mockito.mock(BucketDaoProvider.class);
		mockCollator = Mockito.mock(S3ObjectCollator.class);
		mockFolderMetadataDao = Mockito.mock(FolderMetadataDao.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		mockBucketDao = Mockito.mock(BucketDao.class);
		bucketInfo = new BucketInfo();
		bucketInfo.setBucketName("someBucket");
		bucketInfo.setTimestampColumnIndex(3);
		bucketList = new BucketInfoList(Arrays.asList(bucketInfo));
		folder = new FolderState();
		folder.setBucket(bucketInfo.getBucketName());
		folder.setPath("123/2015-07-28");

		when(mockBucketDaoProvider.createBucketDao(anyString())).thenReturn(
				mockBucketDao);

		worker = new FolderCollateWorker(mockBucketDaoProvider, mockCollator,
				bucketList, mockFolderMetadataDao);

		callbackCapture = ArgumentCaptor.forClass(ProgressCallback.class);
		bucketCapture = ArgumentCaptor.forClass(String.class);
		keysCapture = ArgumentCaptor.forClass(List.class);
		destinationKeyCapture = ArgumentCaptor.forClass(String.class);
		sortIndexCapture = ArgumentCaptor.forClass(Integer.class);
		stateCapture = ArgumentCaptor.forClass(FolderState.class);
	}

	/**
	 * For this case some files should be collated but one file is too new to
	 * collate so the folder state should not change to collated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunWhileHoldingLockNotCollated() throws IOException {
		// These are the keys in this path.
		keysInBucket = Arrays.asList("123/2015-07-28/16-40-30-522-uuid.csv.gz",
				"123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-50-30-521-uuid-rolling.csv.gz",
				"123/2999-07-28/16-47-30-521-uuid-rolling.csv.gz");
		when(mockBucketDao.keyIterator(folder.getPath())).thenReturn(
				keysInBucket.iterator());

		// call under test
		worker.runWhileHoldingLock(mockProgressCallback, folder);
		// Should be called for the 40s and 50s
		verify(mockCollator, times(2)).replaceCSVsWithCollatedCSV(
				callbackCapture.capture(), bucketCapture.capture(),
				keysCapture.capture(), destinationKeyCapture.capture(),
				sortIndexCapture.capture());
		assertEquals(folder.getBucket(), bucketCapture.getValue());
		// Two sets of files should be collated.
		List<List> allKeys = keysCapture.getAllValues();
		assertEquals(2, allKeys.size());
		// The first batch is the 40s
		assertEquals(Arrays.asList(
				"123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz"),
				allKeys.get(1));
		// The second batch is the 50s
		assertEquals(
				Arrays.asList("123/2015-07-28/16-50-30-521-uuid-rolling.csv.gz"),
				allKeys.get(0));
		// are the destination keys correct?
		List<String> destinationKeys = destinationKeyCapture.getAllValues();
		assertEquals(2, destinationKeys.size());
		// 40s should go here
		assertTrue(destinationKeys.get(1).contains("123/2015-07-28/16-40-00-000"));
		// 50s should go here
		assertTrue(destinationKeys.get(0).contains("123/2015-07-28/16-50-00-000"));
		// since the last files is new it will not be collated so the folder's
		// state should not change.
		verify(mockFolderMetadataDao, never()).createOrUpdateFolderState(
				stateCapture.capture());
		
		verify(mockProgressCallback, times(7)).progressMade(null);
	}

	/**
	 * For this case all files should be collated and the folder should be
	 * marked as collated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunWhileHoldingLockCollated() throws IOException {
		// These are the keys in this path.
		keysInBucket = Arrays.asList("123/2015-07-28/16-40-30-522-uuid.csv.gz",
				"123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-50-30-521-uuid-rolling.csv.gz");
		when(mockBucketDao.keyIterator(folder.getPath())).thenReturn(
				keysInBucket.iterator());

		// call under test
		worker.runWhileHoldingLock(mockProgressCallback, folder);
		// Should be called for the 40s and 50s
		verify(mockCollator, times(2)).replaceCSVsWithCollatedCSV(
				callbackCapture.capture(), bucketCapture.capture(),
				keysCapture.capture(), destinationKeyCapture.capture(),
				sortIndexCapture.capture());
		assertEquals(folder.getBucket(), bucketCapture.getValue());
		// Two sets of files should be collated.
		List<List> allKeys = keysCapture.getAllValues();
		assertEquals(2, allKeys.size());
		// The first batch is the 40s
		assertEquals(Arrays.asList(
				"123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz"),
				allKeys.get(1));
		// The second batch is the 50s
		assertEquals(
				Arrays.asList("123/2015-07-28/16-50-30-521-uuid-rolling.csv.gz"),
				allKeys.get(0));
		// Should be set to collated.
		verify(mockFolderMetadataDao).createOrUpdateFolderState(
				stateCapture.capture());
		FolderState state = stateCapture.getValue();
		assertEquals(folder.getBucket(), state.getBucket());
		assertEquals(folder.getPath(), state.getPath());
		assertEquals(FolderState.State.COLLATED, state.getState());

		verify(mockProgressCallback, times(6)).progressMade(null);
	}

	/**
	 * This case has no file to collate, so should just get marked as collated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunWhileHoldingLockAlreadyCollated() throws IOException {
		// These are the keys in this path.
		keysInBucket = Arrays.asList("123/2015-07-28/16-40-30-522-uuid.csv.gz");
		when(mockBucketDao.keyIterator(folder.getPath())).thenReturn(
				keysInBucket.iterator());

		// call under test
		worker.runWhileHoldingLock(mockProgressCallback, folder);

		verify(mockCollator, never()).replaceCSVsWithCollatedCSV(
				callbackCapture.capture(), bucketCapture.capture(),
				keysCapture.capture(), destinationKeyCapture.capture(),
				sortIndexCapture.capture());
		// Should be set to collated.
		verify(mockFolderMetadataDao).createOrUpdateFolderState(
				stateCapture.capture());
		FolderState state = stateCapture.getValue();
		assertEquals(folder.getBucket(), state.getBucket());
		assertEquals(folder.getPath(), state.getPath());
		assertEquals(FolderState.State.COLLATED, state.getState());
		
		verify(mockProgressCallback, times(1)).progressMade(null);
	}

	/**
	 * For this case there are files to collate but the collation fails so the
	 * state of the folder should not be set to collated.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunWhileHoldingLockFailedToCollate() throws IOException {
		// These are the keys in this path.
		keysInBucket = Arrays.asList("123/2015-07-28/16-40-30-522-uuid.csv.gz",
				"123/2015-07-28/16-40-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-42-30-522-uuid-rolling.csv.gz",
				"123/2015-07-28/16-50-30-521-uuid-rolling.csv.gz");
		when(mockBucketDao.keyIterator(folder.getPath())).thenReturn(
				keysInBucket.iterator());
		// setup a failure
		doThrow(new RuntimeException("Some error")).when(mockCollator).replaceCSVsWithCollatedCSV(
						callbackCapture.capture(), bucketCapture.capture(),
						keysCapture.capture(), destinationKeyCapture.capture(),
						sortIndexCapture.capture());

		// call under test
		worker.runWhileHoldingLock(mockProgressCallback, folder);
		// Should be called for the 40s and 50s
		verify(mockCollator, times(2)).replaceCSVsWithCollatedCSV(
				callbackCapture.capture(), bucketCapture.capture(),
				keysCapture.capture(), destinationKeyCapture.capture(),
				sortIndexCapture.capture());
		// state should not change.
		verify(mockFolderMetadataDao, never()).createOrUpdateFolderState(
				stateCapture.capture());

	}

	@Test
	public void testCreateHoursOfDayAndMinutesIntervalZero() {
		int intervalMinutes = 5;
		Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		assertEquals(
				"00-00-00-000",
				FolderCollateWorker.createHoursOfDayAndMinutesInterval(
						cal.getTimeInMillis(), intervalMinutes));
	}

	@Test
	public void testCreateHoursOfDayAndMinutesIntervalOne() {
		int intervalMinutes = 5;
		Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		assertEquals(
				"01-00-00-000",
				FolderCollateWorker.createHoursOfDayAndMinutesInterval(
						cal.getTimeInMillis(), intervalMinutes));
	}

	@Test
	public void testCreateHoursOfDayAndMinutesIntervalMiddle() {
		int intervalMinutes = 5;
		Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 23);
		assertEquals(
				"23-20-00-000",
				FolderCollateWorker.createHoursOfDayAndMinutesInterval(
						cal.getTimeInMillis(), intervalMinutes));
	}

	@Test
	public void testCreateHoursOfDayAndMinutesIntervalAtInterval() {
		int intervalMinutes = 5;
		Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 5);
		assertEquals(
				"23-05-00-000",
				FolderCollateWorker.createHoursOfDayAndMinutesInterval(
						cal.getTimeInMillis(), intervalMinutes));
	}

	@Test
	public void testCreateHoursOfDayAndMinutesIntervalAtTopOfTheHour() {
		int intervalMinutes = 5;
		Calendar cal = KeyGeneratorUtil.getClaendarUTC();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		assertEquals(
				"23-55-00-000",
				FolderCollateWorker.createHoursOfDayAndMinutesInterval(
						cal.getTimeInMillis(), intervalMinutes));
	}

}
