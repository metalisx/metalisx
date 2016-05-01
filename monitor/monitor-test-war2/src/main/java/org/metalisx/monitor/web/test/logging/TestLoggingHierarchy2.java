package org.metalisx.monitor.web.test.logging;

import javax.inject.Inject;

import org.metalisx.monitor.profiler.interceptor.Profile;


@Profile
public class TestLoggingHierarchy2 {

    @Inject
    private TestLoggingHierarchy3 testLoggingHierarchy3;

    public void executeAndWalkFurther() {
        testLoggingHierarchy3.executeGoBack();
        testLoggingHierarchy3.executeGoBack();
    }

    public void executeGoBack() {
    }

}
