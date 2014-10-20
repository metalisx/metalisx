package org.metalisx.monitor.profiler.servlet.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.metalisx.monitor.context.request.MonitorContextRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter for writing monitor log information of the request to the log file.
 * 
 * @author Stefan Oude Nijhuis
 * 
 */
public class ProfilerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ProfilerFilter.class);

    private static final String UTF8 = "UTF-8";
    		
    @Inject
    private ProfilerFilterContext profilerFilterContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
    	InterfaceMonitorContext monitorContext = MonitorContextRequestUtils.initMonitorContext(ProfilerFilter.class.getName(), request);
        try {
            if (!profilerFilterContext.isDisableFilter()) {
                String url = "";
                if (request instanceof HttpServletRequest) {
                	HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                    url = httpServletRequest.getRequestURL().toString();
                    if (httpServletRequest.getQueryString() != null) {
                    	url = url + "?" + httpServletRequest.getQueryString();
                    }
                    url =  URLDecoder.decode(url, UTF8);
                }
                Date startDate = new Date();
                monitorContext.increaseDepth();
                chain.doFilter(request, response);
                monitorContext.decreaseDepth();
                logger.info(monitorContext.profileFormat(url, (new Date()).getTime() - startDate.getTime()));
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            MonitorContextFactory.clear(ProfilerFilter.class.getName());
        }
    }

}
