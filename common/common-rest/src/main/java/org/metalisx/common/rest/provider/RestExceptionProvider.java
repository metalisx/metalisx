package org.metalisx.common.rest.provider;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.metalisx.common.rest.dto.MessagesDto;

@Provider
public class RestExceptionProvider implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		MessagesDto messagesDto = new MessagesDto();
		if (exception instanceof PersistenceException) {
			messagesDto.addError(getMessage(exception));
		} else {
			messagesDto.addError(getMessage(exception), getStackTrace(exception));
		}
		return Response.ok().type(MediaType.APPLICATION_JSON).entity(messagesDto).build();
	}
	
	private String getMessage(Throwable throwable) {
		if (throwable.getCause() != null) {
			return getMessage(throwable.getCause());
		}
		return throwable.getMessage();
	}
	
	private String getStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
		throwable.printStackTrace(printWriter);
		return stringWriter.toString();
	}
	
}
