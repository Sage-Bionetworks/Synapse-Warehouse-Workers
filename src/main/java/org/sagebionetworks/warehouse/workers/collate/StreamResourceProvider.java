package org.sagebionetworks.warehouse.workers.collate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.csv.utils.ObjectCSVWriter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import org.sagebionetworks.common.util.progress.ProgressCallback;

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
	 * Create a GZIP wrapped CSVWriter for the given file.
	 * @param file
	 * @return
	 */
	public CSVWriter createGzipWriter(File file);
	
	/**
	 * Create a ObjectCSVReader for the given file.
	 * @param file
	 * @param clazz
	 * @param header
	 * @return
	 */
	public <T> ObjectCSVReader<T> createObjectCSVReader(File file, Class<T> clazz, String[] header);
	
	/**
	 * Create a ObjectCSVWritter for the given file.
	 * @param file
	 * @param clazz
	 * @param header
	 * @return
	 */
	public <T> ObjectCSVWriter<T> createObjectCSVWriter(File file, Class<T> clazz, String[] headers);
	
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

	/**
	 * Create a GZIP wrapped PrintWriter for the given file.
	 * @param file
	 * @return
	 */
	public PrintWriter createGzipPrintWriter(File file);

	/**
	 * Write the input data into the passed writer.
	 * 
	 * @param toWrite
	 * @param writer
	 */
	public void writeText(String[] toWrite, PrintWriter writer);

}
