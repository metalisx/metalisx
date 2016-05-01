package org.metalisx.monitor.web.test.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import org.metalisx.monitor.context.request.MonitorContextRequestUtils;

public class TestWebserviceClient implements TestWebserviceInterface {

	private TestWebserviceInterface client;

	public TestWebserviceClient(HttpServletRequest request) throws MalformedURLException {
		String wsdlUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
		        + request.getContextPath() + "/" + "TestService/Test?wsdl";
		Service testWebservice = Service.create(new URL(wsdlUrl), new QName(
		        "http://webservice.test.web.monitor.metalisx.org/", "TestService"));
		client = testWebservice.getPort(TestWebserviceInterface.class);

		BindingProvider bindingProvider = (BindingProvider) client;
		bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
		        MonitorContextRequestUtils.monitorContextToHttpHeaders());
	}

	@Override
	public String runTest(Date date, String name) {
		return client.runTest(date, name);
	}

}
