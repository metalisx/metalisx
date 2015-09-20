package org.metalisx.monitor.request.servlet.filter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.monitor.domain.model.MonitorRequest;

@RunWith(Arquillian.class)
public class RequestFilterTest {

    @PersistenceContext(unitName = "monitorPU")
    private EntityManager entityManager;

    public RequestFilterTest() {
    }

    @Deployment
    public static Archive<?> createTestArchive() {
    	return Deployments.createDeployment();
    }

    /**
     * Test if the servlet correctly response to the request. The request
     * triggers the request servlet filter to persist one request and response.
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
        assertEquals(TestServlet.RESPONSE_MESSAGE, stringBuilder.toString());
    }

    /**
     * Test if the contains one request and response, which was persisted by the
     * previous test calling the test servlet.
     */
    @InSequence(2)
    @Test
    public void testRequestFilter() throws IOException {
        String queryString = "select o from " + MonitorRequest.class.getSimpleName() + " o " +
        		" where url like '%/RequestServletFilterTest/test%'";
        TypedQuery<MonitorRequest> typedQuery = entityManager.createQuery(queryString, MonitorRequest.class);
        List<MonitorRequest> monitorRequestList = typedQuery.getResultList();
        assertEquals(1, monitorRequestList.size());
        MonitorRequest monitorRequest = monitorRequestList.get(0);
        assertEquals(1, monitorRequestList.size());
        assertEquals("GET", monitorRequest.getMethod());
        assertEquals("http", monitorRequest.getScheme());
        assertEquals("127.0.0.1", monitorRequest.getServerName());
        assertEquals(8080, monitorRequest.getServerPort());
        assertEquals("/RequestServletFilterTest", monitorRequest.getContextPath());
        assertEquals("/test", monitorRequest.getServletPath());
        assertEquals(null, monitorRequest.getPathInfo());
        assertEquals(null, monitorRequest.getQueryString());
        assertEquals(null, monitorRequest.getPathTranslated());
        assertEquals(200, monitorRequest.getResponse().getStatus());
    }

}
