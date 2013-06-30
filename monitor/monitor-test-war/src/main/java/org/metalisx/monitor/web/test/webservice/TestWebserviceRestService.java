package org.metalisx.monitor.web.test.webservice;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.service.AbstractRestService;
import org.metalisx.monitor.domain.model.User;


@Path("/test")
public class TestWebserviceRestService extends AbstractRestService {

    @POST
    @Path("/testWebservice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto testWebservice(@Context HttpServletRequest request, User user)
            throws MalformedURLException {
        String webserviceCallResult = (new TestWebserviceClient(request)).runTest(user.getDate(), user.getName());
        return new ItemDto(webserviceCallResult);
    }
    
}
