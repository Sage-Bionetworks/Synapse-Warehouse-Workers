package org.sagebionetworks.warehouse.workers.collate;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;

/**
 * Unit test for the S3ObjectCollatorImpl
 *
 */
public class S3ObjectCollatorImplTest {
	
	ProgressCallback<Void> mockProgressCallback;
	AmazonS3Client mockS3Client;
	CollateProvider mockCollateProvider;
	S3ObjectCollatorImpl collator;
	List<File> mockFiles;
	List<CSVReader> mockReaders;
	CSVWriter mockWriter;
	
	String bucket;
	List<String> keysToCollate;
	String destinationKey;
	int sortColumnIndex = 0;
	
	@Before
	public void before(){
		mockS3Client = Mockito.mock(AmazonS3Client.class);
		mockCollateProvider = Mockito.mock(CollateProvider.class);
		mockProgressCallback = Mockito.mock(ProgressCallback.class);
		bucket = "SomeBucket";
		keysToCollate = Arrays.asList("a","b","c");
		destinationKey = "destination";
		mockFiles = new LinkedList<File>();
		mockReaders = new LinkedList<CSVReader>();
		// mock file creation.
		doAnswer(new Answer<File>(){
			@Override
			public File answer(InvocationOnMock invocation) throws Throwable {
				File mockFile = Mockito.mock(File.class);
				mockFiles.add(mockFile);
				return mockFile;
			}}).when(mockCollateProvider).createTempFile(anyString(), anyString());
		// mock reader creation
		doAnswer(new Answer<CSVReader>(){
			@Override
			public CSVReader answer(InvocationOnMock invocation) throws Throwable {
				CSVReader mockReader = Mockito.mock(CSVReader.class);
				mockReaders.add(mockReader);
				return mockReader;
			}}).when(mockCollateProvider).createGzipReader(any(File.class));
		// mock writers
		mockWriter = Mockito.mock(CSVWriter.class);
		when(mockCollateProvider.createGzipWriter(any(File.class))).thenReturn(mockWriter);
		
		collator = new S3ObjectCollatorImpl(mockS3Client, mockCollateProvider);
	}

	@Test
	public void testCollateCSVObjects() throws IOException{
		// call under test.
		collator.collateCSVObjects(mockProgressCallback, bucket, keysToCollate, destinationKey, sortColumnIndex);
		assertEquals("Four temp files should have been created.",4, mockFiles.size());
		// The three input files should be down loaded.
		verify(mockS3Client, times(3)).getObject(any(GetObjectRequest.class), any(File.class));
		assertEquals("Three readers should have been created",3, mockReaders.size());
		// collate should be called.
		verify(mockCollateProvider).mergeSortedStreams(mockProgressCallback, mockReaders, mockWriter, sortColumnIndex);
		// the results should be pushed to S3 with the last file
		verify(mockS3Client).putObject(bucket, destinationKey, mockFiles.get(mockFiles.size()-1));
		// each file that is created should get deleted
		for(File mockFile: mockFiles){
			verify(mockFile).delete();
		}
		// all of the readers should be closed
		for(CSVReader mockReader: mockReaders){
			verify(mockReader).close();
		}
		// the writer should be closed
		verify(mockWriter).close();		
	}
}
