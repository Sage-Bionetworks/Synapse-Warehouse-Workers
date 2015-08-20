package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public class ObjectSnapshotUtilsTest {

	/*
	 * isValidNodeSnapshot() tests
	 */

	@Test
	public void validNodeSnapshotTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullTimestampTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeIdTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullNodeTypeTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setNodeType(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullCreatedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setCreatedByPrincipalId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedOnTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullModifiedByTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setModifiedByPrincipalId(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}
}
