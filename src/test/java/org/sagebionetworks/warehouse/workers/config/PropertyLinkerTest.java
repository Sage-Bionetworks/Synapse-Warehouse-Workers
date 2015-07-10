package org.sagebionetworks.warehouse.workers.config;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertyLinkerTest {
	
	@Test
	public void testLinkAndReplace(){
		Properties input = new Properties();
		input.put("prop.zero", "blank");
		input.put("prop.one", "foo");
		input.put("prop.two", "${prop.one}-${prop.three}");
		input.put("prop.three", "pre-${prop.zero}-post");
		Properties output = PropertyLinker.linkAndReplace(input);
		assertNotNull(output);
		assertEquals(input.size(), output.size());
		assertEquals("blank", output.getProperty("prop.zero"));
		assertEquals("foo", output.getProperty("prop.one"));
		assertEquals("foo-pre-blank-post", output.getProperty("prop.two"));
		assertEquals("pre-blank-post", output.getProperty("prop.three"));
	}
	
	@Test
	public void testLinkAndReplaceMissingRef(){
		Properties input = new Properties();
		input.put("prop.two", "${prop.one}-${prop.three}");
		input.put("prop.three", "blank");
		Properties output;
		try {
			output = PropertyLinker.linkAndReplace(input);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("key: 'prop.one' was not found"));
		}
	}
	
	@Test
	public void testLinkAndReplaceCycle(){
		Properties input = new Properties();
		input.put("prop.one", "${prop.two}");
		input.put("prop.two", "${prop.one}");
		Properties output;
		try {
			output = PropertyLinker.linkAndReplace(input);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Cycles are not allowed"));
		}
	}

}
