package org.metalisx.monitor.profiler.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;

@RunWith(Arquillian.class)
public class ProfilerInterceptorTest {

	private static final String LOG_DIRECTORY = System.getProperty("jboss.server.log.dir");

	private static final String LOG_FILE = "monitor.log";

    @Inject
    private ProfiledTestClass profiledTestClass;

    public ProfilerInterceptorTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
    	return Deployments.createDeployment();
    }

    /**
     * Test if the monitor.log file contains the correct data, which was added
     * by calling the method on the test class.
     */
    @Test
    public void testProfiler() throws IOException {
        initMonitorContext();
        profiledTestClass.run();
        if (LOG_DIRECTORY != null) {
            String logFile = LOG_DIRECTORY + File.separator + LOG_FILE;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
            String line;
            List<String> lines = new ArrayList<String>();
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            assertEquals(1, lines.size());
            String expectedRegularExpression = ".+ "
                    + "\\[SessionId: a-sessionid-1, RequestId: .+, "
                    + "Organisatie: Daily Bugle, Gebruikersnaam: Peter] \\(Depth: 2\\) "
                    + "org.metalisx.monitor.profiler.interceptor.ProfiledTestClass\\$Proxy\\$_\\$\\$_WeldSubclass.run, "
                    + "time: .+ms";
            assertTrue("Line in the log file is inccorrect, expected to match [" + expectedRegularExpression
                    + "] but was [" + lines.get(0) + "]", lines.get(0).matches(expectedRegularExpression));
        }
    }

    private void initMonitorContext() {
        InterfaceMonitorContext monitorContext = MonitorContextFactory.getInstance("theKey");
        monitorContext.setSessionId("a-sessionid-1");
        monitorContext.setOrganization("Daily Bugle");
        monitorContext.setUsername("Peter");
        monitorContext.increaseDepth();
    }

}
