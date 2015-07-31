package org.sagebionetworks.warehouse.workers.utils;

import java.io.StringReader;
import java.io.StringWriter;

import com.thoughtworks.xstream.XStream;

/**
 * Utilities for XML marshaling that wraps Xstream
 *
 */
public class XMLUtils {

	/**
	 * Write the passed object to XML.
	 * 
	 * @param object
	 *            The object to marshal to XML.
	 * @param alais
	 *            The set the name of the root XML element used to marshal the
	 *            given object.
	 * @return
	 */
	public static <T> String toXML(T object, String alias) {
		if (object == null) {
			throw new IllegalArgumentException("Object cannot be null");
		}
		if (alias == null) {
			throw new IllegalArgumentException("Alais cannot be null");
		}
		XStream xStream = new XStream();
		xStream.alias(alias, object.getClass());
		StringWriter writer = new StringWriter();
		xStream.toXML(object, writer);
		return writer.toString();
	}

	/**
	 * Populate a new instance of the provided class from the provided XML
	 * string.
	 * 
	 * @param xml
	 *            The XML to be marshaled into a a new Object.
	 * @param clazz
	 *            The class of the root element. A new instance of this class
	 *            will be created and populated from the XML.
	 * @param alias
	 *            The set the name of the root XML element used to marshal the
	 *            given object.
	 * @return A new instance of an object of the provided class type populated
	 *         with data from the provided XML.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXML(String xml, Class<T> clazz, String alias) {
		if (xml == null) {
			throw new IllegalArgumentException("XML string cannot be null");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("Class cannot be null");
		}
		if (alias == null) {
			throw new IllegalArgumentException("Alais cannot be null");
		}
		XStream xStream = new XStream();
		xStream.alias(alias, clazz);
		StringReader reader = new StringReader(xml);
		return (T) xStream.fromXML(reader);
	}
}
