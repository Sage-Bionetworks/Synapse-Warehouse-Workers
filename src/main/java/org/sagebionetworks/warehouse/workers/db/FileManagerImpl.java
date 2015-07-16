package org.sagebionetworks.warehouse.workers.db;

import java.util.Iterator;

import org.sagebionetworks.workers.util.progress.ProgressCallback;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.inject.Inject;

public class FileManagerImpl implements FileManager{
	
	private FolderMetadataDao folderMetadataDao;
	private FileMetadataDao fileMetadataDao;
	
	@Inject
	public FileManagerImpl(FolderMetadataDao folderMetadataDao,
			FileMetadataDao fileMetadataDao) {
		super();
		this.folderMetadataDao = folderMetadataDao;
		this.fileMetadataDao = fileMetadataDao;
	}

	@Override
	public void addS3Objects(Iterator<S3ObjectSummary> objectStream,
			ProgressCallback<Void> progressCallback) {
		
	}

}
