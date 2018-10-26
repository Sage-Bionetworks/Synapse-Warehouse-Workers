package org.sagebionetworks.warehouse.workers.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.repo.model.UserProfile;
import org.sagebionetworks.repo.model.audit.ObjectRecord;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.warehouse.workers.model.UserProfileSnapshot;

public class UserProfileSnapshotWorkerTest {

	UserProfileSnapshotWorker worker;

	@Before
	public void before() {
		worker = new UserProfileSnapshotWorker(null, null, null, null);
	}

	@Test
	public void testConvertValidRecord() throws JSONObjectAdapterException {
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
		List<UserProfileSnapshot> actual = worker.convert(record);
		assertNotNull(actual);
		assertEquals(actual.size(), 1);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConvertInvalidRecord() throws JSONObjectAdapterException {
		worker.convert(null);
	}
}
