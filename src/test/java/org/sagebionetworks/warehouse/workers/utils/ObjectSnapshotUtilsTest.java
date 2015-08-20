package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;

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

	/*
	 * isValidTeamSnapshot() tests
	 */
	@Test
	public void validTeamSnapshotTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullTimestampTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullIdTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedBy(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedBy(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCanPublicJoinTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCanPublicJoin(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	/*
	 * isValidTeamMemberSnapshot() tests
	 */
	@Test
	public void validTeamMemberSnapshotTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTimestamp() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTeamId() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTeamId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullMemberId() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setMemberId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}
}
