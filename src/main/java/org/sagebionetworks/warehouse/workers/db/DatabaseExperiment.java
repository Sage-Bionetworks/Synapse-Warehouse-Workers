package org.sagebionetworks.warehouse.workers.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.aws.utils.s3.ObjectCSVReader;
import org.sagebionetworks.repo.model.audit.AccessRecord;
import org.sagebionetworks.warehouse.workers.collate.StreamResourceProvider;
import org.sagebionetworks.warehouse.workers.config.ApplicationServletContextListener;
import org.sagebionetworks.warehouse.workers.model.SnapshotHeader;
import org.sagebionetworks.warehouse.workers.utils.AccessRecordUtils;

import com.google.inject.Injector;

/**
 * This class is used to test the Database performance with different settings.
 */
public class DatabaseExperiment {

	private static Logger log = LogManager.getLogger(DatabaseExperiment.class);
	private static Injector injector;
	private static final int BATCH_SIZE = 25000;

	@Inject
	public DatabaseExperiment(Injector injector){
		this.injector = injector;
	}

	public static void main(String[] args) {
		Injector injector = ApplicationServletContextListener.createNewGuiceInjector();
		DatabaseExperiment experiment = new DatabaseExperiment(injector);
		experiment.start(args);
	}

	private void start(String[] args) {
		try {
			log.info("Starting experiment...");
			if(injector == null){
				log.error("Injector is null. Cannot start the application.");
				return;
			}
			if (args.length == 0) {
				throw new IllegalArgumentException("Must provide the file path to the access record files.");
			}
			final File filePath = new File(args[0]);
			if (!filePath.exists()) {
				throw new IllegalArgumentException("File " + filePath.getPath() + " does not exist.");
			}

			final List<File> files = new ArrayList<File>();
			getCsvGzFiles(filePath, files);

			AccessRecordDao dao = injector.getInstance(AccessRecordDao.class);
			StreamResourceProvider provider = injector.getInstance(StreamResourceProvider.class);
			long startProcess = System.currentTimeMillis();
			int totalRecords = 0;
			for (File file : files) {
				log.info("Processing "+ file.getName());
				long start = System.currentTimeMillis();
				int noRecords = process(file, dao, provider);
				log.info("Wrote "+noRecords+" records in "+(System.currentTimeMillis() - start)+" milis.");
				totalRecords += noRecords;
			}
			log.info("Processed "+files.size()+" files, "+totalRecords+" records in "+(System.currentTimeMillis() - startProcess)+" milis.");
		} catch (Exception e) {
			log.error("Failed to start application: "+e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read an access record file and write all records to the database
	 * 
	 * @param file - the file to read
	 * @param dao - used to write records to the database
	 * @param provider - provides an ObjectCSVReader to read records from gzip files
	 * @return the number of records written
	 * @throws IOException
	 */
	private static int process(File file, AccessRecordDao dao, StreamResourceProvider provider) throws IOException {
		ObjectCSVReader<AccessRecord> reader = provider.createObjectCSVReader(file, AccessRecord.class, SnapshotHeader.ACCESS_RECORD_HEADERS);
		AccessRecord record = null;
		List<AccessRecord> batch = new ArrayList<AccessRecord>(BATCH_SIZE);
		int noRecords = 0;
		while ((record = reader.next()) != null) {
			if (!AccessRecordUtils.isValidAccessRecord(record)) {
				log.error("Invalid Access Record: " + record.toString());
				continue;
			}
			batch.add(record);
			if (batch.size() >= BATCH_SIZE) {
				dao.insert(batch);
				noRecords += batch.size();
				batch .clear();
			}
		}

		if (batch.size() > 0) {
			dao.insert(batch);
			noRecords += batch.size();
		}
		return noRecords;
	}

	/**
	 * Gets all the "csv.gz" files but exclude the "rolling" ones.
	 */
	private static void getCsvGzFiles(File file, List<File> files) {
		if (file.isFile()) {
			final String fileName = file.getName();
			if (fileName.endsWith("csv.gz") && !fileName.contains("rolling")) {
				files.add(file);
			}
			return;
		}
		File[] moreFiles = file.listFiles();
		for (File f : moreFiles) {
			getCsvGzFiles(f, files);
		}
	}

}
