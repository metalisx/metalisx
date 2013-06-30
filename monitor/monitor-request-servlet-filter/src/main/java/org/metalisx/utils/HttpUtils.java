package org.metalisx.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.mime.MediaType;

public class HttpUtils {

    private static final int BUFFER_LENGTH = 1024;

    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    private HttpUtils() {
    }

    /**
     * Returns if the <code>request</code> is a multipart request.
     * 
     * @param request The request.
     * @return Return true if it is a multipart request, otherwise it will
     *         return false.
     */
    public static boolean isMultipart(HttpServletRequest request) {
        if ("post".equals(request.getMethod().toLowerCase()) && isMultipart(request.getContentType())) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the <code>request</code> is a form request.
     * 
     * @param request The request.
     * @return Returns true if it is a from request, otherwise it will return
     *         false.
     */
    public static boolean isForm(HttpServletRequest request) {
        if ("post".equals(request.getMethod().toLowerCase()) && isForm(request.getContentType())) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the <code>contentType</code> is a multipart content type.
     * 
     * @param contentType The content type.
     * @return Return true if it is a multipart content type, otherwise it will
     *         return false.
     */
    public static boolean isMultipart(String contentType) {
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the <code>contentType</code> is a form content type.
     * 
     * @param contentType The content type.
     * @return Returns true if it is a from content type, otherwise it will return
     *         false.
     */
    public static boolean isForm(String contentType) {
        if (contentType != null && contentType.toLowerCase().startsWith("application/x-www-form-urlencoded")) {
            return true;
        }
        return false;
    }

    /**
     * Returns the filename in the <code>part</code>. Internet Explorer post the
     * filename with the clients directory, the clients directory is removed
     * before returning the filename.
     * 
     * @param part The part
     * @return The filename
     */
    public static String getFilename(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null && !"".equals(contentDisposition)) {
            for (String value : contentDisposition.split(";")) {
                if (value.trim().startsWith("filename")) {
                    String filename = value.substring(value.indexOf('=') + 1).trim().replace("\"", "");
                    int index = filename.lastIndexOf("\\");
                    if (index != -1) {
                        filename = filename.substring(index + 1);
                    }
                    return filename;
                }
            }
        }
        return null;
    }

    /**
     * Write the content of the <code>part</code> to the <code>filename</code>.
     * 
     * @param fileName Filename to write to.
     * @param part Part containing the content.
     * @return Returns the length of the content.
     * @throws IOException
     */
    public static long writePart(String fileName, Part part) throws IOException {
        InputStream inputStream = part.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(fileName);
        int bufferLength = 0;
        byte[] buffer = new byte[BUFFER_LENGTH];
        int c = -1;
        while ((c = inputStream.read(buffer, 0, BUFFER_LENGTH)) != -1) {
            outputStream.write(buffer, 0, c);
            bufferLength = bufferLength + c;
        }
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return bufferLength;
    }

    public static String toString(InputStream inputStream, String encoding) throws IOException {
        if (encoding == null) {
            encoding = DEFAULT_CHARACTER_ENCODING;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[BUFFER_LENGTH];
        int c = -1;
        while ((c = reader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, c);
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the input stream as byte array.
     * 
     * @param inputStream The input stream.
     * @return Returns a byte array containing the input stream contents.
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_LENGTH];
        int c = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((c = inputStream.read(buffer, 0, BUFFER_LENGTH)) != -1) {
            byteArrayOutputStream.write(buffer, 0, c);
        }
        byte[] result = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return result;
    }

    /**
     * Tika TextDetector is used to determine if the byte array contains text or
     * binary. The detect method returns a MediaType. If the type in the
     * MediaType instance is: - application the content is binary - text the
     * content is text The subtype of the MediaType instance contains the actual
     * content type. You can use the detect method of the Tika object to
     * retrieve the content type: Tika tika = new Tika(); String type =
     * tika.detect(response.getContent());
     * 
     */
    public static boolean isText(byte[] content) throws IOException {
        TextDetector textDetector = new TextDetector();
        InputStream inputStream = new ByteArrayInputStream(content);
        MediaType mediaType = textDetector.detect(inputStream, null);
        if ("text".equals(mediaType.getType())) {
            return true;
        }
        return false;
    }

}
