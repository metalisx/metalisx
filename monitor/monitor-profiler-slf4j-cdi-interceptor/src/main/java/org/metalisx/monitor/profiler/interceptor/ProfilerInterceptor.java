package org.metalisx.monitor.profiler.interceptor;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This CDI interceptor generates log metrics in the log file with the time the
 * method or the @PostConstruct method takes to execute.
 * 
 * Make sure the {@link MonitorContextFactory#getCurrentInstance()} returns an
 * initialized object. Normally done with one of the servlet filters.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
@Interceptor
@Profile
public class ProfilerInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ProfilerInterceptor.class);

    @Inject
    ProfilerInterceptorContext profilerInterceptorContext;

    @PostConstruct
    public void profilePostConstruct(InvocationContext ctx) throws Throwable {
        if (!profilerInterceptorContext.isDisableInterceptor()) {
            Date startDate = null;
            InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
            if (monitorContext != null && !monitorContext.getDisableLogging()) {
                startDate = new Date();
                monitorContext.increaseDepth();
            }
            ctx.proceed();
            if (monitorContext != null && !monitorContext.getDisableLogging()) {
                monitorContext.decreaseDepth();
                logger.info(monitorContext.profileFormat(ctx.getTarget().getClass().getName() + ".@PostConstruct",
                        (new Date()).getTime() - startDate.getTime()));
            }
        } else {
            ctx.proceed();
        }
    }

    @AroundInvoke
    public Object profileConverter(InvocationContext ctx) throws Throwable {
        Object object;
        if (!profilerInterceptorContext.isDisableInterceptor()) {
            Date startDate = null;
            InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
            if (monitorContext != null && !monitorContext.getDisableLogging()) {
                startDate = new Date();
                monitorContext.increaseDepth();
            }
            object = ctx.proceed();
            if (monitorContext != null && !monitorContext.getDisableLogging()) {
                monitorContext.decreaseDepth();
                logger.info(monitorContext.profileFormat(ctx.getTarget().getClass().getName() + "."
                        + ctx.getMethod().getName(), (new Date()).getTime() - startDate.getTime()));
            }
        } else {
            object = ctx.proceed();
        }
        return object;
    }

}
