package org.sagebionetworks.warehouse.workers.utils;

import java.util.Date;

import org.sagebionetworks.repo.model.EntityType;
import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;

public class ObjectSnapshotTestUtil {

	/**
	 * 
	 * @return a unique valid NodeSnapshot
	 */
	public static NodeSnapshot createValidNodeSnapshot() {
		NodeSnapshot snapshot = new NodeSnapshot();
		snapshot.setTimestamp(System.currentTimeMillis());
		snapshot.setId("1234567");
		snapshot.setNodeType(EntityType.file);
		snapshot.setCreatedByPrincipalId(123L);
		snapshot.setCreatedOn(new Date());
		snapshot.setModifiedByPrincipalId(123L);
		snapshot.setModifiedOn(new Date());
		return snapshot;
	}
}
