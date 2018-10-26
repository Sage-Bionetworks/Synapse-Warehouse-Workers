package org.sagebionetworks.warehouse.workers.collate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.sagebionetworks.csv.utils.ObjectCSVReader;
import org.sagebionetworks.csv.utils.ObjectCSVWriter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import org.sagebionetworks.common.util.progress.ProgressCallback;

/**
 * A simple wrapper for resource generation.
 * 
 */
public class StreamResourceProviderImpl implements StreamResourceProvider {

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.CollateProvider#createTempFile(java.lang.String, java.lang.String)
	 */
	@Override
	public File createTempFile(String prefix, String suffix) {
		try {
			return File.createTempFile(prefix, suffix);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.CollateProvider#createGzipReader(java.io.File)
	 */
	@Override
	public CSVReader createGzipReader(File file) {
		try {
			return new CSVReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.CollateProvider#createGzipWriter(java.io.File)
	 */
	@Override
	public CSVWriter createGzipWriter(File file) {
		try {
			return new CSVWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), "UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.CollateProvider#mergeSortedStreams(org.sagebionetworks.workers.util.progress.ProgressCallback, java.util.List, au.com.bytecode.opencsv.CSVWriter, int)
	 */
	@Override
	public void mergeSortedStreams(ProgressCallback<Void> progressCallback,
			List<CSVReader> readers, CSVWriter writer, int sortColumnIndex)
			throws IOException {
		// This is where collation actually occurs.
		StreamingCollateUtils.mergeSortedStreams(progressCallback, readers,
				writer, sortColumnIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider#createObjectCSVReader(java.io.File, java.lang.Class)
	 */
	@Override
	public <T> ObjectCSVReader<T> createObjectCSVReader(File file, Class<T> clazz, String[] headers) {
		try {
			return new ObjectCSVReader<T>(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"), clazz, headers);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider#createObjectCSVWriter(java.io.File, java.lang.Class)
	 */
	@Override
	public <T> ObjectCSVWriter<T> createObjectCSVWriter(File file, Class<T> clazz, String[] headers) {
		try {
			return new ObjectCSVWriter<T>(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), "UTF-8"), clazz, headers);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PrintWriter createGzipPrintWriter(File file) {
		try {
			return new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)), "UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

}
