package org.sagebionetworks.warehouse.workers.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.repo.model.Node;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public class PrincipalSnapshotUtilsTest {

	/*
	 * isValidTeamSnapshot() tests
	 */
	@Test
	public void nullTeamSnapshotTest() {
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(null));
	}

	@Test
	public void validTeamSnapshotTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		assertTrue(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullTimestampTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullIdTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setId(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedOn(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullCreatedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setCreatedBy(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedOnTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedOn(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	@Test
	public void invalidTeamSnapshotWithNullModifiedByTest() {
		TeamSnapshot snapshot = ObjectSnapshotTestUtil.createValidTeamSnapshot();
		snapshot.setModifiedBy(null);
		assertFalse(PrincipalSnapshotUtils.isValidTeamSnapshot(snapshot));
	}

	/*
	 * isValidUserProfileSnapshot() tests
	 */
	@Test
	public void nullUserProfileSnapshotTest() {
		assertFalse(PrincipalSnapshotUtils.isValidUserProfileSnapshot(null));
	}

	@Test
	public void validUserProfileSnapshotTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		assertTrue(PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullTimestampTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setTimestamp(null);
		assertFalse(PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void invalidUserProfileSnapshotWithNullIdTest() {
		UserProfileSnapshot snapshot = ObjectSnapshotTestUtil.createValidUserProfileSnapshot();
		snapshot.setOwnerId(null);
		assertFalse(PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	/*
	 * isValidUserGroupSnapshot() tests
	 */
	@Test
	public void nullUserGroupSnapshotTest() {
		assertFalse(PrincipalSnapshotUtils.isValidUserGroupSnapshot(null));
	}

	@Test
	public void validUserGroupSnapshotTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		assertTrue(PrincipalSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullIdTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setId(null);
		assertFalse(PrincipalSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullIsIndividualTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setIsIndividual(null);
		assertFalse(PrincipalSnapshotUtils.isValidUserGroupSnapshot(snapshot));
	}

	@Test
	public void invalidUserGroupSnapshotWithNullCreationDateTest() {
		UserGroup snapshot = ObjectSnapshotTestUtil.createValidUserGroupSnapshot();
		snapshot.setCreationDate(null);
		assertFalse(PrincipalSnapshotUtils.isValidUserGroupSnapshot(snapshot));
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
		assertNull(PrincipalSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void nullJsonStringGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		assertNull(PrincipalSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Team team = new Team();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getTeamSnapshot(record));
	}

	@Test
	public void wrongTypeGetTeamSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		TeamSnapshot snapshot = PrincipalSnapshotUtils.getTeamSnapshot(record);
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
		TeamSnapshot snapshot = PrincipalSnapshotUtils.getTeamSnapshot(record);
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
	 * getUserProfileSnapshot() tests
	 */
	@Test
	public void nullTimstampGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void nullJsonStringGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		assertNull(PrincipalSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getUserProfileSnapshot(record));
	}

	@Test
	public void wrongTypeGetUserProfileSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
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
		profile.setEmails(Arrays.asList("email"));
		profile.setLocation("location");
		profile.setCompany("company");
		profile.setPosition("position");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(profile.getOwnerId(), snapshot.getOwnerId());
		assertEquals(profile.getUserName(), snapshot.getUserName());
		assertEquals(profile.getFirstName(), snapshot.getFirstName());
		assertEquals(profile.getLastName(), snapshot.getLastName());
		assertEquals(profile.getEmails().get(0), snapshot.getEmail());
		assertEquals(profile.getLocation(), snapshot.getLocation());
		assertEquals(profile.getCompany(), snapshot.getCompany());
		assertEquals(profile.getPosition(), snapshot.getPosition());
	}

	@Test
	public void getUserProfileSnapshotTestWithNullEmails() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserProfile profile = new UserProfile();
		profile.setOwnerId("1");
		profile.setUserName("userName");
		profile.setFirstName("firstName");
		profile.setLastName("lastName");
		profile.setEmails(null);
		profile.setLocation("location");
		profile.setCompany("company");
		profile.setPosition("position");
		Long timestamp = System.currentTimeMillis();
		record.setTimestamp(timestamp);
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(timestamp, snapshot.getTimestamp());
		assertEquals(profile.getOwnerId(), snapshot.getOwnerId());
		assertEquals(profile.getUserName(), snapshot.getUserName());
		assertEquals(profile.getFirstName(), snapshot.getFirstName());
		assertEquals(profile.getLastName(), snapshot.getLastName());
		assertEquals(null, snapshot.getEmail());
		assertEquals(profile.getLocation(), snapshot.getLocation());
		assertEquals(profile.getCompany(), snapshot.getCompany());
		assertEquals(profile.getPosition(), snapshot.getPosition());
	}

	/*
	 * getUserGroupSnapshot() tests
	 */
	@Test
	public void nullJsonStringGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(UserGroup.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void nullJsonClassNameGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroup ug = new UserGroup();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(ug));
		assertNull(PrincipalSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void wrongTypeNameGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		UserGroup ug = new UserGroup();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(ug));
		record.setJsonClassName(Node.class.getSimpleName().toLowerCase());
		assertNull(PrincipalSnapshotUtils.getUserGroupSnapshot(record));
	}

	@Test
	public void wrongTypeGetUserGroupSnapshotTest() throws JSONObjectAdapterException {
		ObjectRecord record = new ObjectRecord();
		Node node = new Node();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		record.setJsonClassName(UserGroup.class.getSimpleName().toLowerCase());
		UserGroup snapshot = PrincipalSnapshotUtils.getUserGroupSnapshot(record);
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
		UserGroup snapshot = PrincipalSnapshotUtils.getUserGroupSnapshot(record);
		assertNotNull(snapshot);
		assertEquals(ug, snapshot);
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
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
		assertTrue(PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

	@Test
	public void userProfile2Test() {
		ObjectRecord record = new ObjectRecord();
		record.setJsonClassName("userprofile");
		record.setTimestamp(1437751246000L);
		String jsonString = "{\"summary\":\"Have worked in biotech and government since 1994.Have held positions with (at): Battelle (National Cancer Institute), Human Genome Sciences, Gene Logic, and SRA International (National Institutes of Health). Currently hold a full-time position of employment with KELLY Government Solutions, working at the National Institute of Neurological Disorders and Stroke (NINDS) at the National Institutes of Health (NIH) as the intramural resident bioinformatics subject matter expert and mentor; performing omics-based research and support and training of attendees, fellows, staff and visitors from abroad. Notable career accomplishments to date include the co-founding of Gene Logic, co-pioneering of predictive toxicogenomics, and the co-development of the NIH Stem Cell Data Management System.I have also been issued 5 patents in the field of predictive toxicogenomics, 1 patent in the field of Stroke, and have had manuscripts published in:American Journal of Human Genetics American Journal of Physiology - Regulatory, Integrative and Comparative Physiology Blood Brain Brain, Behavior, and Immunity Cancer Research JAMA Neurology Journal of Cerebral Blood Flow & Metabolism Journal of Neuroscience Journal of Neuroimmune Pharmacology Journal of Neuroimmunology Journal of Neurovirology Journal of Virology Molecular Psychiatry Multiple Sclerosis PLoS Genetics PLoS One PLoS Pathogen PNAS Retrovirology Stem Cell Research  Part-time, I provide bioinformatics-related consulting services, engage and support research collaborations and teach graduate-level Bioinformatics at the University of Maryland University College (UMUC) as an Adjunct Professor.\",\"position\":\"\",\"lastName\":\"Johnson, MS, Ph.D.\",\"etag\":\"a2e83977-cbee-4744-a94b-cabb4292a755\",\"location\":\"Washington D.C. Metro Area\",\"ownerId\":\"3330107\",\"company\":\"\",\"profilePicureFileHandleId\":\"4744513\",\"displayName\":\"Kory R. Johnson, MS, Ph.D.\",\"firstName\":\"Kory R.\",\"industry\":\"Biotechnology\",\"url\":\"\"}";
		record.setJsonString(jsonString);
		UserProfileSnapshot snapshot = PrincipalSnapshotUtils.getUserProfileSnapshot(record);
		assertTrue(PrincipalSnapshotUtils.isValidUserProfileSnapshot(snapshot));
	}

}
