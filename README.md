# Synapse-Warehouse-Workers
Workers that maintain analytical data related to Synapse.

## Build
In order to build the following properties must be avaiable to the JVM:
Key  | Description
------------ | -------------
org.sagebionetworks.warehouse.worker.stack  | A unique identifier for a stack.  For developer buids use your name. 
org.sagebionetworks.stack.iam.id  | The AMI id used used to connect to AWS
org.sagebionetworks.stack.iam.key  | The AMI secret key used to connect to AWS
