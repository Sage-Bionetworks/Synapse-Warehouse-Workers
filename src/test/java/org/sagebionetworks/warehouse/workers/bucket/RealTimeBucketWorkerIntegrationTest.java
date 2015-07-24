package org.sagebionetworks.warehouse.workers.bucket;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.BucketDaoImpl;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.warehouse.workers.db.TestContext;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.StringInputStream;

public class RealTimeBucketWorkerIntegrationTest {
	
	private static long MAX_WAIT_MS = 2*60*1000;
	
	static RealTimeBucketListenerStack stack = TestContext.singleton().getInstance(RealTimeBucketListenerStack.class);

	FileMetadataDao dao = TestContext.singleton().getInstance(FileMetadataDao.class);
	BucketInfoList bucketList = TestContext.singleton().getInstance(BucketInfoList.class);
	AmazonS3Client s3Client = TestContext.singleton().getInstance(AmazonS3Client.class);
	
	
	@Before
	public void before(){
		// Clear all data in the buckets before starting.
		for(BucketInfo info: bucketList.getBucketList()){
			BucketDao bucketDao = new BucketDaoImpl(s3Client, info.getBucketName());
			bucketDao.deleteAllObjectsWithPrefix(null);
		}
		dao.truncateAll();
	}
	
	@Test
	public void testCreateFile() throws Exception {
		BucketInfo firstBucket = bucketList.getBucketList().get(0);
		String bucket = firstBucket.getBucketName();
		ObjectMetadata metadata = new ObjectMetadata();
		String key = KeyGeneratorUtil.createNewKey(21, 1L, false);
		StringInputStream input = new StringInputStream("Sample data");
		// Create a file in one of the buckets
		s3Client.putObject(firstBucket.getBucketName(), key, input, metadata);
		// Now wait for the file to appear
		dao.getFileState(bucket, key);
		// wait for the file to exist
		waitForFileToExist(bucket, key);
	}

	/**
	 * Wait for a file to appear.
	 * @param bucket
	 * @param key
	 * @throws InterruptedException
	 */
	private void waitForFileToExist(String bucket, String key) throws InterruptedException{
		long start = System.currentTimeMillis();
		while(true){
			System.out.println("Waiting for file exists. bucket:"+bucket+" key: "+key+" ...");
			if(dao.doesFileExist(bucket, key)){
				return;
			}
			assertTrue((System.currentTimeMillis() - start) < MAX_WAIT_MS);
			Thread.sleep(2000);
		}
	}
}
