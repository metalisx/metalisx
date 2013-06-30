package org.metalisx.monitor.web.it;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.monitor.web.it.page.LogFileListenersPage;
import org.metalisx.monitor.web.it.page.LogsPage;
import org.metalisx.monitor.web.it.page.RequestsPage;
import org.metalisx.monitor.web.it.page.SummaryPage;

@RunWith(Arquillian.class)
public class ApplicationTest extends AbstractApplicationTest {

    @RunAsClient
    @InSequence(1)
    @Test
    public void logFileListenersTestCase() throws Exception {
        LogFileListenersPage logFileListenersPage = new LogFileListenersPage(getWebDriver(), getContextPath());
        logFileListenersPage.test();
    }

    @RunAsClient
    @InSequence(2)
    @Test
    public void requestsTestCase() throws Exception {
        RequestsPage requestsPage = new RequestsPage(getWebDriver(), getContextPath());
        requestsPage.test();
    }

    @RunAsClient
    @InSequence(3)
    @Test
    public void logsTestCase() throws Exception {
        LogsPage logsPage = new LogsPage(getWebDriver(), getContextPath());
        logsPage.test();
    }

    @RunAsClient
    @InSequence(4)
    @Test
    public void summaryTestCase() throws Exception {
        SummaryPage summaryPage = new SummaryPage(getWebDriver(), getContextPath());
        summaryPage.test();
    }

}
