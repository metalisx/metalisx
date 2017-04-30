package org.metalisx.common.cdi.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.interceptor.InvocationContext;

import org.slf4j.Logger;

public class CdiUtils {

	private CdiUtils() {
	}

	public static void log(InvocationContext ctx, Logger logger) {
		logger.info("Class");
		CdiUtils.log(ctx.getClass(), logger);
		logger.info("Target class");
		CdiUtils.log(ctx.getTarget().getClass(), logger);
		logger.info("Method");
		CdiUtils.log(ctx.getMethod(), logger);
		logger.info("Contextdata");
		log(ctx.getContextData(), logger);
		logger.info("Parameters");
		log(ctx.getParameters(), logger);
	}

	private static void log(Object[] objects, Logger logger) {
		for (int i=0; i < objects.length; i++) {
			logger.info("Parameter [" + (i+1) + "] : " + objects[i].toString());
		}
	}
	
	private static void log(Map<String, Object> map, Logger logger) {
		map.forEach(new BiConsumer<String, Object>() {
			@Override
			public void accept(String key, Object object) {
				logger.info("Map key : " + key);
				logger.info("Map object : " + object);
			}
		});
	}

	private static void log(Class<?> clazz, Logger logger) {
		while (clazz != Object.class) {
			logger.info("Class : " + clazz.getName());
			for (Annotation annotation : clazz.getAnnotations()) {
				logger.info("Class anotation : " + annotation.toString());
			}
			clazz = clazz.getSuperclass();
		}
	}

	private static void log(Method method, Logger logger) {
		for (Annotation annotation : method.getAnnotations()) {
			logger.info("Method anotation : " + annotation.toString());
		}
	}

}
