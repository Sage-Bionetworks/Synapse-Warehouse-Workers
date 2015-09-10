package org.sagebionetworks.warehouse.workers.snapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.warehouse.workers.db.SnapshotDao;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.sqs.model.Message;

/**
 * Helper that writes a batch of records to a table
 *
 */
public class SnapshotWriter {

	/**
	 * Read K records from reader, convert K to V, and write V records to a table using dao
	 * 
	 * @param <K> the type of input record from snapshot file
	 * @param <V> the type of record that is written to a table
	 * @param reader
	 * @param dao
	 * @param batchSize
	 * @param callback
	 * @param message
	 * @return
	 * @throws IOException 
	 */
	public static <K,V> int write(ObjectCSVReader<K> reader, SnapshotDao<V> dao,
			int batchSize, ProgressCallback<Message> callback, Message message,
			SnapshotWorker<K,V> worker) throws IOException {
		K record = null;
		List<V> batch = new ArrayList<V>(batchSize);

		int noRecords = 0;
		while ((record = reader.next()) != null) {
			V snapshot = worker.convert(record);
			if (snapshot != null)
				batch.add(snapshot);
			if (batch.size() >= batchSize) {
				callback.progressMade(message);
				dao.insert(batch);
				noRecords += batch.size();
				batch .clear();
			}
		}

		if (batch.size() > 0) {
			callback.progressMade(message);
			dao.insert(batch);
			noRecords += batch.size();
		}
		return noRecords;
	}
}
