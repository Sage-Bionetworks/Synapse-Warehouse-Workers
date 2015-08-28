package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;

import org.junit.Test;
import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.ResourceAccess;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.NodeRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public class ObjectSnapshotUtilsTest {

	/*
	 * isValidNodeSnapshot() tests
	 */
	@Test
	public void nullNodeSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(null));
	}

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

	@Test
	public void invalidNodeSnapshotWithNullIsPublicTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsPublic(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIsControlledTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsControlled(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	@Test
	public void invalidNodeSnapshotWithNullIsRestrictedTest() {
		NodeSnapshot snapshot = ObjectSnapshotTestUtil.createValidNodeSnapshot();
		snapshot.setIsRestricted(null);
		assertFalse(ObjectSnapshotUtils.isValidNodeSnapshot(snapshot));
	}

	/*
	 * isValidTeamSnapshot() tests
	 */
	@Test
	public void nullTeamSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidTeamSnapshot(null));
	}

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
	public void nullTeamMemberSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(null));
	}

	@Test
	public void validTeamMemberSnapshotTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTimestampTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullTeamIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setTeamId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	@Test
	public void invalidTeamMemberSnapshotWithNullMemberIdTest() {
		TeamMemberSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamMemberSnapshot();
		snapshot.setMemberId(null);
		assertFalse(ObjectSnapshotUtils.isValidTeamMemberSnapshot(snapshot));
	}

	/*
	 * isValidUserProfileSnapshot() tests
	 */
	@Test
	public void nullUserProfileSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(null));
	}

	@Test
	public void validUserProfileSnapshotTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullTimestampTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullIdTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setOwnerId(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullUserNameTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setUserName(null);
		assertFalse(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	/*
	 * isValidAclSnapshot() tests
	 */
	@Test
	public void nullAclSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidAclSnapshot(null));
	}

	@Test
	public void validAclSnapshotTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullTimestampTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(ObjectSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullOwnerIdTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	@Test
	public void invalidAclSnapshotWithNullOwnerTypeTest() {
		AclSnapshot snapshot = ObjectSnapshotTestUtil.createValidAclSnapshot();
		snapshot.setOwnerType(null);
		assertFalse(ObjectSnapshotUtils.isValidAclSnapshot(snapshot));
	}

	/*
	 * getNodeSnapshot() tests
	 */
	@Test
	public void nullTimstampGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void nullJsonStringGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		assertNull(ObjectSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getNodeSnapshot(record));
	}

	@Test
	public void wrongTypeGetNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = ObjectSnapshotUtils.getNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new NodeSnapshot(), snapshot);
	}

	@Test
	public void getNodeSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		NodeRecord node = new NodeRecord();
		node.setId("id");
		node.setBenefactorId("benefactorId");
		node.setProjectId("projectId");
		node.setParentId("parentId");
		node.setNodeType(EntityType.file);
		node.setCreatedOn(new Date(0));
		node.setCreatedByPrincipalId(1L);
		node.setModifiedOn(new Date());
		node.setModifiedByPrincipalId(2L);
		node.setVersionNumber(3L);
		node.setFileHandleId("fileHandleId");
		node.setName("name");
		node.setIsPublic(false);
		node.setIsControlled(true);
		node.setIsRestricted(null);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		NodeSnapshot snapshot = ObjectSnapshotUtils.getNodeSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(node.getId(), snapshot.getId());
		assertEquals(node.getBenefactorId(), snapshot.getBenefactorId());
		assertEquals(node.getProjectId(), snapshot.getProjectId());
		assertEquals(node.getParentId(), snapshot.getParentId());
		assertEquals(node.getCreatedOn(), snapshot.getCreatedOn());
		assertEquals(node.getCreatedByPrincipalId(), snapshot.getCreatedByPrincipalId());
		assertEquals(node.getModifiedOn(), snapshot.getModifiedOn());
		assertEquals(node.getModifiedByPrincipalId(), snapshot.getModifiedByPrincipalId());
		assertEquals(node.getNodeType(), snapshot.getNodeType());
		assertEquals(node.getVersionNumber(), snapshot.getVersionNumber());
		assertEquals(node.getFileHandleId(), node.getFileHandleId());
		assertEquals(node.getName(), snapshot.getName());
		assertEquals(node.getIsPublic(), snapshot.getIsPublic());
		assertEquals(node.getIsControlled(), snapshot.getIsControlled());
		assertNull(snapshot.getIsRestricted());
	}

	/*
	 * getTeamSnapshot() tests
	 */
	@Test
	public void nullTimstampGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void nullJsonStringGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		assertNull(ObjectSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void wrongTypeGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		TeamSnapshot snapshot = ObjectSnapshotUtils.getTeamSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new TeamSnapshot(), snapshot);
	}

	@Test
	public void getTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		team.setId("id");
		team.setCreatedOn(new Date(0));
		team.setCreatedBy("1");
		team.setModifiedOn(new Date());
		team.setModifiedBy("2");
		team.setName("name");
		team.setCanPublicJoin(true);
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		TeamSnapshot snapshot = ObjectSnapshotUtils.getTeamSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(team.getId(), snapshot.getId());
		assertEquals(team.getCreatedOn(), snapshot.getCreatedOn());
		assertEquals(team.getCreatedBy(), snapshot.getCreatedBy());
		assertEquals(team.getModifiedOn(), snapshot.getModifiedOn());
		assertEquals(team.getModifiedBy(), snapshot.getModifiedBy());
		assertEquals(team.getName(), snapshot.getName());
		assertEquals(team.getCanPublicJoin(), snapshot.getCanPublicJoin());
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
		assertNull(ObjectSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void nullJsonStringGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		TeamMember teamMember = new TeamMember();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		assertNull(ObjectSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		TeamMember teamMember = new TeamMember();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getTeamMemberSnapshot(record));
	}

	@Test
	public void wrongTypeGetTeamMemberSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		TeamMemberSnapshot snapshot = ObjectSnapshotUtils.getTeamMemberSnapshot(record);
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
		TeamMemberSnapshot snapshot = ObjectSnapshotUtils.getTeamMemberSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(teamMember.getTeamId(), snapshot.getTeamId().toString());
		assertEquals(teamMember.getMember().getOwnerId(), snapshot.getMemberId().toString());
		assertEquals(teamMember.getIsAdmin(), snapshot.getIsAdmin());
	}

	/*
	 * getUserProfileSnapshot() tests
	 */
	@Test
	public void nullTimstampGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void nullJsonStringGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		assertNull(ObjectSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void wrongTypeGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		UserProfileSnapshot snapshot = ObjectSnapshotUtils.getUserProfileSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new UserProfileSnapshot(), snapshot);
	}

	@Test
	public void getUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		profile.setOwnerId("1");
		profile.setUserName("userName");
		profile.setFirstName("firstName");
		profile.setLastName("lastName");
		profile.setEmail("email");
		profile.setLocation("location");
		profile.setCompany("company");
		profile.setPosition("position");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		UserProfileSnapshot snapshot = ObjectSnapshotUtils.getUserProfileSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(profile.getOwnerId(), snapshot.getOwnerId());
		assertEquals(profile.getUserName(), snapshot.getUserName());
		assertEquals(profile.getFirstName(), snapshot.getFirstName());
		assertEquals(profile.getLastName(), snapshot.getLastName());
		assertEquals(profile.getEmail(), snapshot.getEmail());
		assertEquals(profile.getLocation(), snapshot.getLocation());
		assertEquals(profile.getCompany(), snapshot.getCompany());
		assertEquals(profile.getPosition(), snapshot.getPosition());
	}

	/*
	 * getAclSnapshot() tests
	 */
	@Test
	public void nullTimstampGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void nullJsonStringGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		assertNull(ObjectSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getAclSnapshot(record));
	}

	@Test
	public void wrongTypeGetAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		AclSnapshot snapshot = ObjectSnapshotUtils.getAclSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new AclSnapshot(), snapshot);
	}

	@Test
	public void getAclSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		AclRecord acl = new AclRecord();
		acl.setCreationDate(new Date());
		acl.setId("id");
		acl.setOwnerType(ObjectType.ENTITY);
		acl.setResourceAccess(new HashSet<ResourceAccess>());
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		AclSnapshot snapshot = ObjectSnapshotUtils.getAclSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(acl.getCreationDate(), snapshot.getCreationDate());
		assertEquals(acl.getId(), snapshot.getId());
		assertEquals(acl.getOwnerType(), snapshot.getOwnerType());
		assertEquals(acl.getResourceAccess(), snapshot.getResourceAccess());
	}
}
