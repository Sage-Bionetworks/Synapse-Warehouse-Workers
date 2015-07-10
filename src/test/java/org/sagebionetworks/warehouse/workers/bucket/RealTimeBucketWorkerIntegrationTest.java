package org.sagebionetworks.warehouse.workers.bucket;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.aws.utils.s3.BucketDaoImpl;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.warehouse.workers.db.TestContext;

import com.amazonaws.services.s3.AmazonS3Client;

public class RealTimeBucketWorkerIntegrationTest {
	
	static RealTimeBucketListenerStack stack = TestContext.singleton().getInstance(RealTimeBucketListenerStack.class);

	FileMetadataDao dao = TestContext.singleton().getInstance(FileMetadataDao.class);
	BucketInfoList bucketList = TestContext.singleton().getInstance(BucketInfoList.class);
	AmazonS3Client s3Client = TestContext.singleton().getInstance(AmazonS3Client.class);
	
	@BeforeClass
	public static void beforeClass(){
		stack.start();
	}
	
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
	}
	
	@Test
	public void test(){
		
	}

}
