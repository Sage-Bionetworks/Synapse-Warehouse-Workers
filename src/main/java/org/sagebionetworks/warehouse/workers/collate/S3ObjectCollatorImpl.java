package org.sagebionetworks.warehouse.workers.collate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.inject.Inject;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class S3ObjectCollatorImpl implements S3ObjectCollator {
	
	AmazonS3Client s3Client;
	StreamResourceProvider collateProvider;
	
	@Inject
	public S3ObjectCollatorImpl(AmazonS3Client s3Client, StreamResourceProvider collateProvider) {
		super();
		this.s3Client = s3Client;
		this.collateProvider = collateProvider;
	}


	/*
	 * (non-Javadoc)
	 * @see org.sagebionetworks.warehouse.workers.S3ObjectCollator#collateCSVObjects(java.lang.String, java.util.List, java.lang.String, int)
	 */
	@Override
	public void replaceCSVsWithCollatedCSV(ProgressCallback<Void> progressCallback, String bucket, List<String> keysToCollate,
			String destinationKey, int sortColumnIndex) throws IOException {
		List<File> inputFiles = new LinkedList<File>();
		File destination = null;
		try{
			// download each input file to a temporary file.
			for(String key: keysToCollate){
				progressCallback.progressMade(null);
				File temp = collateProvider.createTempFile("inputCollate", ".csv.gz");
				s3Client.getObject(new GetObjectRequest(bucket, key), temp);
				inputFiles.add(temp);
			}
			// Create the destination file.
			destination = collateProvider.createTempFile("resultCollate", ".csv.gz");
			// collate the files.
			collateCSVObjects(progressCallback, bucket, inputFiles, destination, destinationKey, sortColumnIndex);
			// Collation was successful so delete the original files.
			for(String toDelete: keysToCollate){
				progressCallback.progressMade(null);
				s3Client.deleteObject(bucket, toDelete);
			}
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
			// Stream each input file to a CSVReader
			for(File file: inputFiles){
				CSVReader reader = collateProvider.createGzipReader(file);
				readers.add(reader);
			}
			// Stream the output to a CSVWriter
			writer = collateProvider.createGzipWriter(destination);
			// This is where collation actually occurs.
			collateProvider.mergeSortedStreams(progressCallback, readers, writer, sortColumnIndex);
			progressCallback.progressMade(null);
			// upload the result file.
			PutObjectRequest request = new PutObjectRequest(bucket, destinationKey, destination)
					// Both the object owner and the bucket owner get FULL_CONTROL over the object.
					.withCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
			s3Client.putObject(request);
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
