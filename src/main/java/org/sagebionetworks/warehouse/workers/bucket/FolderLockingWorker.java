package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.database.semaphore.CountingSemaphore;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerConfiguration;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerImpl;

import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker will attempt to acquire and hold the global lock on a folder. If
 * while a lock is held this worker will run the actual worker that does the
 * collation of the folder.
 */
public class FolderLockingWorker implements MessageDrivenRunner {

	public static final int TIMEOUT_SEC = 60;

	CountingSemaphore semaphore;
	FolderCollateWorker collateWorker;

	@Inject
	public FolderLockingWorker(CountingSemaphore semaphore,
			FolderCollateWorker collateWorker) {
		super();
		this.semaphore = semaphore;
		this.collateWorker = collateWorker;
	}

	@Override
	public void run(final ProgressCallback<Message> progressCallback,
			final Message message) throws RecoverableMessageException,
			Exception {
		// Read the folder data from the message
		final FolderDto folder = XMLUtils.fromXML(message.getBody(),
				FolderDto.class, FolderDto.FOLDER_DTO_ALIAS);
		// Lock on the folder and key combo to ensure only one worker collates a
		// folder at a time.
		final String lockKey = folder.getBucket() + "-" + folder.getPath();
		SemaphoreGatedRunnerConfiguration<Void> gateConfig = new SemaphoreGatedRunnerConfiguration<Void>();
		gateConfig.setLockKey(lockKey);
		gateConfig.setLockTimeoutSec(TIMEOUT_SEC);
		gateConfig.setMaxLockCount(1);
		gateConfig.setProgressCallack(new ProgressCallback<Void>() {
			@Override
			public void progressMade(Void t) {
				progressCallback.progressMade(message);
			}
		});
		gateConfig.setRunner(new ProgressingRunner<Void>() {
			@Override
			public void run(ProgressCallback<Void> progressCallback)
					throws Exception {
				// Called when we have the folder lock.
				collateWorker.run(progressCallback, folder);
			}
		});
		SemaphoreGatedRunnerImpl<Void> gatedRunner = new SemaphoreGatedRunnerImpl<Void>(
				semaphore, gateConfig);
		// start the gate to attempt to get the lock.
		gatedRunner.run();
	}

}
