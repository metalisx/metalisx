package org.metalisx.monitor.context.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MonitorContextRequestUtilsTest {

	private static final String LOG_DIRECTORY = System.getProperty("jboss.server.log.dir");

	private static final String LOG_FILE = "monitor.log";

	public MonitorContextRequestUtilsTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return Deployments.createDeployment();
	}

	@RunAsClient
	@InSequence(1)
	@Test
	public void initMonitorContextTest(@ArquillianResource URL baseURL) throws IOException {
		URL url = new URL(baseURL, "test");
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		bufferedReader.close();
		Assert.assertEquals(TestServlet.RESPONSE_MESSAGE, stringBuilder.toString());
	}

	/**
	 * Test if the monitor.log file contains the correct data, which was added
	 * by the previous test calling the test servlet.
	 */
	@InSequence(2)
	@Test
	public void validateInitMonitorContextTest() throws IOException {
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
			        + "\\[SessionId: .+, RequestId: .+, ParentRequestId: , Organisatie: , Gebruikersnaam: ] "
			        + "\\(Depth: 1\\) A message";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
			        + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedRegularExpression));
			lineIndex = 1;
			String expectedProfilingRegularExpression = ".+ "
			        + "\\[SessionId: .+, RequestId: .+, ParentRequestId: , Organisatie: , Gebruikersnaam: ] "
			        + "\\(Depth: 1\\) A message, time: .+ms";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match ["
			        + expectedProfilingRegularExpression + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedProfilingRegularExpression));
		}
	}

	@RunAsClient
	@InSequence(3)
	@Test
	public void initMonitorContextWithParentMonitorContextTest(@ArquillianResource URL baseURL) throws IOException {
		URL url = new URL(baseURL, "test");
		URLConnection urlConnection = url.openConnection();
		setParentMonitorContextOnRequest(urlConnection);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		bufferedReader.close();
		Assert.assertEquals(TestServlet.RESPONSE_MESSAGE, stringBuilder.toString());
	}
	
	private void setParentMonitorContextOnRequest(URLConnection urlConnection) {
		urlConnection.setRequestProperty(MonitorContextRequestUtils.REQUEST_HEADER_REQUEST_ID,"a-parentrequestid-1");
		urlConnection.setRequestProperty(MonitorContextRequestUtils.REQUEST_HEADER_ORGANIZATION,"Daily Bugle");
		urlConnection.setRequestProperty(MonitorContextRequestUtils.REQUEST_HEADER_USERNAME,"Peter");
		urlConnection.setRequestProperty(MonitorContextRequestUtils.REQUEST_HEADER_DEPTH,"1");
	}

	@InSequence(4)
	@Test
	public void validateInitMonitorContextWithParentMonitorContextTest() throws IOException {
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
			        + "\\[SessionId: .+, RequestId: .+, ParentRequestId: a-parentrequestid-1, Organisatie: Daily Bugle, Gebruikersnaam: Peter] "
			        + "\\(Depth: 2\\) A message";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
			        + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedRegularExpression));
			lineIndex = 3;
			String expectedProfilingRegularExpression = ".+ "
			        + "\\[SessionId: .+, RequestId: .+, ParentRequestId: a-parentrequestid-1, Organisatie: Daily Bugle, Gebruikersnaam: Peter] "
			        + "\\(Depth: 2\\) A message, time: .+ms";
			Assert.assertTrue("Line in the log file is inccorrect, expected to match ["
			        + expectedProfilingRegularExpression + "] but was [" + lines.get(lineIndex) + "]",
			        lines.get(lineIndex).matches(expectedProfilingRegularExpression));
		}
	}

}
