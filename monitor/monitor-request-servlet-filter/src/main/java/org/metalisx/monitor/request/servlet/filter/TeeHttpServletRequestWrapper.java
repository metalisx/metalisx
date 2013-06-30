package org.metalisx.monitor.request.servlet.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;

import org.metalisx.utils.HttpUtils;


/**
 * The input stream of a Servlet request can only be read once and the read
 * action on the Servlet request input stream is already reserved for the
 * application. So if we need to read what is in the input stream, we need a
 * second input stream to mirror the Servlet request input stream. This class
 * gives you this functionality by providing a byte array of the request input
 * stream.
 * 
 * The getParts is a Servlet 3.0 specification implementation. The use of this
 * method will prevent apache commons fileupload to work. And vice versa. Now
 * the getParts is implemented in this filter, this means the multipart
 * functionality will only workt in a Servlet 3.0 environment.
 * 
 * The form parameters are retrieved before coping the request inputstream. This
 * is because the servlet implementation is reading the parameters from the
 * original request inputstream when calling the getParameters method. But at
 * that time the inputstream is already closed. This means we need to read the
 * form parameters here.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class TeeHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private TeeServletInputStream teeServletInputStream;

    private Collection<Part> parts;

    private Map<String, String[]> parameterMap;

    public TeeHttpServletRequestWrapper(HttpServletRequest request) throws IOException, ServletException {
        super(request);
        if (HttpUtils.isMultipart(request)) {
            parts = request.getParts();
        }
        if (HttpUtils.isForm(request)) {
            parameterMap = request.getParameterMap();
        }
        teeServletInputStream = new TeeServletInputStream(request.getInputStream());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return parts;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(teeServletInputStream));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return teeServletInputStream;
    }

    public byte[] getInputStreamAsByteArray() {
        return teeServletInputStream.getInputStreamAsByteArray();
    }

}