MetalIsX

1 Quick setup for using the monitor application

You need to follow the actions in this chapter for quickly 
enabling all functionality of the monitor application and 
to configure the Monitor application in Wildfly 8.1.0.Final.

2 Build

Build the projects:
 - install Maven (currently Maven 3.0.5).
 - open a dos command window
 - go to the directory containing this file
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

4 Deploy the Monitor application to Wildfly 8.2.0.Final

Install and configure Wildfly 8.2.0.Final
 - donwload and install/unzip the WildFly application
   server from: 
     http://wildfly.org/downloads/
 - configure the datasource see: 4.1 Datasource
 - configure the logging see: 4.2 Logging
 - copy the Monitor web application war file:
     monitor\monitor-war\target\monitor-war-1.0-SNAPSHOT.war
   to
     wildfly-8.2.0.Final\standalone\deployments
 - open a browser and navigate to:
     http://localhost:8080/monitor
 - Start the listener for retrieving log statements from
   the log file:
     - in the menu click on: Log File Listener
     - the field 'Filename' should contain the right log file
       if not point it to the right one
     - click on 'Start listener'
 - For test purpose you can enable the logging on the Monitor 
   application. Do this by executing the following actions:
     - in the menu click on: Settings
     - click on MONITOR_WEB_APPLICATION_DISABLE_PROFILER_FILTER
     - uncheck the checkbox: Value
     - click op 'Save'
     - click on MONITOR_WEB_APPLICATION_DISABLE_PROFILER_INTERCEPTOR
     - uncheck the checkbox: Value
     - click op 'Save'
     - click on MONITOR_WEB_APPLICATION_DISABLE_REQUEST_FILTER
     - uncheck the checkbox: Value
     - click op 'Save'

4.1 Datasource

Instructions to configure WildFly 8.1.0.Final standalone 
with the required jdbc/monitorDS datasource:
 - open the file <jboss home>/standalone/configuration/standalone.xml
 - find the subsystem with the datasources
 - add in the datasources section the following datasource to create a h2 database:
                <datasource jndi-name="java:/jdbc/monitorDS" pool-name="monitorDS" enabled="true" use-java-context="true">

                    <connection-url>jdbc:h2:file:${jboss.server.data.dir}/h2database/monitor;DB_CLOSE_DELAY=-1</connection-url>

                    <driver>h2</driver>

                    <security>

                        <user-name>sa</user-name>

                        <password>sa</password>
                    </security>

                </datasource>

 - you can change the datasource setting to your liking, but the 
   jndi-name should remain the same

4.2 Logger

Instructions to configure a seperated log file for the monitor application:
 - open the file <jboss home>/standalone/configuration/standalone.xml
 - find the subsystem with the logging
 - add in the subsystem the following file handler:
            <periodic-rotating-file-handler name="MONITOR" autoflush="true">

                <formatter>

                    <pattern-formatter pattern="%d %-5p [%c] (%t) [SessionId: %X{sessionid}, RequestId: %X{requestid}, ParentRequestId: %X{parentrequestid}, Organisatie: %X{organization}, Gebruikersnaam: %X{username}] (Depth: %X{depth}) %s%E%n"/>

                </formatter>

                <file relative-to="jboss.server.log.dir" path="monitor.log"/>

                <suffix value=".yyyy-MM-dd"/>

                <append value="false"/>

            </periodic-rotating-file-handler>

 - add in the subsystem the following logger for logging en processing
   monitor logging:
            <logger category="org.metalisx.monitor" use-parent-handlers="false">
                <level name="INFO"/>

                <handlers>

                    <handler name="MONITOR"/>

                </handlers>
            </logger>

 - add in the subsystem the following logger for logging en processing 
   hibernate logging:
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
