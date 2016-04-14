package org.metalisx.monitor.request.servlet.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * The output stream of a Servlet response can only be read once and the read
 * action on the Servlet response output stream is already reserved by the
 * application server. So if we need to read what is in the output stream, we
 * need a second output stream to mirror the Servlet response output stream.
 * This class gives you this functionality by providing a byte array of the
 * response output stream.
 * 
 * Because there is no way to get the cookies from the response, this class
 * collects the cookies which are added by the addCookie method and a getCookies
 * method is added to return this list.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class TeeHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private TeeServletOutputStream teeServletOutputStream;

    private List<Cookie> cookies = new ArrayList<Cookie>();

    public TeeHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        try {
            teeServletOutputStream = new TeeServletOutputStream(super.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Could not branch ServletOutputStream.", e);
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        cookies.add(cookie);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(teeServletOutputStream));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return teeServletOutputStream;
    }

    public byte[] getOutputStreamAsByteArray() {
        return teeServletOutputStream.getOutputStreamAsByteArray();
    }

    public Cookie[] getCookies() {
        // Yuck.. but to return the same object as the getCookies from the
        // request.
        return (Cookie[]) cookies.toArray((Cookie[]) Array.newInstance(Cookie.class, cookies.size()));
    }

}