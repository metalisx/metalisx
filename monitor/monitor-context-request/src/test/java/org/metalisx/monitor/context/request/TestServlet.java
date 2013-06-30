package org.metalisx.monitor.context.request;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    private static final long serialVersionUID = 1L;

    public static final String RESPONSE_MESSAGE = "Request successful.";

    public static final String KEY = "theKey";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InterfaceMonitorContext monitorContext = MonitorContextRequestUtils.initMonitorContext(KEY, request);
        logger.info(monitorContext.format("A message"));
        logger.info(monitorContext.profileFormat("A message", 1));
        MonitorContextFactory.clear(KEY);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(RESPONSE_MESSAGE);
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        super.doGet(request, response);
    }

}
