# Synapse-Warehouse-Workers
Workers that maintain analytical data related to Synapse. More information can be found at [warehouse-workers-wiki](https://sagebionetworks.jira.com/wiki/display/PLFM/Synapse+Warehouse+workers)

## Build
In order to build the following properties must be available to the JVM:

Key | Description
------------ | -------------
org.sagebionetworks.warehouse.workers.stack | A unique identifier for a stack. This prefix is applied to all AWS resources.
org.sagebionetworks.stack.iam.id | The AMI id used used to connect to AWS
org.sagebionetworks.stack.iam.key | The AMI secret key used to connect to AWS
org.sagebionetworks.warehouse.workers.jdbc.user.username | database username
org.sagebionetworks.warehouse.workers.jdbc.user.password | database password

### Local MySQL
By default the build will attempt to connect to a local MySQL database with a schema named: warehouse.
This can be override by providing a new value for the following property:
```
org.sagebionetworks.warehouse.workers.jdbc.connection.url=jdbc:mysql://localhost/warehouse
```

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
				<org.sagebionetworks.warehouse.workers.stack>devhill</org.sagebionetworks.warehouse.workers.stack>
				<org.sagebionetworks.stack.iam.id>your-id</org.sagebionetworks.stack.iam.id>
				<org.sagebionetworks.stack.iam.key>your-key</org.sagebionetworks.stack.iam.key>						
			</properties>
		</profile>
	</profiles>
	<activeProfiles />
</settings>
```
