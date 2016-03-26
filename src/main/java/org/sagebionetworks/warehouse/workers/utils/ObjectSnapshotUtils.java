package org.sagebionetworks.warehouse.workers.utils;


public class ObjectSnapshotUtils {

	/**
	 * 
	 * @param synId
	 * @return
	 */
	public static long convertSynapseIdToLong(String synId) {
		if (synId == null)
			throw new IllegalArgumentException();
		synId = synId.trim().toLowerCase();
		if (synId.startsWith("syn"))
			synId = synId.substring(3);
		return Long.parseLong(synId);
	}
}
