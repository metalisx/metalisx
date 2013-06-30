package org.metalisx.monitor.domain.interceptor;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


import org.metalisx.monitor.context.MonitorLogListenerMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This CDI interceptor generates a marker log statement to instruct the monitor
 * application to stop parsing the log statements until the method finishes.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
@Interceptor
@DisableLogListener
public class DisableLogListenerInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DisableLogListenerInterceptor.class);

    @AroundInvoke
    public Object disableLogListener(InvocationContext ctx) throws Throwable {
        logger.info(MonitorLogListenerMarker.getDisableMarker(Thread.currentThread().getName()));
        Object object = ctx.proceed();
        logger.info(MonitorLogListenerMarker.getEnableMarker(Thread.currentThread().getName()));
        return object;
    }

}
