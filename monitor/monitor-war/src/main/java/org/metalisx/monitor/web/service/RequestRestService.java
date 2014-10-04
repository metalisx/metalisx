package org.metalisx.monitor.web.service;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.utils.DateUtils;
import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorRequestCertificate;
import org.metalisx.monitor.domain.model.MonitorRequestPart;
import org.metalisx.monitor.domain.model.MonitorResponse;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;
import org.metalisx.monitor.domain.service.MonitorRequestService;
import org.metalisx.monitor.profiler.interceptor.Profile;
import org.metalisx.monitor.web.utils.MonitorRequestSendUtil;
import org.metalisx.utils.PrettyPrintUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.x509.X509CertImpl;

/**
 * REST request monitor service producing JSON objects.
 * 
 * @author Stefan Oude Nijhuis
 */
@SuppressWarnings("restriction")
@Profile
@Path("/requests")
public class RequestRestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestRestService.class);

    @EJB
    private MonitorRequestService monitorRequestService;

    public RequestRestService() {
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto filter() {
        return new ItemDto(new MonitorRequestFilter());
    }

    @POST
    @Path("/page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public PageDto<MonitorRequestList> getPage(PageContextDto<MonitorRequestFilter> pageContextDto) {
    	DateUtils.processDateRange(pageContextDto.getFilter());
        return monitorRequestService.findPage(pageContextDto);
    }

    @GET
    @Path("/list-item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto getListItemContent(@PathParam("id") Long id) {
        return new ItemDto(monitorRequestService.findById(id));
    }

    @GET
    @Path("/request/resend/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto resend(@PathParam("id") Long id) {
        HttpResponse httpResponse = MonitorRequestSendUtil.send(monitorRequestService.findById(id));
        return (new MessagesDto()).addInfo("Request resend status result: " + httpResponse.getStatusLine().toString());
    }

    @GET
    @Path("/request/content/{id}")
    public Response getRequestContent(@PathParam("id") Long id) {
        MonitorRequest request = monitorRequestService.findById(id);
        if (request != null) {
            ResponseBuilder responseBuilder = Response.ok(request.getContent());
            responseBuilder.type(request.getContentType());
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/request/content-as-text/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequestContentAsText(@PathParam("id") Long id) {
        MonitorRequest request = monitorRequestService.findById(id);
        if (request != null && request.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(request.getContent());
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/request/content-pretty-print/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequestContentPrettyPrint(@PathParam("id") Long id) {
        MonitorRequest request = monitorRequestService.findById(id);
        if (request != null && request.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(PrettyPrintUtils.prettyPrint(request.getContentType(),
                    request.getContent()));
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/response/content/{id}")
    public Response getResponseContent(@PathParam("id") Long id) {
        MonitorResponse response = monitorRequestService.findResponse(id);
        if (response != null) {
            ResponseBuilder responseBuilder = Response.ok(response.getContent());
            responseBuilder.type(response.getContentType());
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/response/content-as-text/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResponseContentAsText(@PathParam("id") Long id) {
        MonitorResponse response = monitorRequestService.findResponse(id);
        if (response != null && response.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(response.getContent());
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/response/content-pretty-print/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getResponseContentPrettyPrint(@PathParam("id") Long id) {
        MonitorResponse response = monitorRequestService.findResponse(id);
        if (response != null && response.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(PrettyPrintUtils.prettyPrint(response.getContentType(),
                    response.getContent()));
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/part/content/{id}")
    public Response getPartContent(@PathParam("id") Long id) {
        MonitorRequestPart monitorRequestPart = monitorRequestService.findPart(id);
        if (monitorRequestPart != null) {
            ResponseBuilder responseBuilder = Response.ok(monitorRequestPart.getContent());
            responseBuilder.type(monitorRequestPart.getContentType());
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/part/content-as-text/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPartContentAsText(@PathParam("id") Long id) {
        MonitorRequestPart monitorRequestPart = monitorRequestService.findPart(id);
        if (monitorRequestPart != null && monitorRequestPart.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(monitorRequestPart.getContent());
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/part/content-pretty-print/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPartContentPrettyPrint(@PathParam("id") Long id) {
        MonitorRequestPart monitorRequestPart = monitorRequestService.findPart(id);
        if (monitorRequestPart != null && monitorRequestPart.isTextContent()) {
            ResponseBuilder responseBuilder = Response.ok(PrettyPrintUtils.prettyPrint(
                    monitorRequestPart.getContentType(), monitorRequestPart.getContent()));
            responseBuilder.type("plain/text");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/certificate/info/{id}")
    public Response getRequestCertificateInfo(@PathParam("id") Long id) {
        MonitorRequestCertificate monitorRequestCertificate = monitorRequestService.findCertificate(id);
        if (monitorRequestCertificate != null && monitorRequestCertificate.getContent() != null) {
            try {
                X509Certificate certificate = new X509CertImpl(monitorRequestCertificate.getContent());
                ResponseBuilder responseBuilder = Response.ok(certificate.toString());
                responseBuilder.type("text/html");
                return responseBuilder.build();
            } catch (CertificateException e) {
                logger.error("Error converting monitor request certificate with id " + id + " to an X509Certificate.");
                e.printStackTrace();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/certificate/download/{id}")
    public Response getRequestCertificateDownload(@PathParam("id") Long id) {
        MonitorRequestCertificate monitorRequestCertificate = monitorRequestService.findCertificate(id);
        if (monitorRequestCertificate != null) {
            ResponseBuilder responseBuilder = Response.ok(monitorRequestCertificate.getContent());
            responseBuilder.type("application/octet-stream");
            responseBuilder.header("Content-Disposition", "attachment; filename='certificate'");
            return responseBuilder.build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

}
