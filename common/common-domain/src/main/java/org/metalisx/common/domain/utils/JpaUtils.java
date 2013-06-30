package org.metalisx.common.domain.utils;

import org.metalisx.common.domain.model.AbstractEntity;

/**
 * Helper class providing some helper methods for using JPA.
 * 
 * The isNullOrEmpty is added to keep the dependent jars limited. This means we
 * can, in case of unit testing with Arquillian, create a jar archive for
 * deployment instead of a web archive containing the required dependencies.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class JpaUtils {

	/**
	 * Returns true if <code>value</code> is null or empty.
	 * 
	 * @param value The value to test.
	 * @return true if the value is null or empty otherwise false.
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value == null || "".equals(value));
	}

	/**
	 * Adds %-sign before and after <code>value</code>.
	 * 
	 * @param value The value
	 * @return The value inclosed in %-signs
	 */
	public static String like(String value) {
		if (isNullOrEmpty(value)) {
			return "%";
		} else {
			return "%" + value + "%";
		}
	}

	/**
	 * Returns the runtime class of the string value in <code>className</code>.
	 * The class needs to extend the {@link AbstractEntity} class otherwise an
	 * exception is thrown.
	 * 
	 * @param className The fully qualified class name.
	 * @return The runtime class
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends AbstractEntity> toAbstractEntityClass(String className) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		Object o = Class.forName(className).newInstance();
		if (!(o instanceof AbstractEntity)) {
			throw new IllegalStateException(
			        "Class should extend the org.metalisx.common.domain.model.AbstractEntity class.");
		}
		return (Class<? extends AbstractEntity>) Class.forName(className).newInstance().getClass();
	}

	/**
	 * Returns the runtime class of the string value in <code>className</code>.
	 * 
	 * @param className The fully qualified class name.
	 * @return The runtime class
	 */
	public static Class<?> toClass(String className) throws ClassNotFoundException,
	        InstantiationException, IllegalAccessException {
		return (Class<?>) Class.forName(className).newInstance().getClass();
	}
	
}
