package org.metalisx.common.cdi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * Wrapper for adding annotations to an AnnotatedType in an CDI Extension.
 * Because the annotations list in the wrapped AnnotationType is immutable, a
 * new annotations list is created in this class.
 * 
 * @author Stefan Oude Nijhuis
 *
 * @param <T> The type
 */
public class AnnotatedTypeWrapper<T> implements AnnotatedType<T> {

	private final AnnotatedType<T> wrappedAnnotatedType;
	private final Set<Annotation> annotations;

	public AnnotatedTypeWrapper(AnnotatedType<T> wrappedAnnotatedType, Set<Annotation> annotations) {
		this.wrappedAnnotatedType = wrappedAnnotatedType;
		this.annotations = new HashSet<>(annotations);
	}

	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return wrappedAnnotatedType.getAnnotation(annotationType);
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
		for (Annotation annotation : annotations) {
			if (annotationType.isInstance(annotation)) {
				return true;
			}
		}
		return false;
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
}
