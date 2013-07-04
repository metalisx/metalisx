package org.metalisx.monitor.web.service;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.monitor.domain.service.AdminService;
import org.metalisx.monitor.profiler.interceptor.Profile;


/**
 * REST admin service producing JSON objects.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/admin")
public class AdminRestService {

    @EJB
    private AdminService adminService;

    @GET
    @Path("/requests/clean")
    @Produces(MediaType.APPLICATION_JSON)
    public Object removeMonitorRequests() {
        adminService.removeMonitorRequests();
        return (new MessagesDto()).addInfo("Monitor requests are removed.");
    }

    @GET
    @Path("/logs/clean")
    @Produces(MediaType.APPLICATION_JSON)
    public Object removeMonitorLogs() {
        adminService.removeMonitorLogs();
        return (new MessagesDto()).addInfo("Monitor logs are removed.");
    }

}
