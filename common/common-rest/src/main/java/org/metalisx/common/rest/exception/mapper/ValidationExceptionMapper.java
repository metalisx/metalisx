package org.metalisx.common.rest.exception.mapper;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.metalisx.common.rest.dto.MessagesDto;

/**
 * Provider to handle the Bean Validation exceptions.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	public Response toResponse(ConstraintViolationException exception) {
		return Response.status(Response.Status.OK)
		        .entity((new MessagesDto()).addError(exception.getMessage())).build();
	}

}