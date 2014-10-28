MetalIsX

1 Using the monitor application

You need to follow the actions in this chapter for quickly 
enabling all functionality of the monitor application and 
to configure the Monitor application in Wildfly 8.1.0.Final.

2 Build

Build the projects:
 - install Maven (currently Maven 3.0.5).
 - open a prompt in the directory containing this file
 - build all projects by running:
     mvn clean install -DskipTests

3 Add Monitor stuf to your project

Enable your project to use the Monitor artifacts:
 - Include the Monitor artifacts in your projects Maven POM by
   adding the following lines in the dependencies part:
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-context</artifactId>
	</dependency>
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-context-slf4j-mdc</artifactId>
	</dependency>
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-domain</artifactId>
	</dependency>
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-profiler-slf4j-cdi-interceptor</artifactId>
	</dependency>
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-profiler-slf4j-servlet-filter</artifactId>
	</dependency>
	<dependency>
		<groupId>org.metalisx</groupId>
		<artifactId>monitor-request-servlet-filter</artifactId>
	</dependency>

Add the order for the request servlet filter and profiler servlet filter
to the web.xml
 - open the web.xml
 - add the absolute-order snippet:
	<absolute-ordering>
		<others/>
		<name>monitorRequestServletFilter</name>
		<name>monitorProfilerServletFilter</name>
	</absolute-ordering>

4 Deploy the Monitor application to Wildfly 8.1.0.Final

Install and configure Wildfly 8.1.0.Final
 - donwload and install/unzip the application
   server:
     Wildfly 8.1.0.Final
 - configure in the application server
   a datasource named: 
     jdbc/monitorDS
   follow the instruction in: 4.1 Datasource
 - copy the Monitor web application war file:
     monitor\monitor-war\target\monitor-war-1.0-SNAPSHOT.war
   to
     wildfly-8.1.0.Final\standalone\deployments
 - Set the correct logger output:
    - open the file <jboss home>/standalone/configuration/standalone.xml
    - find the section with: <subsystem xmlns="urn:jboss:domain:logging:1.1">
    - add the periodic-rotating-file-handler:
            <periodic-rotating-file-handler name="MONITOR" autoflush="true">
                <formatter>
                    <pattern-formatter pattern="%d %-5p [%c] (%t) [SessionId: %X{sessionid}, RequestId: %X{requestid}, ParentRequestId: %X{parentrequestid}, Organisatie: %X{organization}, Gebruikersnaam: %X{username}] (Depth: %X{depth}) %s%E%n"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="monitor.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="false"/>
            </periodic-rotating-file-handler>
    - for logging monitor log statements to the MONITOR add the logger:
            <logger category="org.metalisx.monitor" use-parent-handlers="false">
                <level name="INFO"/>
                <handlers>
                    <handler name="MONITOR"/>
                </handlers>
            </logger>
    - for logging hibernate statements add the logger:
            <logger category="org.hibernate.SQL" use-parent-handlers="false">
                <level name="DEBUG"/>
                <handlers>
                    <handler name="MONITOR"/>
                </handlers>
            </logger>
            <logger category="org.hibernate.type" use-parent-handlers="false">
                <level name="DEBUG"/>
                <handlers>
                	<handler name="MONITOR"/>
                </handlers>
            </logger>
 - open a browser
 - navigate to:
     localhost:8080\monitor

4.1 Datasource configuration 

Congigure the jdbc/monitorDS datasource in Wildfly 8.1.0.Final by
executing the following actions:
 - open the file <jboss home>/standalone/configuration/standalone.xml
 - find the subsystem with the datasources
 - add to the datasources section the following datasource, this will create a 
   h2 database with the monitor tables when the application is first started:
				<datasource jndi-name="java:jdbc/monitorDS" pool-name="monitorDS" enabled="true" use-java-context="true">
					<connection-url>jdbc:h2:file:${jboss.server.data.dir}/h2database/monitor;DB_CLOSE_DELAY=-1</connection-url>
					<driver>h2</driver>
					<security>
						<user-name>sa</user-name>
						<password>sa</password>
					</security>
				</datasource>
 - you can change the datasource setting to your liking, but the 
   jndi-name should remain the same
