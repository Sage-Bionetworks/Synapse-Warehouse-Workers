package org.sagebionetworks.warehouse.workers.bucket;

import java.util.Iterator;
import java.util.List;

import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

/**
 * This worker stack will scan all buckets to find files and folders that have
 * not been discovered by the real time process.  This worker's primary task is to back-fill
 * all S3 objects that existed before the stack start.
 *
 */
public class BucketScanningWorker implements ProgressingRunner<Void> {
	
	BucketDaoProvider bucketDaoProvider;
	AmazonSQSClient awsSQSClient;
	List<BucketInfo> bucketList;
	
	@Inject
	public BucketScanningWorker(BucketDaoProvider bucketDaoProvider,
			AmazonSQSClient awsSQSClient, BucketInfoList toCollate) {
		super();
		this.bucketDaoProvider = bucketDaoProvider;
		this.awsSQSClient = awsSQSClient;
		this.bucketList = toCollate.getBucketList();
	}

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		// Scan each bucket looking for files to collate
		for(BucketInfo info: bucketList){
			// Helper to scan the files
			BucketDao bucketDao = bucketDaoProvider.createBucketDao(info.getBucketName());
			String nullPrefix = null;
			Iterator<String> it = bucketDao.keyIterator(nullPrefix);
			while(it.hasNext()){
				String key = it.next();
				System.out.println(key);
			}
		}
		
	}

}
