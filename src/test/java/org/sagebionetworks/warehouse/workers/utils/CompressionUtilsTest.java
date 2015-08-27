package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class CompressionUtilsTest {

	@Test
	public void testRoundTrip() {
		String toCompress = "<set\\>";
		byte[] compressed = CompressionUtils.compressStringUTF8(toCompress);
		String unzipped = CompressionUtils.decompressUTF8(compressed);
		assertEquals(toCompress, unzipped);
	}

	@Test
	public void longStringTest() {
		String longString = RandomStringUtils.random(1500);
		byte[] compressed = CompressionUtils.compressStringUTF8(longString);
		String unzipped = CompressionUtils.decompressUTF8(compressed);
		assertEquals(longString, unzipped);
	}
}
