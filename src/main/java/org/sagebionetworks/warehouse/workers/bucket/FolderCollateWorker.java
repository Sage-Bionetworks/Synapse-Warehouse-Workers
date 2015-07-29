package org.sagebionetworks.warehouse.workers.bucket;

import java.util.Iterator;

import javax.inject.Inject;

import org.sagebionetworks.aws.utils.s3.BucketDao;
import org.sagebionetworks.warehouse.workers.BucketDaoProvider;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

public class FolderCollateWorker implements LockedFolderRunner{
	
	BucketDaoProvider bucketDaoProvider;
	
	@Inject
	public FolderCollateWorker(BucketDaoProvider bucketDaoProvider) {
		super();
		this.bucketDaoProvider = bucketDaoProvider;
	}



	@Override
	public void runWhileHoldingLock(ProgressCallback<Void> progressCallback,
			FolderDto folder) {
		// walk all files in this folder
		BucketDao bucketDao = this.bucketDaoProvider.createBucketDao(folder.getBucket());
		Iterator<String> keyIterator = bucketDao.keyIterator(folder.getPath());
		
		
	}

}
