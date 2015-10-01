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
import org.sagebionetworks.repo.model.UserGroup;
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
	 * isValidUserGroupSnapshot() tests
	 */
	@Test
	public void nullUserGroupSnapshotTest() {
		assertFalse(ObjectSnapshotUtils.isValidUserGroupSnapshot(null));
	}

	@Test
	public void validUserGroupSnapshotTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		assertTrue(ObjectSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullIdTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setId(null);
		assertFalse(ObjectSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullIsIndividualTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setIsIndividual(null);
		assertFalse(ObjectSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullCreationDateTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setCreationDate(null);
		assertFalse(ObjectSnapshotUtils.isValidUserGroupSnapshot(snapshot));
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

	/*
	 * getUserGroupSnapshot() tests
	 */
	@Test
	public void nullJsonStringGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(UserGroup.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroup ug = new UserGroup();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(ug));
		assertNull(ObjectSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroup ug = new UserGroup();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(ug));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(ObjectSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void wrongTypeGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(UserGroup.class.getSimpleName().toLowerCase());
		UserGroup snapshot = ObjectSnapshotUtils.getUserGroupSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(new UserGroup(), snapshot);
	}

	@Test
	public void getUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroup ug = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(ug));
		record.setJsonClassName(UserGroup.class.getSimpleName().toLowerCase());
		UserGroup snapshot = ObjectSnapshotUtils.getUserGroupSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(ug, snapshot);
	}

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

	/*
	 * real data tests
	 */
	@Test
	public void userProfile1Test() {
		ObjectRecord record = new ObjectRecord();
		record.setJsonClassName("userprofile");
		record.setTimestamp(1437751246000L);
		String jsonString = "{\"lastName\":\"TestUser\",\"etag\":\"7c23e592-5e60-46ed-af4e-ae675b1e7473\",\"ownerId\":\"3320560\",\"firstName\":\"RClient\"}";
		record.setJsonString(jsonString);
		UserProfileSnapshot snapshot = ObjectSnapshotUtils.getUserProfileSnapshot(record);
		assertTrue(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void userProfile2Test() {
		ObjectRecord record = new ObjectRecord();
		record.setJsonClassName("userprofile");
		record.setTimestamp(1437751246000L);
		String jsonString = "{\"summary\":\"Have worked in biotech and government since 1994.Have held positions with (at): Battelle (National Cancer Institute), Human Genome Sciences, Gene Logic, and SRA International (National Institutes of Health). Currently hold a full-time position of employment with KELLY Government Solutions, working at the National Institute of Neurological Disorders and Stroke (NINDS) at the National Institutes of Health (NIH) as the intramural resident bioinformatics subject matter expert and mentor; performing omics-based research and support and training of attendees, fellows, staff and visitors from abroad. Notable career accomplishments to date include the co-founding of Gene Logic, co-pioneering of predictive toxicogenomics, and the co-development of the NIH Stem Cell Data Management System.I have also been issued 5 patents in the field of predictive toxicogenomics, 1 patent in the field of Stroke, and have had manuscripts published in:American Journal of Human Genetics American Journal of Physiology - Regulatory, Integrative and Comparative Physiology Blood Brain Brain, Behavior, and Immunity Cancer Research JAMA Neurology Journal of Cerebral Blood Flow & Metabolism Journal of Neuroscience Journal of Neuroimmune Pharmacology Journal of Neuroimmunology Journal of Neurovirology Journal of Virology Molecular Psychiatry Multiple Sclerosis PLoS Genetics PLoS One PLoS Pathogen PNAS Retrovirology Stem Cell Research  Part-time, I provide bioinformatics-related consulting services, engage and support research collaborations and teach graduate-level Bioinformatics at the University of Maryland University College (UMUC) as an Adjunct Professor.\",\"position\":\"\",\"lastName\":\"Johnson, MS, Ph.D.\",\"etag\":\"a2e83977-cbee-4744-a94b-cabb4292a755\",\"location\":\"Washington D.C. Metro Area\",\"ownerId\":\"3330107\",\"company\":\"\",\"profilePicureFileHandleId\":\"4744513\",\"displayName\":\"Kory R. Johnson, MS, Ph.D.\",\"firstName\":\"Kory R.\",\"industry\":\"Biotechnology\",\"url\":\"\"}";
		record.setJsonString(jsonString);
		UserProfileSnapshot snapshot = ObjectSnapshotUtils.getUserProfileSnapshot(record);
		assertTrue(ObjectSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}
}
