package org.metalisx.common.cdi.extension;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

import org.metalisx.common.cdi.interceptor.Log;
import org.metalisx.common.cdi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension will add the Log annotation to all injectable CDI beans.
 * 
 * Down side of adding an annotation in an extension to the class is that the
 * annotation is only added to the metadata of the BeanManager. Reading the
 * class annotation in the interceptor will return a list of the original class
 * annotations without the annotation added during runtime in the extension.
 * This means that it is not possible to read the value of the annotation in the
 * interceptor, which is sometimes the purpose of adding an annotation.
 * 
 * This seems a bug and hopefully it is possible to retrieve added runtime
 * annotations in the interceptor.
 * 
 * There are ways to pass values to the interceptor. You could create a property
 * and getter method in the extension, then inject the extension in the
 * interceptor and call the getter method. But this will bind the extension to
 * the interceptor.
 * 
 * You could let the interceptor determine the value at runtime. But be aware of
 * performance penalties when it is determined over and over and it is resource
 * heavy.
 * 
 * A better way is to create a singleton bean for the interceptor/annotation
 * wherein you initialize a property, then inject the singleton bean in the
 * intercepter and read the value from the bean property. This will create an
 * extra class for every interceptor/annotation. In this case the bean should be
 * able to determine the value of the property because you can not inject beans
 * in extensions.
 *
 * @author Stefan.Oude.Nijhuis
 */
public class LogExtension implements Extension {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogExtension.class);

	private static final String ROOT_PACAKGE_NAME = "org.metalisx";

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		LOGGER.info("Starting scanning process for classes");
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		LOGGER.info("Done scanning process for classes");
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {
		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
		if (isPartOfRootPackage(annotatedType.getJavaClass())) {
			if (hasEjbAnnotation(annotatedType) || annotatedType.getJavaClass().equals(UserService.class)) {
				LOGGER.info("Adding Log annotation to " + annotatedType.getJavaClass().getName());
				Annotation logAnnotation = new LogAnnotationLiteral();
				AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(annotatedType, logAnnotation);
				processAnnotatedType.setAnnotatedType(wrapper);
			}
		}
	}

	private boolean isPartOfRootPackage(Class<?> clazz) {
		return clazz.getName().startsWith(ROOT_PACAKGE_NAME);
	}

	private boolean hasEjbAnnotation(AnnotatedType<?> annotatedType) {
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

	public static class LogAnnotationLiteral extends AnnotationLiteral<Log> implements Log {

		private static final long serialVersionUID = 1L;

		@Override
		public String value() {
			return "some value";
		}

	}

}
