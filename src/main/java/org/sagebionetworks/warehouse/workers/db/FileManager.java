package org.sagebionetworks.warehouse.workers.db;

import java.util.Iterator;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Abstraction for all business logic around files and folder.
 *
 */
public interface FileManager {

	/**
	 * Notify the manager of a stream of S3 objects discovered either from a
	 * bucket event or a bucket scan. This method is idempotent so it can be
	 * called multiple times with the same objects.
	 * 
	 * Rolling S3 objects in the stream will result in the containing folder
	 * being marked as rolling.
	 * 
	 * Non-rolling files in the stream that have already been processed will be
	 * ignored. New non-rolling files will start the processing of each file.
	 * 
	 * @param objectStream A stream of S3 objects.
	 * @param progressCallback Will be called as progress is made.
	 */
	public void addS3Objects(Iterator<S3ObjectSummary> objectStream,
			ProgressCallback<Void> progressCallback);

}
