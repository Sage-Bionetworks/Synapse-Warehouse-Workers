package org.sagebionetworks.warehouse.workers.bucket;

import java.util.List;

/**
 * List of information about buckets that should have their files collated.
 *
 */
public class BucketInfoList {

	private List<BucketInfo> bucketList;

	
	public BucketInfoList(List<BucketInfo> bucketList) {
		super();
		this.bucketList = bucketList;
	}

	/**
	 * Each info object in the list provides information about a bucket that should be collated.
	 * @return
	 */
	public List<BucketInfo> getBucketList() {
		return bucketList;
	}

	/**
	 * Each info object in the list provides information about a bucket that should be collated.
	 * @param bucketList
	 */
	public void setBucketList(List<BucketInfo> bucketList) {
		this.bucketList = bucketList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bucketList == null) ? 0 : bucketList.hashCode());
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
		BucketInfoList other = (BucketInfoList) obj;
		if (bucketList == null) {
			if (other.bucketList != null)
				return false;
		} else if (!bucketList.equals(other.bucketList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BucketsToCollate [bucketList=" + bucketList + "]";
	}
	
}
