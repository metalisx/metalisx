package org.metalisx.monitor.profiler.servlet.filter;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.metalisx.monitor.profiler.servlet.filter.ProfilerFilter;
import org.metalisx.monitor.profiler.servlet.filter.ProfilerFilterContext;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
                "pom.xml");
        return ShrinkWrap.create(WebArchive.class, "ProfilerFilterTest.war")
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-context").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-context-request").resolveAsFiles())
                .addClasses(ProfilerFilter.class, ProfilerFilterContext.class, ProfilerFilterTest.class, TestServlet.class)
                .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
                .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
