package org.sagebionetworks.warehouse.workers.utils;

import org.sagebionetworks.warehouse.workers.model.NodeSnapshot;
import org.sagebionetworks.warehouse.workers.model.TeamSnapshot;

public class ObjectSnapshotUtils {

	/**
	 * 
	 * @param snapshot - the snapshot of a Synapse Node object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidNodeSnapshot(NodeSnapshot snapshot) {
		if (snapshot.getTimestamp() 			== null) return false;
		if (snapshot.getId() 					== null) return false;
		if (snapshot.getNodeType() 				== null) return false;
		if (snapshot.getCreatedOn() 			== null) return false;
		if (snapshot.getCreatedByPrincipalId() 	== null) return false;
		if (snapshot.getModifiedOn()	 		== null) return false;
		if (snapshot.getModifiedByPrincipalId() == null) return false;
		return true;
	}

	/**
	 * 
	 * @param snapshot - the snapshot of a Synapse Team object
	 * @return true is the snapshot contains not null values for required fields
	 *         false otherwise.
	 */
	public static boolean isValidTeamSnapshot(TeamSnapshot snapshot) {
		if (snapshot.getTimestamp() 		== null) return false;
		if (snapshot.getId() 				== null) return false;
		if (snapshot.getCreatedOn() 		== null) return false;
		if (snapshot.getCreatedBy() 		== null) return false;
		if (snapshot.getModifiedOn()		== null) return false;
		if (snapshot.getModifiedBy() 		== null) return false;
		if (snapshot.getCanPublicJoin() 	== null) return false;
		return true;
	}
}
