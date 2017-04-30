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
//		Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {
//			private static final long serialVersionUID = 1L;
//		});
//		for (Bean<?> bean : beans) {
//			if (isPartOfRootPackage(bean.getBeanClass())) {
//				if (bean.getBeanClass().equals(UserService.class)) {
//					// There is no option to add an annotation to the
//					// annotations list of the bean. Altering the list with
//					// reflection is not clean and is for now not an option.
//					// Annotation logAnnotation = new LogAnnotationLiteral();
//					// Annotation[] annotations = bean.getBeanClass().getAnnotations();
//					System.out.println("Bean " + bean.getBeanClass().getName());
//				}
//			}
//		}
	}

	/**
	 * This method is only here to show that the Log annotation is added to the
	 * AnnotatedType and that the value of the annotation is the value as
	 * specified in the LogAnnotationLiteral.
	 */
	public <T> void processInjectionTarget(final @Observes ProcessInjectionTarget<T> processInjectionTarget) {
		AnnotatedType<T> annotatedType = processInjectionTarget.getAnnotatedType();
		if (annotatedType.isAnnotationPresent(Log.class)) {
			System.out.println("Injection target : log annotation detected on "
					+ processInjectionTarget.getAnnotatedType().getJavaClass());
			for (Annotation annotation : processInjectionTarget.getAnnotatedType().getAnnotations()) {
				System.out.println("Injection target annotation " + annotation.toString());
			}
		}
	}

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {
		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
		if (isPartOfRootPackage(annotatedType.getJavaClass())) {
			if (hasEjbAnnotation(annotatedType) || annotatedType.getJavaClass().equals(UserService.class)) {
				LOGGER.info("Adding Log annotation to " + annotatedType.getJavaClass().getName());
				Annotation logAnnotation = new LogAnnotationLiteral();
				AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(annotatedType, logAnnotation);
				processAnnotatedType.setAnnotatedType(wrapper);
				// Following code is only here to illustrate the Log annotation
				// is added to the AnnotatedType.
				for (Annotation annotation : processAnnotatedType.getAnnotatedType().getAnnotations()) {
					System.out.println("After annotation " + annotation.toString());
				}
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
