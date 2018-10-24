package org.sagebionetworks.warehouse.workers.db;

import java.sql.Timestamp;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.KeyData;
import org.sagebionetworks.aws.utils.s3.KeyGeneratorUtil;
import org.sagebionetworks.warehouse.workers.bucket.BucketTopicPublisher;
import org.sagebionetworks.warehouse.workers.log.AmazonLogger;
import org.sagebionetworks.warehouse.workers.model.FileState;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.warehouse.workers.model.FileState.State;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.inject.Inject;

/**
 * Business logic for processing S3 files and folder.
 *
 */
public class FileManagerImpl implements FileManager{
	
	private static Logger log = LogManager.getLogger(FileManagerImpl.class);
	private FolderMetadataDao folderMetadataDao;
	private FileMetadataDao fileMetadataDao;
	private BucketTopicPublisher bucketToTopicManager;
	private AmazonLogger amazonLogger;
	
	@Inject
	public FileManagerImpl(FolderMetadataDao folderMetadataDao,
			FileMetadataDao fileMetadataDao, BucketTopicPublisher bucketToTopicManager,
			AmazonLogger amazonLogger) {
		super();
		this.folderMetadataDao = folderMetadataDao;
		this.fileMetadataDao = fileMetadataDao;
		this.bucketToTopicManager = bucketToTopicManager;
		this.amazonLogger = amazonLogger;
	}

	@Override
	public void addS3Objects(Iterator<S3ObjectSummary> objectStream,
			ProgressCallback<Void> progressCallback) {
		// Keep track of the last rolling path added.
		String lastRollingPath = null;
		// stream through the files
		while(objectStream.hasNext()){
			// make progress for each files.
			progressCallback.progressMade(null);
			S3ObjectSummary summary = objectStream.next();
			validateObjectSummary(summary);
			// parse the key into its parts.
			try {
				KeyData keyData = KeyGeneratorUtil.parseKey(summary.getKey());
				if(keyData.isRolling()){
					// For rolling files we need to mark the folder as rolling.
					if(lastRollingPath != null && lastRollingPath.equals(keyData.getPath())){
						// This folder was already marked as rolling in this call
						continue;
					}
					// set this folder to rolling
					folderMetadataDao.createOrUpdateFolderState(new FolderState(summary.getBucketName(), keyData.getPath(), FolderState.State.ROLLING, new Timestamp(keyData.getTimeMS())));
					// used to skip folders that have already been marked as rolling.
					lastRollingPath = keyData.getPath();
				}else{
					// This is not a rolling file.
					FileState state = fileMetadataDao.getFileState(summary.getBucketName(), summary.getKey());
					if(FileState.State.UNKNOWN.equals(state.getState())){
						// This is the fist time this file has been found.
						submitFileToBeProcessed(summary);
					}
				}
			} catch (IllegalArgumentException e) {
				log.error(e.toString());
				amazonLogger.logNonRetryableError(
						progressCallback, null,
						this.getClass().getSimpleName(), e);
			}
		}
	}
	
	/**
	 * Submit a file to be processed.
	 * 
	 * @param toSubmit
	 */
	private void submitFileToBeProcessed(S3ObjectSummary toSubmit){
		// First push to the topic
		bucketToTopicManager.publishS3ObjectToTopic(toSubmit.getBucketName(), toSubmit.getKey());
		// Set the state of the file.
		fileMetadataDao.setFileState(toSubmit.getBucketName(), toSubmit.getKey(), State.SUBMITTED);
	}

	/**
	 * Validate a summary object.
	 * @param summary
	 */
	public static void validateObjectSummary(S3ObjectSummary summary){
		if(summary == null){
			throw new IllegalArgumentException("Summary cannot be null");
		}
		if(summary.getBucketName() == null){
			throw new IllegalArgumentException("Bucket name cannot be null");
		}
		if(summary.getKey() == null){
			throw new IllegalArgumentException("Key cannot be null");
		}
	}
}
