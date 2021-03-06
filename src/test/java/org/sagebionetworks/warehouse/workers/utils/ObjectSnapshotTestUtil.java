package org.sagebionetworks.warehouse.workers.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.repo.model.ObjectType;
import org.sagebionetworks.repo.model.Team;
import org.sagebionetworks.repo.model.TeamMember;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserGroupHeader;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.AclRecord;
import org.sagebionetworks.repo.model.audit.FileHandleSnapshot;
import org.sagebionetworks.repo.model.audit.NodeRecord;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.repo.model.file.FileHandleAssociateType;
import org.sagebionetworks.repo.model.verification.VerificationStateEnum;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.AclSnapshot;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizQuestionRecord;
import org.sagebionetworks.warehouse.workers.model.CertifiedQuizRecord;
import org.sagebionetworks.warehouse.workers.model.FileDownload;
import org.sagebionetworks.warehouse.workers.model.FileHandleCopyRecordSnapshot;
import org.sagebionetworks.warehouse.workers.model.FileHandleDownload;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamMemberSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionRecord;
import org.sagebionetworks.warehouse.workers.model.VerificationSubmissionStateRecord;

public class ObjectSnapshotTestUtil {
	private static Random random = new Random(1);

	/**
	 * 
	 * @return a unique valid NodeSnapshot
	 */
	public static NodeSnapshot createValidNodeSnapshot() {
		NodeSnapshot snapshot = new NodeSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("" + random.nextLong());
		snapshot.setNodeType(EntityType.values()[random.nextInt(5)]);
		snapshot.setCreatedByPrincipalId(random.nextLong());
		snapshot.setCreatedOn(new Date());
		snapshot.setModifiedByPrincipalId(random.nextLong());
		snapshot.setModifiedOn(new Date());
		snapshot.setIsPublic(false);
		snapshot.setIsControlled(false);
		snapshot.setIsRestricted(false);
		return snapshot;
	}

	/**
	 * 
	 * @return a unique valid TeamSnapshot
	 */
	public static TeamSnapshot createValidTeamSnapshot() {
		TeamSnapshot snapshot = new TeamSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("" + random.nextLong());
		snapshot.setCreatedBy("" + random.nextLong());
		snapshot.setCreatedOn(new Date());
		snapshot.setModifiedBy("" + random.nextLong());
		snapshot.setModifiedOn(new Date());
		snapshot.setCanPublicJoin(true);
		return snapshot;
	}

	/**
	 * 
	 * @return a unique valid TeamMemberSnapshot
	 */
	public static TeamMemberSnapshot createValidTeamMemberSnapshot() {
		TeamMemberSnapshot snapshot = new TeamMemberSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setTeamId(random.nextLong());
		snapshot.setMemberId(random.nextLong());
		return snapshot;
	}

	/**
	 * 
	 * @return a unique valid UserProfileSnapshot
	 */
	public static UserProfileSnapshot createValidUserProfileSnapshot() {
		UserProfileSnapshot snapshot = new UserProfileSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setOwnerId("" + random.nextLong());
		return snapshot;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidNodeObjectRecord() throws JSONObjectAdapterException {
		NodeRecord node = new NodeRecord();
		node.setId("" + random.nextLong());
		node.setNodeType(EntityType.values()[random.nextInt(5)]);
		node.setCreatedByPrincipalId(random.nextLong());
		node.setCreatedOn(new Date());
		node.setModifiedByPrincipalId(random.nextLong());
		node.setModifiedOn(new Date());
		node.setIsPublic(false);
		node.setIsControlled(false);
		node.setIsRestricted(false);
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(NodeRecord.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(node));
		return record;
	}

	/**
	 * 
	 * @param size
	 * @return a batch of size valid NodeSnapshots
	 * @throws JSONObjectAdapterException 
	 */
	public static List<ObjectRecord> createValidNodeSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidNodeObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @param size
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public static List<ObjectRecord> createValidTeamSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidTeamObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidTeamObjectRecord() throws JSONObjectAdapterException {
		Team team = new Team();
		team.setId("" + random.nextLong());
		team.setCreatedBy("" + random.nextLong());
		team.setCreatedOn(new Date());
		team.setModifiedBy("" + random.nextLong());
		team.setModifiedOn(new Date());
		team.setCanPublicJoin(true);
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(Team.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(team));
		return record;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidTeamMemberObjectRecord() throws JSONObjectAdapterException {
		TeamMember teamMember = new TeamMember();
		UserGroupHeader member = new UserGroupHeader();
		member.setOwnerId("" + random.nextLong());
		teamMember.setMember(member);
		teamMember.setTeamId("" + random.nextLong());
		teamMember.setIsAdmin(false);
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(TeamMember.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(teamMember));
		return record;
	}

	/**
	 * 
	 * @param size
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static List<ObjectRecord> createValidTeamMemberSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidTeamMemberObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidUserProfileObjectRecord() throws JSONObjectAdapterException {
		UserProfile profile = new UserProfile();
		profile.setOwnerId("" + random.nextLong());
		profile.setUserName("userName");
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(UserProfile.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(profile));
		return record;
	}

	/**
	 * 
	 * @param size
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static List<ObjectRecord> createValidUserProfileSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidUserProfileObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @return a unique valid AclSnapshot
	 */
	public static AclSnapshot createValidAclSnapshot() {
		AclSnapshot snapshot = new AclSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("" + random.nextLong());
		snapshot.setOwnerType(ObjectType.ENTITY);
		return snapshot;
	}

	/**
	 * 
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static ObjectRecord createValidAclObjectRecord() throws JSONObjectAdapterException {
		AclRecord acl = new AclRecord();
		acl.setCreationDate(new Date());
		acl.setId("" + random.nextLong());
		acl.setOwnerType(ObjectType.ENTITY);
		ObjectRecord record = new ObjectRecord();
		record.setTimestamp(System.currentTimeMillis());
		record.setJsonClassName(AclRecord.class.getSimpleName().toLowerCase());
		record.setJsonString(EntityFactory.createJSONStringForEntity(acl));
		return record;
	}

	/**
	 * 
	 * @param size
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public static List<ObjectRecord> createValidAclSnapshotBatch(int size) throws JSONObjectAdapterException {
		List<ObjectRecord> batch = new ArrayList<ObjectRecord>();
		for (int i = 0; i < size; i++) {
			batch.add(createValidAclObjectRecord());
		}
		return batch;
	}

	/**
	 * 
	 * @return a unique valid UserGroup snapshot
	 */
	public static UserGroup createValidUserGroupSnapshot() {
		UserGroup ug = new UserGroup();
		ug.setId("" + random.nextLong());
		ug.setIsIndividual(true);
		ug.setCreationDate(new Date());
		return ug;
	}

	/**
	 * 
	 * @return a unique valid CertifiedQuizRecord
	 */
	public static CertifiedQuizRecord createValidCertifiedQuizRecord() {
		CertifiedQuizRecord record = new CertifiedQuizRecord();
		record.setResponseId(random.nextLong());
		record.setUserId(random.nextLong());
		record.setPassed(true);
		record.setPassedOn(System.currentTimeMillis());
		return record;
	}

	/**
	 * 
	 * @return a unique valid CertifiedQuizQuestionRecord
	 */
	public static CertifiedQuizQuestionRecord createValidCertifiedQuizQuestionRecord() {
		CertifiedQuizQuestionRecord record = new CertifiedQuizQuestionRecord();
		record.setResponseId(random.nextLong());
		record.setQuestionIndex(random.nextLong());
		record.setIsCorrect(false);
		return record;
	}

	/**
	 * 
	 * @return a valid VerificationSubmissionRecord
	 */
	public static VerificationSubmissionRecord createValidVerificationSubmissionRecord() {
		VerificationSubmissionRecord record = new VerificationSubmissionRecord();
		record.setId(random.nextLong());
		record.setCreatedOn(new Date().getTime());
		record.setCreatedBy(random.nextLong());
		return record;
	}

	/**
	 * 
	 * @return a valid VerificationSubmissionStateRecord
	 */
	public static VerificationSubmissionStateRecord createValidVerificationSubmissionStateRecord() {
		VerificationSubmissionStateRecord record = new VerificationSubmissionStateRecord();
		record.setId(random.nextLong());
		record.setCreatedOn(new Date().getTime());
		record.setCreatedBy(random.nextLong());
		record.setState(VerificationStateEnum.SUBMITTED);
		return record;
	}

	/**
	 * 
	 * @return a valid FileDownload object
	 */
	public static FileDownload createValidFileDownloadRecord() {
		FileDownload record = new FileDownload();
		record.setTimestamp(random.nextLong());
		record.setUserId(random.nextLong());
		record.setFileHandleId(random.nextLong());
		record.setAssociationObjectId(random.nextLong());
		record.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		return record;
	}

	/**
	 * 
	 * @return a valid FileHandleDownload object
	 */
	public static FileHandleDownload createValidFileHandleDownloadRecord() {
		FileHandleDownload record = new FileHandleDownload();
		record.setTimestamp(random.nextLong());
		record.setUserId(random.nextLong());
		record.setDownloadedFileHandleId(random.nextLong());
		record.setRequestedFileHandleId(random.nextLong());
		record.setAssociationObjectId(random.nextLong());
		record.setAssociationObjectType(FileHandleAssociateType.TableEntity);
		return record;
	}

	/**
	 * 
	 * @return a valid FileHandleSnapshot
	 */
	public static FileHandleSnapshot createValidFileHandleSnapshot() {
		FileHandleSnapshot snapshot = new FileHandleSnapshot();
		snapshot.setId(""+random.nextLong());
		snapshot.setCreatedOn(new Date());
		snapshot.setCreatedBy(""+random.nextLong());
		snapshot.setFileName("fileName");
		snapshot.setConcreteType("concreteType");
		return snapshot;
	}

	public static FileHandleCopyRecordSnapshot createValidFileHandleCopyRecordSnapshot() {
		FileHandleCopyRecordSnapshot snapshot = new FileHandleCopyRecordSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setUserId(random.nextLong());
		snapshot.setOriginalFileHandleId(random.nextLong());
		snapshot.setAssociationObjectId(random.nextLong());
		snapshot.setAssociationObjectType(FileHandleAssociateType.FileEntity);
		snapshot.setNewFileHandleId(random.nextLong());
		return snapshot;
	}
}
