package org.metalisx.monitor.web.test.logging;

import javax.inject.Inject;

import org.metalisx.monitor.profiler.interceptor.Profile;


@Profile
public class TestLoggingHierarchy1 {

    @Inject
    private TestLoggingHierarchy2 testLoggingHierarchy2;

    public void executeAndWalkFurther() {
        testLoggingHierarchy2.executeGoBack();
        testLoggingHierarchy2.executeAndWalkFurther();
        testLoggingHierarchy2.executeAndWalkFurther();
        testLoggingHierarchy2.executeGoBack();
        testLoggingHierarchy2.executeAndWalkFurther();
        testLoggingHierarchy2.executeGoBack();
    }

    public void executeGoBack() {
    }

}
