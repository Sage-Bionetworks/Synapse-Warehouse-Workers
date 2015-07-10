package org.sagebionetworks.warehouse.workers.access.record;

import org.sagebionetworks.warehouse.workers.config.Configuration;
import org.sagebionetworks.warehouse.workers.db.FileMetadataDao;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;

/**
 * This worker will scan the access recored bucket looking for folders that have not been 
 * processed.  When a folder is found, a message will be pushed to a queue to be processed.
 * 
 * @author jhill
 *
 */
public class AccessRecordBucketScanWorker implements ProgressingRunner<Void>{
	
	private FileMetadataDao fileMetadataDao;
	private AmazonS3Client s3Client;
	private String buckentName;
	
	@Inject
	public AccessRecordBucketScanWorker(FileMetadataDao fileMetadataDao, AmazonS3Client s3Client, Configuration config) {
		super();
		this.fileMetadataDao = fileMetadataDao;
		this.s3Client = s3Client;
		this.buckentName = config.getProperty("org.sagebionetworks.warehouse.worker.access.record.bucket");
	}


	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		// does the bucket exist
		
	}

}
