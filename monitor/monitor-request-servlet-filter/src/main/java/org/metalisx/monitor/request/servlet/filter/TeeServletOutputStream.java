package org.metalisx.monitor.request.servlet.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.TeeOutputStream;

/**
 * Replaces the Servlet output stream with Apache Commons IO TeeOutputStream to
 * write output to two stream.
 * 
 * @author Stefan.Oude.Nijhuis
 */
public class TeeServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream branch;

    private TeeOutputStream teeOutputStream;

    public TeeServletOutputStream(OutputStream outputStream) {
        branch = new ByteArrayOutputStream();
        teeOutputStream = new TeeOutputStream(outputStream, branch);
    }

    public byte[] getOutputStreamAsByteArray() {
        return branch.toByteArray();
    }

    @Override
    public void write(int b) throws IOException {
        teeOutputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        teeOutputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        teeOutputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        teeOutputStream.flush();
    }

    @Override
    public void close() throws IOException {
        teeOutputStream.close();
    }

    @Override
    public boolean isReady() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new RuntimeException("Not yet implemented");
    }

}
