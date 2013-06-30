package org.metalisx.common.gson;

import org.metalisx.common.gson.annotation.GsonTransient;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * This strategy skips fields in objects which are annotated with @GsonTransient 
 * from serializing to the JSON object.
 */
public class GsonAnnotationExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getAnnotation(GsonTransient.class) != null);
    }

}
