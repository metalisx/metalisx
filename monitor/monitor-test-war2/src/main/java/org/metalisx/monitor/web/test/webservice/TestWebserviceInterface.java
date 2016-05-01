package org.metalisx.monitor.web.test.webservice;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace = "http://webservice.test.web.monitor.metalisx.org/")
public interface TestWebserviceInterface {

    @WebMethod
    @WebResult String runTest(@WebParam Date date, @WebParam String name);

}
