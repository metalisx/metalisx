package org.metalisx.common.cdi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

import org.metalisx.common.cdi.interceptor.Log;

/**
 * Wrapper for adding an annotation to an AnnotatedType in an CDI Extension.
 * Because the annotations list in the wrapped AnnotationType is immutable, a
 * new annotations list is created in this class.
 *
 * @author Stefan Oude Nijhuis
 *
 * @param <T> The type
 */
public class AnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> wrappedAnnotatedType;
//	private final Annotation annotation;
	private final Set<Annotation> annotations = new HashSet<>();

	public <A extends Annotation> AnnotatedTypeWrapper(AnnotatedType<T> wrappedAnnotatedType, A annotation) {
		this.wrappedAnnotatedType = wrappedAnnotatedType;
//		this.annotation = annotation;
		addAnnotation(wrappedAnnotatedType, annotation);
	}

	/**
	 * This method will add the annotation or replace an existing annotation of the same type. The getAnnotations will be called multiple times,
	 * therefore it the new list is created once in the constructor.
	 * @param wrappedAnnotatedType The annotatedType
	 * @param annotation The annotation
	 */
	private <A extends Annotation> void addAnnotation(AnnotatedType<T> wrappedAnnotatedType, A annotation) {
		if (annotation != null) {
			boolean replaced = false;
			for (Annotation a : wrappedAnnotatedType.getAnnotations()) {
				System.out.println("addAnnotation " + a.toString());
				if (!Log.class.equals(a.annotationType())) {
					System.out.println("addAnnotation :: added " + a.toString());
					annotations.add(a);
				} else {
					// Replace
					System.out.println("addAnnotation :: replaced " + a.toString());
					annotations.add(annotation);
					replaced = true;
				}
			}
			if (replaced == false) {
				System.out.println("addAnnotation :: added LOG " + annotation.toString());
				annotations.add(annotation);
			}
		}
	}
	
	@Override
	public Set<AnnotatedConstructor<T>> getConstructors() {
		return wrappedAnnotatedType.getConstructors();
	}

	@Override
	public Set<AnnotatedField<? super T>> getFields() {
		return wrappedAnnotatedType.getFields();
	}

	@Override
	public Class<T> getJavaClass() {
		return wrappedAnnotatedType.getJavaClass();
	}

	@Override
	public Set<AnnotatedMethod<? super T>> getMethods() {
		return wrappedAnnotatedType.getMethods();
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annType) {
//		System.out.println("xxxxxxxxx " + annType.getName());
//		if (annotation.annotationType().equals(annType)) {
//			return (A) annotation;
//		} else {
			return wrappedAnnotatedType.getAnnotation(annType);
//		}
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	@Override
	public Type getBaseType() {
		return wrappedAnnotatedType.getBaseType();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return wrappedAnnotatedType.getTypeClosure();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
//		if (annotation.annotationType().equals(annotationType)) {
//			return true;
//		}
		return wrappedAnnotatedType.isAnnotationPresent(annotationType);
	}

}