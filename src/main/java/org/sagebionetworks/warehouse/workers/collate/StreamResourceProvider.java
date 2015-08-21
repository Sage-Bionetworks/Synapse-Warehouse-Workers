package org.sagebionetworks.warehouse.workers.collate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.s3.ObjectCSVWriter;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Abstraction for the creation of stream resources.
 *
 */
public interface StreamResourceProvider {

	/**
	 * Create a temporary file.
	 * @param string
	 * @param string2
	 * @return
	 */
	public File createTempFile(String prefix, String suffix);
	
	/**
	 * Create a GZIP wrapped CSVReader for the given file.
	 * @param file
	 * @return
	 */
	public CSVReader createGzipReader(File file);
	
	/**
	 * Create a GZIP wrapped CSVWritter for the given file.
	 * @param file
	 * @return
	 */
	public CSVWriter createGzipWriter(File file);
	
	/**
	 * Create a ObjectCSVReader for the given file.
	 * @param file
	 * @return
	 */
	public <T> ObjectCSVReader<T> createObjectCSVReader(File file, Class<T> clazz);
	
	/**
	 * Create a ObjectCSVWritter for the given file.
	 * @param file
	 * @return
	 */
	public <T> ObjectCSVWriter<T> createObjectCSVWriter(File file, Class<T> clazz);
	
	/**
	 * Collate the passed input readers into the passed writer.
	 * 
	 * @param progressCallback
	 * @param sortedInputStreams
	 * @param out
	 * @param timestampIndex
	 * @throws IOException
	 */
	public void mergeSortedStreams(
			ProgressCallback<Void> progressCallback,
			List<CSVReader> sortedInputStreams, CSVWriter out,
			int timestampIndex) throws IOException;

}
