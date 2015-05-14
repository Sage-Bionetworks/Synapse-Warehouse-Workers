package org.sagebionetworks.warehouse.workers;

/**
 * Abstraction for a worker that finds all access record files that need to
 * processed and pushes the results to a queue to be processed by another
 * worker.
 * 
 * @author John
 * 
 */
public interface AccessRecordBootstrap extends WorkerStack {

}
