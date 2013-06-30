package org.metalisx.monitor.web.test.fileupload;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.metalisx.common.gson.RestGsonConverter;
import org.metalisx.utils.HttpUtils;


/**
 * Servlet for uploading files.
 * 
 * The result of the upload is a JSON object. The servlet response is placed in
 * an iframe and unfortunately we can not change it's content type after the
 * iframe is created. So setting the response content type to application/json
 * does nothing and in case of Internet Explorer it would show a message how to
 * download the file. To solve the issue we set the content type to text/html
 * and create a html page with the JSON string result into the body. The
 * Javascript client can retrieve the body and convert it to an Javascript
 * object for processing.
 * 
 */
@WebServlet(urlPatterns = "/fileUpload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String FILE_LOCATION = System.getProperty("java.io.tmpdir");

    @Inject
    private RestGsonConverter restGsonConverter;

    public FileUploadServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        out.write("<html>");
        out.write("<body>");
        UploadInfo uploadInfo = new UploadInfo();
        for (Part part : request.getParts()) {
            String filename = HttpUtils.getFilename(part);
            if (filename != null) {
                UploadFile uploadFile = new UploadFile();
                uploadFile.setFilename(filename);
                uploadFile.setContentType(part.getContentType());
                long size = HttpUtils.writePart(FILE_LOCATION + filename, part);
                uploadFile.setSize(size);
                uploadInfo.addUploadFile(uploadFile);
            }
        }
        out.write(restGsonConverter.toJson(uploadInfo));
        out.write("</body>");
        out.write("</html>");
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        doGet(request, response);
    }

}
