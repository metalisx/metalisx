package org.metalisx.monitor.web.startup;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.metalisx.monitor.profiler.interceptor.ProfilerInterceptorContext;
import org.metalisx.monitor.profiler.servlet.filter.ProfilerFilterContext;
import org.metalisx.monitor.request.servlet.filter.RequestFilterContext;
import org.metalisx.monitor.web.producer.property.Value;

@Singleton
@Startup
@DependsOn("MonitorDatabase")
public class MonitorStartup {

	@Inject
	@Value("MONITOR_WEB_APPLICATION_DISABLE_REQUEST_FILTER")
	private boolean disableRequestFilter;

	@Inject
	@Value("MONITOR_WEB_APPLICATION_DISABLE_PROFILER_FILTER")
	private boolean disableProfilerFilter;

	@Inject
	@Value("MONITOR_WEB_APPLICATION_DISABLE_PROFILER_INTERCEPTOR")
	private boolean disableProfilerInterceptor;

    @Inject
    private RequestFilterContext requestFilterContext;

    @Inject
    private ProfilerFilterContext profilerFilterContext;

    @Inject
    ProfilerInterceptorContext profilerInterceptorContext;

    public MonitorStartup() {
    }

    @PostConstruct
    public void postConstruct() {
        requestFilterContext.setDisableFilter(disableRequestFilter);
        profilerFilterContext.setDisableFilter(disableProfilerFilter);
        profilerInterceptorContext.setDisableInterceptor(disableProfilerInterceptor);
    }

}
