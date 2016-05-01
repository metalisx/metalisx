package org.metalisx.monitor.web.test.webservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Stateless;
import javax.jws.WebService;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.metalisx.monitor.domain.interceptor.DisableLogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@DisableLogListener
@Stateless
@WebService(portName = "TestPort", serviceName = "TestService", name = "Test",
        targetNamespace = "http://webservice.test.web.monitor.metalisx.org/",
        endpointInterface = "org.metalisx.monitor.web.test.webservice.TestWebserviceInterface")
public class TestWebservice implements TestWebserviceInterface {

    private static final Logger logger = LoggerFactory.getLogger(TestWebservice.class);
	
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public String runTest(Date date, String name) {
    	InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
    	Date startDate = new Date();
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String dateAsString = date != null ? "(" + format.format(date) + ")" : "";
    	logger.info(monitorContext.profileFormat("runTest", (new Date()).getTime() - startDate.getTime()));
        return "Webservice says: (" + dateAsString + ") Hello " + name + ".";
    }

}
