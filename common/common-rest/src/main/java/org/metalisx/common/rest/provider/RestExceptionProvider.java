package org.metalisx.common.rest.provider;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.metalisx.common.rest.dto.MessagesDto;

@Provider
public class RestExceptionProvider implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		MessagesDto messagesDto;
		Throwable rootCause = getRootCause(exception);
		if (rootCause instanceof ConstraintViolationException) {
			messagesDto = constraintViolationsToMessagesDto((ConstraintViolationException) rootCause);
		} else if (exception instanceof PersistenceException) {
			messagesDto = persistenceExceptionToMessagesDto((PersistenceException) exception);
		} else {
			messagesDto = exceptionToMessagesDto(exception);
		}
		return Response.ok().type(MediaType.APPLICATION_JSON)
				.entity(messagesDto).build();
	}

	private MessagesDto constraintViolationsToMessagesDto(ConstraintViolationException constraintViolationException) {
		MessagesDto messagesDto = new MessagesDto();
		Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iterator = violations.iterator();
		while (iterator.hasNext()) {
			ConstraintViolation<?> constraintViolation = iterator.next();
			messagesDto.addError(constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage());
		}
		return messagesDto;
	}

	private MessagesDto persistenceExceptionToMessagesDto(PersistenceException persistenceException) {
		MessagesDto messagesDto = new MessagesDto();
		messagesDto.addError(getRootCause(persistenceException).getMessage());
		return messagesDto;
	}
	
	private MessagesDto exceptionToMessagesDto(Exception exception) {
		MessagesDto messagesDto = new MessagesDto();
		messagesDto.addError(getRootCause(exception).getMessage(), getStackTrace(exception));
		return messagesDto;
	}
	
	private Throwable getRootCause(Throwable throwable) {
		if (throwable.getCause() != null) {
			return throwable.getCause();
		}
		return throwable;
	}
	
	private String getStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
		throwable.printStackTrace(printWriter);
		return stringWriter.toString();
	}

}
