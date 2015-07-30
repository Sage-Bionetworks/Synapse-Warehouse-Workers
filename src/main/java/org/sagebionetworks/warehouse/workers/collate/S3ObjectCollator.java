package org.sagebionetworks.warehouse.workers.collate;

import java.io.IOException;
import java.util.List;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

/**
 * Abstraction for a collator that will collate a given list of S3 objects into
 * the given object.
 *
 */
public interface S3ObjectCollator {

	/**
	 * Given a S3 bucket and a list of keys from the bucket, collate all of the
	 * files into a single file and save the result to the given destination key
	 * within the same bucket.
	 * 
	 * @param progressCallback Will be called for each row written to the d
	 * @param bucket
	 *            The bucket contain the files to collate. The resulting
	 *            collated file will also be saved to this bucket.
	 * @param keysToCollate
	 *            A list of S3Object keys from the given bucket that are to be
	 *            collated into the destination S3Object. Each S3Object must be
	 *            a gzipped CSV (*.csv.gz) files sorted on the same column as
	 *            the passed sortColumnIndex.
	 * @param destinationKey
	 *            The key of the new destination object. The resulting collated
	 *            file will be put to this location and will also be a gzipped
	 *            CSV.
	 * @param sortColumnIndex
	 *            The index of the sort column from each CSV. The resulting
	 *            destination CSV will be sorted on this column as if all of the
	 *            input CSV files are sorted on this column.
	 * @throws IOException 
	 */
	public void collateCSVObjects(ProgressCallback<Void> progressCallback, String bucket, List<String> keysToCollate,
			String destinationKey, int sortColumnIndex) throws IOException;

}
