package org.sagebionetworks.warehouse.workers.db;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Provides paginated query results as an iterator implementation for easy consumption.
 *
 * @param <T> The type of the query results.
 */
public class QueryIterator<T> implements Iterator<T> {
	
	Long pageSize;
	JdbcTemplate template;
	RowMapper<T> rowMapper;
	String query;
	Object[] args;
	List<T> currentPage;
	Iterator<T> iterator;
	int currentIndex;

	/**
	 * Create a new iterator for each use.
	 * 
	 * @param pageSize Sets the number of rows fetched for each page of the query.
	 * @param template Template used to execute the query.
	 * @param query The SQL query to page over.  Note: The query should not include limit and offset.
	 * @param rowMapper
	 * @param args
	 */
	public QueryIterator(Long pageSize, JdbcTemplate template, String query, RowMapper<T> rowMapper,
			 Object...args) {
		super();
		this.pageSize = pageSize;
		this.template = template;
		this.rowMapper = rowMapper;
		// Add limit and offset to the query.
		this.query = query +" LIMIT ? OFFSET ?";		
		// add limit and offset to the args.
		this.args = ArrayUtils.addAll(args, pageSize, new Long(0));

	}

	@Override
	public boolean hasNext() {
		if(currentPage == null){
			// execute the first page of the query
			queryNextPage();
		}
		// If the current page is empty there are no more results.
		if(currentPage.isEmpty()){
			return false;
		}
		if(currentIndex < currentPage.size()){
			// The current index is on this page.
			return true;
		}else{
			// Get the next page.
			// Update the offset argument.
			long offset = (Long) args[args.length-1];
			offset += pageSize;
			args[args.length-1] = offset;
			// query
			queryNextPage();
			return hasNext();
		}
	}
	
	private void queryNextPage(){
		currentPage = template.query(query, rowMapper, args);
		iterator = currentPage.iterator();
		currentIndex = 0;
	}

	@Override
	public T next() {
		T result = currentPage.get(currentIndex);
		currentIndex++;
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not supported");
	}

}
