package org.metalisx.common.gson.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate properties in classes with this annotation to prevent them from
 * being serialized by the GSON converter (@link RestGsonConverter} to the JSON
 * object.
 * 
 * @author Stefan.Oude.Nijhuis
 */
@Retention(RUNTIME)
@Target({ FIELD })
public @interface GsonTransient {
}
