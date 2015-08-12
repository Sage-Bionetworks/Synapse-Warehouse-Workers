package org.sagebionetworks.warehouse.workers.bucket;

import org.sagebionetworks.warehouse.workers.utils.XMLUtils;

/**
 * A data transfer object for a folder.
 * 
 * @author jhill
 *
 */
public class FolderDto {

	public static final String FOLDER_DTO_ALIAS = "FolderDto";
	
	private String bucket;
	private String path;
	
	/**
	 * The name of the bucket containing the folder.
	 * @return
	 */
	public String getBucket() {
		return bucket;
	}
	/**
	 * The name of the bucket containing the folder.
	 * @param bucket
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	/**
	 * The full path of the folder.
	 * @return
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * The full path of the folder.
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Create a DTO from XML.
	 * @param xml
	 * @return
	 */
	public static FolderDto fromXMl(String xml){
		return XMLUtils.fromXML(xml, FolderDto.class, FOLDER_DTO_ALIAS);
	}
	
	/**
	 * Create XML to represent the given DTO.
	 * @param dto
	 * @return
	 */
	public static String toXML(FolderDto dto){
		return XMLUtils.toXML(dto, FOLDER_DTO_ALIAS);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bucket == null) ? 0 : bucket.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FolderDto other = (FolderDto) obj;
		if (bucket == null) {
			if (other.bucket != null)
				return false;
		} else if (!bucket.equals(other.bucket))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	
}
