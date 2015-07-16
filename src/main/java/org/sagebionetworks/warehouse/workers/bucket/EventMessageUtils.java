package org.sagebionetworks.warehouse.workers.bucket;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Utilities for parsing S3 bucket events.
 *
 */
public class EventMessageUtils {
	
	/*
	 * JSON keys
	 */
	private static final String KEY_SIZE = "size";
	private static final String KEY_KEY = "key";
	private static final String KEY_E_TAG = "eTag";
	private static final String KEY_OBJECT = "object";
	private static final String KEY_NAME = "name";
	private static final String KEY_BUCKET = "bucket";
	private static final String KEY_S3 = "s3";
	private static final String KEY_EVENT_TIME = "eventTime";
	private static final String KEY_RECORDS = "Records";
	private static final String KEY_MESSAGE = "Message";

	/**
	 * Parse the JSON of an S3 object event into an S3ObjectSummary.
	 * 
	 * @param eventJson
	 * @return
	 */
	public static List<S3ObjectSummary> parseEventJson(String eventJson){
		JSONObject event = new JSONObject(eventJson);
		List<S3ObjectSummary> list = new LinkedList<S3ObjectSummary>();
		if(event.has(KEY_MESSAGE)){
			// the message is JSON.
			JSONObject message = new JSONObject(event.get(KEY_MESSAGE).toString());
			if(message.has(KEY_RECORDS)){
				JSONArray records= message.getJSONArray(KEY_RECORDS);
				for(int i=0; i<records.length(); i++){
					S3ObjectSummary summary = new S3ObjectSummary();
					list.add(summary);
					JSONObject record = records.getJSONObject(i);
					if(record.has(KEY_EVENT_TIME)){
						DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(record.getString(KEY_EVENT_TIME));
						summary.setLastModified(dt.toDate());
					}
					if(record.has(KEY_S3)){
						JSONObject s3 = record.getJSONObject(KEY_S3);
						if(s3.has(KEY_BUCKET)){
							JSONObject bucket = s3.getJSONObject(KEY_BUCKET);
							summary.setBucketName(bucket.getString(KEY_NAME));
						}
						if(s3.has(KEY_OBJECT)){
							JSONObject object = s3.getJSONObject(KEY_OBJECT);
							summary.setETag(object.getString(KEY_E_TAG));
							summary.setKey(object.getString(KEY_KEY));
							summary.setSize(object.getLong(KEY_SIZE));
						}
					}
				}
			}
		}
		return list;
	}
}
