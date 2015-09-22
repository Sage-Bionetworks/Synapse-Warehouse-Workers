package org.sagebionetworks.warehouse.workers.collate;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfo;
import org.sagebionetworks.warehouse.workers.bucket.BucketInfoList;
import org.sagebionetworks.warehouse.workers.db.FolderMetadataDao;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.inject.Inject;

/**
 * This worker periodically runs and looks folders with a state of 'rolling'.
 * For each 'rolling' folder discovered a message will pushed the collate worker
 * queue.
 * 
 */
public class PeriodicRollingFolderMessageGeneratorWorker implements
		ProgressingRunner<Void> {
	private static Logger log = LogManager.getLogger(PeriodicRollingFolderMessageGeneratorWorker.class);
	FolderMetadataDao folderDao;
	List<BucketInfo> bucketList;
	AmazonSQSClient amazonSQSClient;
	String queueUrl;

	@Inject
	public PeriodicRollingFolderMessageGeneratorWorker(FolderMetadataDao folderDao,
			BucketInfoList bucketList, AmazonSQSClient amazonSQSClient,
			CollateMessageQueue queue) {
		super();
		this.folderDao = folderDao;
		this.bucketList = bucketList.getBucketList();
		this.amazonSQSClient = amazonSQSClient;
		this.queueUrl = queue.getMessageQueue().getQueueUrl();
	}

	@Override
	public void run(ProgressCallback<Void> progressCallback) throws Exception {
		log.info("Looking for rolling files...");
		// Find
		for (BucketInfo bucketInfo : bucketList) {
			Iterator<FolderState> it = folderDao.listFolders(
					bucketInfo.getBucketName(), FolderState.State.ROLLING);
			while (it.hasNext()) {
				progressCallback.progressMade(null);
				FolderState rollingFolder = it.next();
				String xml = XMLUtils.toXML(rollingFolder, FolderState.DTO_ALIAS);
				// Send the message
				amazonSQSClient.sendMessage(queueUrl, xml);
			}
		}
	}

}
