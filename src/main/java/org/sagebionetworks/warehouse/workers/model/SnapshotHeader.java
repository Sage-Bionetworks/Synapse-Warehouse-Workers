package org.sagebionetworks.warehouse.workers.model;

public class SnapshotHeader {
	public final static String[] ACCESS_RECORD_HEADERS = new String[]{"returnObjectId", "elapseMS","timestamp","via","host","threadId","userAgent","queryString","sessionId","xForwardedFor","requestURL","userId","origin", "date","method","vmId","instance","stack","success", "responseStatus"};
	public final static String[] OBJECT_RECORD_HEADERS = new String[] { "timestamp", "jsonClassName", "jsonString" };
}
