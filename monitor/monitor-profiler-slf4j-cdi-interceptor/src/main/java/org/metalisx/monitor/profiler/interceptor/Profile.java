package org.metalisx.monitor.profiler.interceptor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Target({TYPE })
@Retention(RUNTIME)
@Inherited
public @interface Profile {
}
