package org.metalisx.monitor.profiler.servlet.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
public class ProfilerFilterTest {

	private static final String LOG_DIRECTORY = System.getProperty("jboss.server.log.dir");

	private static final String LOG_FILE = "monitor.log";

    public ProfilerFilterTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
    	return Deployments.createDeployment();
    }

    /**
     * Test if the servlet correctly response to the request. The request
     * triggers the profiler servlet filter to log a profile log statement into
     * the monitor.log.
     */
    @RunAsClient
    @InSequence(1)
    @Test
    public void testHttpRequest(@ArquillianResource URL baseURL) throws IOException {
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
    public void testProfilerFilter() throws IOException {
        if (LOG_DIRECTORY != null) {
            String logFile = LOG_DIRECTORY + File.separator + LOG_FILE;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
            String line;
            List<String> lines = new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            Assert.assertEquals(1, lines.size());
            int lineIndex = 0;
            String expectedRegularExpression = ".+ " + "\\[SessionId: .+, RequestId: .+, Organisatie: , "
                    + "Gebruikersnaam: ] \\(Depth: 1\\) http://127.0.0.1:8180/ProfilerFilterTest/test, time: .+ms";
            Assert.assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
                    + "] but was [" + lines.get(lineIndex) + "]", lines.get(lineIndex).matches(expectedRegularExpression));
        }
    }

}
