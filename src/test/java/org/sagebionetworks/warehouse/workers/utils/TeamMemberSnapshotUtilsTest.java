package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;

public class TeamMemberSnapshotUtilsTest {

	/*
	 * isValidTeamMemberSnapshot() tests
	 */
	@Test
	public void nullTeamMemberSnapshotTest() {
		assertFalse(TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(null));
	}

	@Test
	public void validTeamMemberSnapshotTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		assertTrue(TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTimestampTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTeamIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTeamId(null);
		assertFalse(TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullMemberIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setMemberId(null);
		assertFalse(TeamMemberSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	/*
	 * getTeamMemberSnapshot() tests
	 */
	@Test
	public void nullTimstampGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		TeamMember teamMember = new TeamMember();
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		assertNull(TeamMemberSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void nullJsonStringGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		assertNull(TeamMemberSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		TeamMember teamMember = new TeamMember();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		assertNull(TeamMemberSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		TeamMember teamMember = new TeamMember();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(TeamMemberSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void wrongTypeGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		TeamMemberSnapshot snapshot = TeamMemberSnapshotUtils.getTeamMemberSnapshot(record);
		assertNull(snapshot);
	}

	@Test
	public void getTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroupHeader member = new UserGroupHeader();
		member.setOwnerId("2");
		TeamMember teamMember = new TeamMember();
		teamMember.setTeamId("1");
		teamMember.setMember(member);
		teamMember.setIsAdmin(false);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		TeamMemberSnapshot snapshot = TeamMemberSnapshotUtils.getTeamMemberSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(teamMember.getTeamId(), snapshot.getTeamId().toString());
		assertEquals(teamMember.getMember().getOwnerId(), snapshot.getMemberId().toString());
		assertEquals(teamMember.getIsAdmin(), snapshot.getIsAdmin());
	}

}
