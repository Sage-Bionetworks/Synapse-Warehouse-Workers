package org.sagebionetworks.warehouse.workers.db;

import java.util.List;

import org.sagebionetworks.warehouse.workers.utils.ClasspathUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.inject.Inject;


public class FolderMetadataDaoImpl implements FolderMetadataDao {
	
	JdbcTemplate template;
	
	@Inject
	public FolderMetadataDaoImpl(JdbcTemplate template) {
		super();
		this.template = template;
		// Create the table
		this.template.update(ClasspathUtils
				.loadStringFromClassPath("FolderState.ddl.sql"));
	}

	@Override
	public void markFolderAsRolling(String bucketName, String key,
			long modifiedOnTimeMS) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> listRollingFolders(String bucketName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void truncateTable() {
		// TODO Auto-generated method stub
		
	}

}
