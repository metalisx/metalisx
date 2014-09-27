package org.metalisx.monitor.context.request;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
		MavenResolverSystem resolver = Maven.resolver();
		return ShrinkWrap.create(WebArchive.class, "MonitorRequestUtilsTest.war")
		        .addAsLibraries(resolver.resolve("org.metalisx:monitor-context").withTransitivity().asFile())
		        .addClasses(MonitorContextRequestUtils.class, MonitorContextRequestUtilsTest.class, TestServlet.class)
		        .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
		        .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
