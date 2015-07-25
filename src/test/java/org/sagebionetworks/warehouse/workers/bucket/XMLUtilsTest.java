package org.sagebionetworks.warehouse.workers.bucket;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class XMLUtilsTest {
	
	String alias;
	TestDto dto;
	
	@Before
	public void before(){
		alias = "anAlias";
		dto = new TestDto();
		dto.setAnInt(123);
		dto.setaString("Some string");
	}
	
	@Test
	public void testRoundTrip(){		
		String xml = XMLUtils.toXML(dto, alias);
		System.out.println(xml);
		TestDto clone = XMLUtils.fromXML(xml, TestDto.class, alias);
		assertEquals(dto, clone);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testToXmlDtoNull(){
		XMLUtils.toXML(null, alias);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testToXmlAlaisNull(){
		XMLUtils.toXML(dto, null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testFromXmlXmlNull(){
		XMLUtils.fromXML(null, TestDto.class, alias);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testFromXmlDtoNull(){
		String xml = XMLUtils.toXML(dto, alias);
		XMLUtils.fromXML(xml, null, alias);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testFromXmlAliasNull(){
		String xml = XMLUtils.toXML(dto, alias);
		XMLUtils.fromXML(xml, TestDto.class, null);
	}
}
