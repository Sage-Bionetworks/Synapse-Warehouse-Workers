# Synapse-Warehouse-Workers
Workers that maintain analytical data related to Synapse.

## Build
In order to build the following properties must be available to the JVM:

Key | Description
------------ | -------------
org.sagebionetworks.warehouse.worker.stack | A unique identifier for a stack. This prefix is applied to all AWS resources.
org.sagebionetworks.stack.iam.id | The AMI id used used to connect to AWS
org.sagebionetworks.stack.iam.key | The AMI secret key used to connect to AWS

### Settings.xml
One option for making the properties available is to add them to the maven settings file:
```
<user_home>\.m2\settings.xml
```
Here is an example:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
	<localRepository />
	<interactiveMode />
	<usePluginRegistry />
	<offline />
	<pluginGroups />
	<servers>
	</servers>
	<mirrors />
	<proxies />
	<profiles>
		<profile>
			<id>dev-environment</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<properties>
				<org.sagebionetworks.warehouse.worker.stack>devhill</org.sagebionetworks.warehouse.worker.stack>
				<org.sagebionetworks.stack.iam.id>your-id</org.sagebionetworks.stack.iam.id>
				<org.sagebionetworks.stack.iam.key>your-key</org.sagebionetworks.stack.iam.key>						
			</properties>
		</profile>
	</profiles>
	<activeProfiles />
</settings>
```
