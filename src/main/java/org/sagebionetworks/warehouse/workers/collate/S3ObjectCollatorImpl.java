package org.sagebionetworks.warehouse.workers.collate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sagebionetworks.workers.util.progress.ProgressCallback;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.google.inject.Inject;

public class S3ObjectCollatorImpl implements S3ObjectCollator {
	
	AmazonS3Client s3Client;
	CollateProvider collateProvider;
	
	@Inject
	public S3ObjectCollatorImpl(AmazonS3Client s3Client, CollateProvider collateProvider) {
		super();
		this.s3Client = s3Client;
		this.collateProvider = collateProvider;
	}


	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.S3ObjectCollator#collateCSVObjects(java.lang.String, java.util.List, java.lang.String, int)
	 */
	@Override
	public void collateCSVObjects(ProgressCallback<Void> progressCallback, String bucket, List<String> keysToCollate,
			String destinationKey, int sortColumnIndex) throws IOException {
		// The first step is to download each file
		List<File> inputFiles = new LinkedList<File>();
		File destination = null;
		try{
			for(String key: keysToCollate){
				File temp = collateProvider.createTempFile("inputCollate", ".csv.gz");
				s3Client.getObject(new GetObjectRequest(bucket, key), temp);
				inputFiles.add(temp);
			}
			// Create the destination file.
			destination = collateProvider.createTempFile("resultCollate", ".csv.gz");
			// collate the files.
			collateCSVObjects(progressCallback, bucket, inputFiles, destination, destinationKey, sortColumnIndex);
		}finally{
			// unconditionally delete the temp files.
			for(File file: inputFiles){
				try {
					file.delete();
				} catch (Exception e) {}
			}
			if(destination != null){
				destination.delete();
			}
		}
	}


	/**
	 * This does the actual collate and upload.
	 * @param progressCallback
	 * @param bucket
	 * @param inputFiles
	 * @param destination
	 * @param destinationKey
	 * @param sortColumnIndex
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void collateCSVObjects(ProgressCallback<Void> progressCallback, String bucket, List<File> inputFiles,
			File destination, String destinationKey, int sortColumnIndex) throws FileNotFoundException, IOException {
		// Create a stream for each file
		List<CSVReader> readers = new LinkedList<CSVReader>();
		CSVWriter writer = null;
		try{
			for(File file: inputFiles){
				CSVReader reader = collateProvider.createGzipReader(file);
				readers.add(reader);
			}
			writer = collateProvider.createGzipWriter(destination);
			// This is where collation actually occurs.
			collateProvider.mergeSortedStreams(progressCallback, readers, writer, sortColumnIndex);
			// upload the result file.
			s3Client.putObject(bucket, destinationKey, destination);
		}finally{
			// unconditionally close all readers and writers.
			for(CSVReader reader: readers){
				IOUtils.closeQuietly(reader);
			}
			if(writer != null){
				IOUtils.closeQuietly(writer);
			}
		}		
	}

}
