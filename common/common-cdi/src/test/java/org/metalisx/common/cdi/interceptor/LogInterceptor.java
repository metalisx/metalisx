package org.metalisx.common.cdi.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@Log
public class LogInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

	@AroundInvoke
	public Object around(InvocationContext ctx) throws Exception {
		LOGGER.info("LogInterceptor => interceptor method: around");
		LOGGER.info("LogInterceptor => method name: " + ctx.getMethod().getName());
		
		// Declaring
		LOGGER.info("LogInterceptor => declaring class: " + ctx.getMethod().getDeclaringClass().getName());
		// This works only if the method is called on the class having the annotation.
		// So a sub class having the annotation needs to override the method in the super class,
		// even if it just calls the super.
		if (ctx.getMethod().getDeclaringClass().isAnnotationPresent(Log.class)) {
			LOGGER.info("LogInterceptor => Log annotation is present");
			LOGGER.info("LogInterceptor => Log value = " + ctx.getMethod().getDeclaringClass().getAnnotation(Log.class).value());
		}
		
		// Target
		// This works if the annotation is set to the sub class
		LOGGER.info("LogInterceptor => target class: " + ctx.getTarget().getClass().getName());
		if (ctx.getTarget().getClass().isAnnotationPresent(Log.class)) {
			LOGGER.info("LogInterceptor => Log annotation is present");
			LOGGER.info("LogInterceptor => Log value = " + ctx.getTarget().getClass().getAnnotation(Log.class).value());
		}
		
		return ctx.proceed();
	}

}
