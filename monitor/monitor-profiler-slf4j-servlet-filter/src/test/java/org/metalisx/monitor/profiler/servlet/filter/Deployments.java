package org.metalisx.monitor.profiler.servlet.filter;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
		PomEquippedResolveStage resolverStage = Maven.resolver().loadPomFromFile("pom.xml");
        return ShrinkWrap.create(WebArchive.class, "ProfilerFilterTest.war")
                .addAsLibraries(resolverStage.resolve("org.metalisx:monitor-context").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("org.metalisx:monitor-context-request").withTransitivity().asFile())
                .addClasses(ProfilerFilter.class, ProfilerFilterContext.class, ProfilerFilterTest.class, TestServlet.class)
                .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
                .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
