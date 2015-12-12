package org.sagebionetworks.warehouse.workers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.sagebionetworks.warehouse.workers.collate.StreamingCollateUtils;
import org.sagebionetworks.common.util.progress.ProgressCallback;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class StreamingCollateUtilsTest {
	
	ProgressCallback<Void> mockCallback;
	
	@SuppressWarnings("unchecked")
	@Before
	public void before(){
		mockCallback = Mockito.mock(ProgressCallback.class);
	}
	
	@Test
	public void testMergeSortedStreamsHappy() throws IOException{
		int timestampColumnIndex  = 1;
		
		CSVReader one = createReader(new String[][]{
				new String[]{ "a", "1"},
				new String[]{ "c", "3"},
				new String[]{ "f", "6"},
		});
		
		CSVReader two= createReader(new String[][]{
				new String[]{ "b", "2"},
				new String[]{ "z", "26"},
		});
		
		CSVReader three = createReader(new String[][]{
				new String[]{ "d", "4"},
				new String[]{ "e", "5"},
				new String[]{ "g", "7"},
				new String[]{ "h", "8"},
		});
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		StreamingCollateUtils.mergeSortedStreams(mockCallback, Arrays.asList(three, two, one), writer, timestampColumnIndex);
	
		List<String[]> expectedOut = Arrays.asList(
				new String[]{ "a", "1"},
				new String[]{ "b", "2"},
				new String[]{ "c", "3"},
				new String[]{ "d", "4"},
				new String[]{ "e", "5"},
				new String[]{ "f", "6"},
				new String[]{ "g", "7"},
				new String[]{ "h", "8"},
				new String[]{ "z", "26"}
				);
		validateExpected(expectedOut, stringWriter.toString());
		// progress should be made for each row written.
		verify(mockCallback, times(expectedOut.size())).progressMade(null);
	}
	
	@Test
	public void testMergeSortedStreamsEmpty() throws IOException{
		int timestampColumnIndex  = 1;
		
		CSVReader one = createReader(new String[][]{
		});
		
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		StreamingCollateUtils.mergeSortedStreams(mockCallback, Arrays.asList(one), writer, timestampColumnIndex);
	
		List<String[]> expectedOut = Arrays.asList(
				);
		validateExpected(expectedOut, stringWriter.toString());
		// progress should be made for each row written.
		verify(mockCallback, times(expectedOut.size())).progressMade(null);
	}
	
	@Test
	public void testMergeSortedStreamsDuplicates() throws IOException{
		int timestampColumnIndex  = 1;
		
		CSVReader one = createReader(new String[][]{
				new String[]{ "a", "1"},
				new String[]{ "a", "1"},
		});
		
		CSVReader two= createReader(new String[][]{
				new String[]{ "a", "1"},
				new String[]{ "a", "1"},
		});
		
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		StreamingCollateUtils.mergeSortedStreams(mockCallback, Arrays.asList(one, two), writer, timestampColumnIndex);
	
		List<String[]> expectedOut = Arrays.asList(
				new String[]{ "a", "1"},
				new String[]{ "a", "1"},
				new String[]{ "a", "1"},
				new String[]{ "a", "1"}
				);
		validateExpected(expectedOut, stringWriter.toString());
		// progress should be made for each row written.
		verify(mockCallback, times(expectedOut.size())).progressMade(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testMergeSortedStreamsBadData() throws IOException{
		// the first column is not a timestamp
		int timestampColumnIndex  = 0;
		CSVReader one = createReader(new String[][]{
				new String[]{ "a", "1"},
				new String[]{ "c", "3"},
				new String[]{ "f", "6"},
		});
		
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		StreamingCollateUtils.mergeSortedStreams(mockCallback, Arrays.asList(one), writer, timestampColumnIndex);
	}
	
	/**
	 * Will first write the given input objects to a CSV then provide a new reader to read resulting CSV.
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static CSVReader createReader(String[][] input) throws IOException{
		// first write the data to a CSV
		StringWriter writer = new StringWriter();
		CSVWriter csvOut = new CSVWriter(writer);
		for(int row=0; row<input.length; row++){
			String[] rowData = input[row];
			csvOut.writeNext(rowData);
		}
		csvOut.close();
		return new CSVReader(new StringReader(writer.toString()));
	}
	
	/**
	 * Validate the the passed CSV data is the same as expected.
	 * @param expected
	 * @param csvData
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void validateExpected(List<String[]> expected, String csvData) throws IOException{
		StringReader stringReader = new StringReader(csvData);
		CSVReader reader = new CSVReader(stringReader);
		List<String[]> results = reader.readAll();
		assertEquals(expected.size(), results.size());
		for(int i=0; i<expected.size(); i++){
			String expectedRow = Arrays.toString(expected.get(i));
			String actualRow = Arrays.toString(results.get(i));
			assertEquals(expectedRow, actualRow);
		}
	}

}
