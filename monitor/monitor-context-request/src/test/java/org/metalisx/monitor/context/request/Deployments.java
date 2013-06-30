package org.metalisx.monitor.context.request;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
		        "pom.xml");
		return ShrinkWrap.create(WebArchive.class, "MonitorRequestUtilsTest.war")
		        .addAsLibraries(resolver.artifact("org.metalisx:monitor-context").resolveAsFiles())
		        .addClasses(MonitorContextRequestUtils.class, MonitorContextRequestUtilsTest.class, TestServlet.class)
		        .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
		        .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
