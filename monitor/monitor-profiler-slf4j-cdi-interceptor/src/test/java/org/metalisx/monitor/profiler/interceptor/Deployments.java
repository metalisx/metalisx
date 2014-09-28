package org.metalisx.monitor.profiler.interceptor;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.metalisx.monitor.context.MonitorContextFactory;

public class Deployments {
	
	private Deployments() {
	}
	
    public static JavaArchive createDeployment() {
        return ShrinkWrap
                .create(JavaArchive.class, "profilerInterceptorTest.jar")
                .addPackage(MonitorContextFactory.class.getPackage())
                .addClasses(Profile.class, ProfilerInterceptor.class, ProfilerInterceptorContext.class,
                        ProfiledTestClass.class)
                .addAsManifestResource("META-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
