package org.metalisx.common.cdi.interceptor;

import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log
@Interceptor
public class LogInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

    @AroundInvoke
    public Object logInvocation(InvocationContext ctx) throws Exception {
        long startTime = 0;
        if (LOGGER.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
        }
        Object object = null;
        try {
            object = ctx.proceed();
        } finally {
            if (LOGGER.isInfoEnabled()) {
                long endTime = System.currentTimeMillis();
                LOGGER.info(getLogMessage((endTime - startTime), startTime, endTime, ctx));
            }
        }
        return object;
    }

    private String getLogMessage(long ms, long startTime, long endTime, InvocationContext ctx) throws Exception {
        String user = "";
        EJBContext ejbContext = getEjbContext();
        if (ejbContext != null && ejbContext.getCallerPrincipal() != null) {
            user = ejbContext.getCallerPrincipal().getName();
        } else {
            user = "<no EJB context>";
        }
        return "{thread: '" + Thread.currentThread().getId() + "'"
                + ", ms = " + ms
                + ", description = '" + ctx.getTarget().getClass().getSimpleName() + "." + ctx.getMethod().getName() + "'"
                + ", startTime = " + startTime
                + ", endTime = " + endTime
                + ", user = '" + user + "'"
                + ", class = '" + ctx.getTarget().getClass().getName() + "'"
                + ", method = '" + ctx.getMethod().getName() + "'}";
    }

    /**
     * The EJBContext is retrieved with a JNDI lookup and it cannot be inject in the interceptor because the context of the Log interceptor
     * is not always in a EJB context.
     *
     * @return The EJBContext if the lookup is successful otherwise null.
     * @throws Exception
     */
    private EJBContext getEjbContext() throws Exception {
        InitialContext initialContext = new InitialContext();
        Object object = initialContext.lookup("java:comp/EJBContext");
        if (object == null) {
            return null;
        } else {
            return (EJBContext) object;
        }
    }
    
}
