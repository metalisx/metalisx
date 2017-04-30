package org.metalisx.common.cdi.interceptor;

import java.lang.reflect.Method;

import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;

import org.eclipse.sisu.Priority;
import org.metalisx.common.cdi.utils.CdiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log
@Interceptor
@Priority(2000)
public class LogInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

	@AroundInvoke
	public Object logInvocation(InvocationContext ctx) throws Exception {
		long startTime = 0;
		LOGGER.info("Annotation value = " + getValue(ctx));
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
		return "{thread: '" + Thread.currentThread().getId() + "'" + ", ms = " + ms + ", description = '"
				+ ctx.getTarget().getClass().getSimpleName() + "." + ctx.getMethod().getName() + "'" + ", startTime = "
				+ startTime + ", endTime = " + endTime + ", user = '" + user + "'" + ", class = '"
				+ ctx.getTarget().getClass().getName() + "'" + ", method = '" + ctx.getMethod().getName() + "'}";
	}

	/**
	 * The EJBContext is retrieved with a JNDI lookup and it cannot be inject in
	 * the interceptor because the context of the Log interceptor is not always
	 * in a EJB context.
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

	private String getContextInfo(InvocationContext ctx) {
		return "[class = " + ctx.getTarget().getClass().getName() + ", method = " + ctx.getMethod().getName() + "]";
	}

	/**
	 * This will return null because the Log annotation is not found when the
	 * extension adds it during runtime.
	 * 
	 * If we need a value it cannot be done with an annotation. The value needs
	 * to be retrieved through other means. For instance by injecting a bean
	 * providing the value which normally would be set as attribute on the
	 * annotation. Hopefully there will be a solution to this problem in a new
	 * release.
	 */
	private String getValue(InvocationContext ctx) {
		CdiUtils.log(ctx, LOGGER);
		String value = null;
		// Find the annotation on the target class down the super classes to the
		// Object class
		Class<?> clazz = ctx.getTarget().getClass();
		while (clazz != Object.class) {
			if (clazz.isAnnotationPresent(Log.class)) {
				value = clazz.getAnnotation(Log.class).value();
				break;
			}
			clazz = clazz.getSuperclass();
		}
		// Find the annotation on the method.
		if (value == null) {
			Method method = ctx.getMethod();
			if (method.isAnnotationPresent(Log.class)) {
				value = method.getAnnotation(Log.class).value();
			}
		}
		if (value == null) {
			String message = "Could not retrieve application code from annotation. " + getContextInfo(ctx);
			LOGGER.error(message);
			// throw new InformoreRuntimeException(message);
		}
		return value;
	}

}
