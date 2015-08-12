package org.sagebionetworks.warehouse.workers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.aws.utils.sns.MessageUtil;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.bucket.FileSubmissionMessage;
import org.sagebionetworks.warehouse.workers.utils.XMLUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.sqs.model.Message;

public class AccessRecordWorkerUtil {

	/**
	 * Extract the bucket and key from the message body, and create an ObjectCSVReader
	 * to read access records from the collated file.
	 * 
	 * @param message - the message that was sent after a file is collated and submitted
	 * @param s3Client
	 * @return
	 * @throws IOException
	 */
	public static ObjectCSVReader<AccessRecord> getAccessRecordReader(Message message, AmazonS3Client s3Client) throws IOException {
		// extract the bucket and key from the message
		String xml = MessageUtil.extractMessageBodyAsString(message);
		FileSubmissionMessage fileSubmissionMessage = XMLUtils.fromXML(xml, FileSubmissionMessage.class, "Message");

		// read the file as a stream
		File file = File.createTempFile("collatedAccessRecords", ".csv.gz");
		s3Client.getObject(new GetObjectRequest(fileSubmissionMessage.getBucket(), fileSubmissionMessage.getKey()), file);
		return new ObjectCSVReader<AccessRecord>(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"), AccessRecord.class);
	}
}
