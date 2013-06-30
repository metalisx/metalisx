package org.metalisx.common.rest.service;

/**
 * Exception to be used when the REST service throws an exception which should
 * be returned as property exception in an JSON object.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class RestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RestException(String message) {
        super(message);
    }

}
