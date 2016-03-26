package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ObjectSnapshotUtilsTest {

	/*
	 * convertSynapseIdToLong() tests
	 */
	@Test (expected=IllegalArgumentException.class)
	public void convertNullSynapseId() {
		ObjectSnapshotUtils.convertSynapseIdToLong(null);
	}

	@Test (expected=NumberFormatException.class)
	public void convertNANSynapseId() {
		ObjectSnapshotUtils.convertSynapseIdToLong("abc");
	}

	@Test
	public void idWithSynPrefixTest() {
		assertEquals(123456L, ObjectSnapshotUtils.convertSynapseIdToLong("Syn123456"));
	}

	@Test
	public void idWithoutSynPrefixTest() {
		assertEquals(123456L, ObjectSnapshotUtils.convertSynapseIdToLong("123456"));
	}

	@Test
	public void idWithWhiteSpaceTest() {
		assertEquals(123456L, ObjectSnapshotUtils.convertSynapseIdToLong("123456     "));
	}
}
