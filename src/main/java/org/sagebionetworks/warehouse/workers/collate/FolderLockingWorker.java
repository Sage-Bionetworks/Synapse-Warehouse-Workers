package org.sagebionetworks.warehouse.workers.collate;

import org.sagebionetworks.warehouse.workers.SemaphoreGatedRunnerProvider;
import org.sagebionetworks.warehouse.workers.model.FolderState;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;
import org.sagebionetworks.workers.util.aws.message.MessageDrivenRunner;
import org.sagebionetworks.workers.util.aws.message.RecoverableMessageException;
import org.sagebionetworks.workers.util.progress.ProgressCallback;
import org.sagebionetworks.workers.util.progress.ProgressingRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunner;
import org.sagebionetworks.workers.util.semaphore.SemaphoreGatedRunnerConfiguration;

import com.amazonaws.services.sqs.model.Message;
import com.google.inject.Inject;

/**
 * This worker will attempt to acquire and hold the global lock on a folder. If
 * while a lock is held this worker will run the actual worker that does the
 * collation of the folder.
 */
public class FolderLockingWorker implements MessageDrivenRunner {

	public static final int TIMEOUT_SEC = 2*60;

	SemaphoreGatedRunnerProvider semaphoreProvider;
	LockedFolderRunner collateWorker;

	@Inject
	public FolderLockingWorker(SemaphoreGatedRunnerProvider semaphoreProvider,
			LockedFolderRunner collateWorker) {
		super();
		this.semaphoreProvider = semaphoreProvider;
		this.collateWorker = collateWorker;
	}

	@Override
	public void run(final ProgressCallback<Message> progressCallback,
			final Message message) throws RecoverableMessageException,
			Exception {
		// Read the folder data from the message
		final FolderState folder = XMLUtils.fromXML(message.getBody(),
				FolderState.class, FolderState.DTO_ALIAS);
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
				// This work does the real work while holding the lock on the folder's path.
				collateWorker.runWhileHoldingLock(progressCallback, folder);
			}
		});
		// Create a new gate.
		SemaphoreGatedRunner gatedRunner = semaphoreProvider.createRunner(gateConfig);
		// start the gate to attempt to get the lock.
		gatedRunner.run();
	}

}
