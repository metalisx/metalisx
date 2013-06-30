package org.metalisx.monitor.web.test.inputfield;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.parameter.DateParameter;
import org.metalisx.common.rest.service.AbstractRestService;
import org.metalisx.monitor.domain.model.User;

@Path("/test")
public class TestFieldInputRestService extends AbstractRestService {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	@GET
	@Path("/get/{date}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto get(@PathParam("date") DateParameter date, @PathParam("name") String name) {
		String result = "GET " + dateAsString(date.getDate()) + " Hello " + name;
		return new ItemDto(result);
	}

	@DELETE
	@Path("/delete/{date}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto delete(@PathParam("date") String date, @PathParam("name") String name) {
		String result = "DELETE " + date + " Hello " + name;
		return new ItemDto(result);
	}

	@PUT
	@Path("/put")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto put(User user) {
		String result = "PUT " + dateAsString(user.getDate()) + " Hello " + user.getName();
		return new ItemDto(result);
	}

	@HEAD
	@Path("/head")
	@Produces(MediaType.APPLICATION_JSON)
	public Response head(@QueryParam("name") String name, @QueryParam("date") DateParameter date) {
		String result = "HEAD " + dateAsString(date.getDate()) + " Hello " + name;
		return Response.ok().header("headresult", result).build();
	}

	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto post(User user) {
		String result = "POST JSON " + dateAsString(user.getDate()) + " Hello " + user.getName();
		return new ItemDto(result);
	}

	@POST
	@Path("/form")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto form(@FormParam("name") String name, @FormParam("date") DateParameter date) {
		String result = "POST FORM " + dateAsString(date.getDate()) + " Hello " + name;
		return new ItemDto(result);
	}

	private String dateAsString(Date date) {
		DateFormat format = new SimpleDateFormat(DATE_FORMAT);
		return date != null ? "(" + format.format(date) + ")" : "";
	}
	
}
