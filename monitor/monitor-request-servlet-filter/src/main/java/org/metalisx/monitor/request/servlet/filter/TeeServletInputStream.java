package org.metalisx.monitor.request.servlet.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import org.metalisx.utils.HttpUtils;


/**
 * Replaces the Servlet input stream with a version which splits the stream so
 * it can be read twice.
 * 
 * @author Stefan.Oude.Nijhuis
 */
public class TeeServletInputStream extends ServletInputStream {

    private byte[] buffer;

    private ByteArrayInputStream byteArrayInputStream;

    public TeeServletInputStream(InputStream inputStream) {
        try {
            byte[] buffer2 = HttpUtils.toByteArray(inputStream);
            buffer = new byte[buffer2.length];
            System.arraycopy(buffer2, 0, buffer, 0, buffer2.length);
            byteArrayInputStream = new ByteArrayInputStream(buffer2);
        } catch (IOException e) {
            throw new IllegalStateException("Making a copy of the inputstream failed.");
        }
    }

    public byte[] getInputStreamAsByteArray() {
        return buffer;
    }

    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return byteArrayInputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return byteArrayInputStream.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        try {
            byteArrayInputStream.close();
        } catch (IOException e) {
            super.close();
        }
    }

    @Override
    public boolean isFinished() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean isReady() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new RuntimeException("Not yet implemented");
    }


}
