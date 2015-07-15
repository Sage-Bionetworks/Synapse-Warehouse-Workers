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
	
	/**
	 * Parse the JSON of an S3 object event into an S3ObjectSummary.
	 * 
	 * @param eventJson
	 * @return
	 */
	public static List<S3ObjectSummary> parseEventJson(String eventJson){
		JSONObject event = new JSONObject(eventJson);
		List<S3ObjectSummary> list = new LinkedList<S3ObjectSummary>();
		if(event.has("Message")){
			// the message is JSON.
			JSONObject message = new JSONObject(event.get("Message").toString());
			if(message.has("Records")){
				JSONArray records= message.getJSONArray("Records");
				for(int i=0; i<records.length(); i++){
					S3ObjectSummary summary = new S3ObjectSummary();
					list.add(summary);
					JSONObject record = records.getJSONObject(i);
					if(record.has("eventTime")){
						DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(record.getString("eventTime"));
						summary.setLastModified(dt.toDate());
					}
					if(record.has("s3")){
						JSONObject s3 = record.getJSONObject("s3");
						if(s3.has("bucket")){
							JSONObject bucket = s3.getJSONObject("bucket");
							summary.setBucketName(bucket.getString("name"));
						}
						if(s3.has("object")){
							JSONObject object = s3.getJSONObject("object");
							summary.setETag(object.getString("eTag"));
							summary.setKey(object.getString("key"));
							summary.setSize(object.getLong("size"));
						}
					}
				}
			}
		}
		return list;
	}
}
