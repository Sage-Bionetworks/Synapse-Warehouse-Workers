package org.sagebionetworks.warehouse.workers.bucket;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.BucketDaoImpl;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.WorkerStack;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.warehouse.workers.db.TestContext;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class RealTimeBucketListenerStackIntegrationTest {
	
	private static long MAX_WAIT_MS = 2*60*1000;
	
	static WorkerStack stack = TestContext.findWorkerStackByName(RealTimeBucketWorker.class.getName());

	FileMetadataDao dao = TestContext.singleton().getInstance(FileMetadataDao.class);
	BucketInfoList bucketList = TestContext.singleton().getInstance(BucketInfoList.class);
	AmazonS3Client s3Client = TestContext.singleton().getInstance(AmazonS3Client.class);
	StreamResourceProvider resourceProvider = TestContext.singleton().getInstance(StreamResourceProvider.class);
	
	@BeforeClass
	public static void beforeClass(){
		stack.start();
	}
	
	@AfterClass
	public static void afterClass(){
		stack.shutdown();
	}
	
	
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
		String key = KeyGeneratorUtil.createNewKey(21, 1L, false);
		File logFile = resourceProvider.createTempFile("test", "csv.gz");
		PrintWriter writer = resourceProvider.createGzipPrintWriter(logFile);
		writer.println("Sample data");
		writer.flush();
		writer.close();
		// Create a file in one of the buckets
		PutObjectRequest request = new PutObjectRequest(firstBucket.getBucketName(), key, logFile)
				// Both the object owner and the bucket owner get FULL_CONTROL over the object.
				.withCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
		s3Client.putObject(request);
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
