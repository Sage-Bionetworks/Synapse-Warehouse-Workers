package org.sagebionetworks.warehouse.workers.bucket;

/**
 * Information about a bucket to be processed.
 * 
 */
public class BucketInfo {
	
	private String bucketName;
	int timestampColumnIndex;
	
	/**
	 * The name of the bucket to be processed.
	 * 
	 * @return
	 */
	public String getBucketName() {
		return bucketName;
	}
	/**
	 * The name of the bucket to be processed.
	 * @param bucketName
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	/**
	 * The name of the bucket to be collated.
	 * @param bucketName
	 * @return
	 */
	public BucketInfo withBucketName(String bucketName){
		setBucketName(bucketName);
		return this;
	}
	
	/**
	 * The index of the time stamp column for all files within this bucket.
	 * Files in this bucket will be collated on this time stamp.
	 * @return
	 */
	public int getTimestampColumnIndex() {
		return timestampColumnIndex;
	}
	
	/**
	 * The index of the time stamp column for all files within this bucket.
	 * Files in this bucket will be collated on this time stamp.
	 * @param timestampColumnIndex
	 */
	public void setTimestampColumnIndex(int timestampColumnIndex) {
		this.timestampColumnIndex = timestampColumnIndex;
	}
	
	/**
	 * The index of the time stamp column for all files within this bucket.
	 * Files in this bucket will be collated on this time stamp.
	 * @param timestampColumnIndex
	 * @return
	 */
	public BucketInfo withTimestampColumnIndex(int timestampColumnIndex){
		setTimestampColumnIndex(timestampColumnIndex);
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bucketName == null) ? 0 : bucketName.hashCode());
		result = prime * result + timestampColumnIndex;
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
		BucketInfo other = (BucketInfo) obj;
		if (bucketName == null) {
			if (other.bucketName != null)
				return false;
		} else if (!bucketName.equals(other.bucketName))
			return false;
		if (timestampColumnIndex != other.timestampColumnIndex)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BucketCollateInfo [bucketName=" + bucketName
				+ ", timestampIndex=" + timestampColumnIndex + "]";
	}

}
