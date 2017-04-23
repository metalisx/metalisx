package org.metalisx.common.cdi.extension;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.metalisx.common.cdi.interceptor.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension will add the Log annotation to all injectable CDI beans.
 *
 * @author Stefan.Oude.Nijhuis
 */
public class LogExtension implements Extension {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogExtension.class);

	private static final String ROOT_PACAKGE_NAME = "org.metalisx";

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		LOGGER.info("Starting scanning process for classes which are extended with the Log annotation");
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {
		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
		if (isPartOfRootPackage(annotatedType.getJavaClass())) {
			if (isEjbAnnotation(annotatedType)) {
				LOGGER.info("Adding Log annotation to " + annotatedType.getJavaClass().getName());
				Annotation logAnnotation = new Annotation() {
					@Override
					public Class<? extends Annotation> annotationType() {
						return Log.class;
					}
				};
				AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(annotatedType,
						annotatedType.getAnnotations());
				wrapper.addAnnotation(logAnnotation);
				processAnnotatedType.setAnnotatedType(wrapper);	
			}
		}
	}

	private boolean isPartOfRootPackage(Class<?> clazz) {
		return clazz.getName().startsWith(ROOT_PACAKGE_NAME);
	}

	private boolean isEjbAnnotation(AnnotatedType<?> annotatedType) {
		Set<Annotation> annotations = annotatedType.getAnnotations();
		for (Annotation annotation : annotations) {
			Class<?> annotationType = annotation.annotationType();
			if (annotationType.equals(Stateless.class) || annotationType.equals(Stateful.class)
					|| annotationType.equals(Singleton.class) || annotationType.equals(MessageDriven.class)) {
				return true;
			}
		}
		return false;
	}

}
