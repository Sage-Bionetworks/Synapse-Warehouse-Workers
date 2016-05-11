package org.sagebionetworks.warehouse.workers.collate;

import java.io.IOException;
import java.util.List;

import org.sagebionetworks.common.util.progress.ProgressCallback;
import org.sagebionetworks.csv.utils.CSVReader;
import org.sagebionetworks.csv.utils.CSVWriter;

/**
 * A Utility for collating CSV files as streams.
 * 
 */
public class StreamingCollateUtils {

	/**
	 * Given sorted input CSV streams, merge all CSV data to the given output
	 * stream. As long as each input stream is sorted, the resulting output
	 * stream will be sorted. This utility uses a variation of the 'merge'
	 * portion of the merge-sort algorithm.
	 * 
	 * Note: This utility is memory efficient and will never keep more than one
	 * row from each input stream in memory at a time.
	 * 
	 * @param progressCallback
	 *            progressMade() will be called for ever row written to the
	 *            output writer.
	 * @param sortedInputStreams
	 *            Each CSVReader must be reading from a CSV sorted on the given
	 *            timestampIndex.
	 * @param out
	 *            All data from each input stream will be streamed to the output
	 *            writer. If the each input stream is sorted then, the output
	 *            stream will be sorted.
	 * @param timestampIndex
	 *            The index of the timestamp column that each input CSV reader
	 *            is sorted on. The output will also be sorted on this column.
	 *            All data in the
	 * @throws IOException
	 */
	public static void mergeSortedStreams(
			ProgressCallback<Void> progressCallback,
			List<CSVReader> sortedInputStreams, CSVWriter out,
			int timestampIndex) throws IOException {
		// Create a stack for each reader
		CSVReaderStack[] stacks = new CSVReaderStack[sortedInputStreams.size()];
		for (int i = 0; i < sortedInputStreams.size(); i++) {
			stacks[i] = new CSVReaderStack(sortedInputStreams.get(i));
		}
		// Keep reading as long as there is data.
		int minimumIndex = -1;
		while ((minimumIndex = findMiniumnIndex(stacks, timestampIndex)) >= 0) {
			progressCallback.progressMade(null);
			String[] nextOut = stacks[minimumIndex].pop();
			out.writeNext(nextOut);
		}
		out.close();
		// done
	}

	/**
	 * Given an array of CSVReaderStack find the index of the stack with the
	 * smallest time stamp.
	 * 
	 * @param stacks
	 * @param timestampIndex
	 *            The index of the time stamp column in the CSV.
	 * @return
	 */
	private static int findMiniumnIndex(CSVReaderStack[] stacks,
			int timestampIndex) {
		long minimumTimestamp = Long.MAX_VALUE;
		int minIndex = -1;
		for (int i = 0; i < stacks.length; i++) {
			// only read from non-empty stacks.
			if (!stacks[i].isEmpty()) {
				String value = null;
				try {
					value = stacks[i].peak()[timestampIndex];
					long thisTimestamp = Long.parseLong(value);
					if (thisTimestamp < minimumTimestamp) {
						minimumTimestamp = thisTimestamp;
						minIndex = i;
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(
							"Unable to read the timestamp column from the input CSV.  Given timestampIndex = "
									+ timestampIndex
									+ ". Value at this index was: " + value);
				}
			}
		}
		return minIndex;
	}

	/**
	 * Wrap a CSVReader in a stack-like interface.
	 * 
	 */
	private static class CSVReaderStack {

		CSVReader wrapped;
		String[] lastRow;

		public CSVReaderStack(CSVReader wrapped) throws IOException {
			super();
			this.wrapped = wrapped;
			this.lastRow = wrapped.readNext();
		}

		/**
		 * Look at the row at the top of this stack.
		 * 
		 * @return
		 */
		public String[] peak() {
			return this.lastRow;
		}

		/**
		 * Remove the row at the top of this stack
		 * 
		 * @throws IOException
		 */
		public String[] pop() throws IOException {
			String[] last = this.lastRow;
			this.lastRow = wrapped.readNext();
			return last;
		}

		/**
		 * Is this stack empty?
		 * 
		 * @return
		 */
		public boolean isEmpty() {
			return this.lastRow == null;
		}
	}
}
