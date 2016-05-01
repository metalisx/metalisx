package org.metalisx.monitor.web.test.logging;

import java.net.MalformedURLException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.ItemsDto;
import org.metalisx.monitor.domain.model.User;
import org.metalisx.monitor.domain.service.UserService;


@Path("/test")
public class TestLoggingRestService {

    @Inject
    private TestLoggingHierarchy1 testLoggingHierarchy1;

    @Inject
    private UserService userService;

    @POST
    @Path("/testDomainServiceLoggingPersist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ItemsDto testDomainServiceLoggingPersist(User user) {
        userService.persist(user);
        List<User> list = userService.findAll();
        return new ItemsDto(list);
    }

    @DELETE
    @Path("/testDomainServiceLoggingClean")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto testDomainServiceLoggingClean() {
        int count = userService.clean();
        String result = "Removed " + count + " records.";
        return new ItemDto(result);
    }

    @GET
    @Path("/testLoggingHierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto testLoggingHierarchy() throws MalformedURLException {
        testLoggingHierarchy1.executeGoBack();
        testLoggingHierarchy1.executeAndWalkFurther();
        testLoggingHierarchy1.executeGoBack();
        testLoggingHierarchy1.executeAndWalkFurther();
        testLoggingHierarchy1.executeAndWalkFurther();
        testLoggingHierarchy1.executeGoBack();
        String result = "Logging hierarchy statements are written.";
        return new ItemDto(result);
    }

}
