package org.metalisx.monitor.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MonitorContextSlf4jMdcTest {

	private static final String LOG_DIRECTORY = System.getProperty("jboss.server.log.dir");

	private static final String LOG_FILE = "monitor.log";

	@Inject
	private MonitorContextSlf4jMdcTestClass monitorContextMdcTestClass;

	private static final String KEY = "theKey";

	public MonitorContextSlf4jMdcTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return Deployments.createDeployment();
	}

	/**
	 * Test if an instance of the MonitorContext class in this artifact is used.
	 */
	@Test
	@InSequence(1)
	public void testMcdMonitorContextAvailable() throws IOException {
		Assert.assertTrue(
		        "The MonitorContext class is not available, the " + MonitorContextSimple.class.getSimpleName()
		                + " is still used.", MonitorContextFactory.getInstance((KEY)) instanceof MonitorContext);
	}

	@Test
	@InSequence(2)
	public void testMcdMonitorContext() throws IOException {
		initMonitorContext(KEY);
		monitorContextMdcTestClass.run();
		validateMonitorContext();
		MonitorContextFactory.clear(KEY);
	}
	
	@Test
	@InSequence(3)
	public void testMcdMonitorContextWithNullFields() throws IOException {
		initMonitorContextNullFields(KEY);
		monitorContextMdcTestClass.run();
		validateMonitorContextWithNullFields();
		MonitorContextFactory.clear(KEY);
	}
	
	private void initMonitorContext(String key) {
		InterfaceMonitorContext monitorContext = MonitorContextFactory.getInstance(key);
		monitorContext.setSessionId("a-sessionid-1");
		monitorContext.setRequestId("a-requestid-1");
		monitorContext.setParentRequestId("a-parentrequestid-1");
		monitorContext.setOrganization("Daily Bugle");
		monitorContext.setUsername("Peter");
		monitorContext.increaseDepth();
	}

	private void initMonitorContextNullFields(String key) {
		InterfaceMonitorContext monitorContext = MonitorContextFactory.getInstance(key);
		monitorContext.setRequestId("a-requestid-1");
	}

	private void validateMonitorContext() throws IOException {
		if (LOG_DIRECTORY != null) {
			String logFile = LOG_DIRECTORY + File.separator + LOG_FILE;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			bufferedReader.close();
			Assert.assertEquals(2, lines.size());
			int lineIndex = 0;
			String expectedRegularExpression = ".+ "
			        + "\\[SessionId: a-sessionid-1, RequestId: a-requestid-1, ParentRequestId: a-parentrequestid-1, Organisatie: Daily Bugle, Gebruikersnaam: Peter] "
			        + "\\(Depth: 2\\) A message";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
			        + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedRegularExpression));
			lineIndex = 1;
			String expectedProfilingRegularExpression = ".+ "
			        + "\\[SessionId: a-sessionid-1, RequestId: a-requestid-1, ParentRequestId: a-parentrequestid-1, Organisatie: Daily Bugle, Gebruikersnaam: Peter] "
			        + "\\(Depth: 2\\) A message, time: .+ms";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match ["
			        + expectedProfilingRegularExpression + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedProfilingRegularExpression));
		}
	}

	private void validateMonitorContextWithNullFields() throws IOException {
		if (LOG_DIRECTORY != null) {
			String logFile = LOG_DIRECTORY + File.separator + LOG_FILE;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			bufferedReader.close();
			Assert.assertEquals(4, lines.size());
			int lineIndex = 2;
			String expectedRegularExpression = ".+ "
			        + "\\[SessionId: , RequestId: a-requestid-1, ParentRequestId: , Organisatie: , Gebruikersnaam: ] "
			        + "\\(Depth: 1\\) A message";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
			        + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedRegularExpression));
			lineIndex = 3;
			String expectedProfilingRegularExpression = ".+ "
			        + "\\[SessionId: , RequestId: a-requestid-1, ParentRequestId: , Organisatie: , Gebruikersnaam: ] "
			        + "\\(Depth: 1\\) A message, time: .+ms";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match ["
			        + expectedProfilingRegularExpression + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedProfilingRegularExpression));
		}
	}

}
