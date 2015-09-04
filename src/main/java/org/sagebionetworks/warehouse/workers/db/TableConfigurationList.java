package org.sagebionetworks.warehouse.workers.db;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper for the list of table configurations provided by Guice.
 *
 */
public class TableConfigurationList {

	List<TableConfiguration> list = new LinkedList<TableConfiguration>();

	/**
	 * 
	 * @return the list of table configurations
	 */
	public List<TableConfiguration> getList() {
		return list;
	}

	/**
	 * Add a table configuration to the list
	 * @param config
	 */
	public void add(TableConfiguration config) {
		this.list.add(config);
	}
}
