package org.metalisx.monitor.request.servlet.filter;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.metalisx.monitor.request.servlet.filter.RequestFilter;
import org.metalisx.monitor.request.servlet.filter.RequestFilterContext;
import org.metalisx.monitor.request.servlet.filter.TeeHttpServletRequestWrapper;
import org.metalisx.monitor.request.servlet.filter.TeeHttpServletResponseWrapper;
import org.metalisx.monitor.request.servlet.filter.TeeServletInputStream;
import org.metalisx.monitor.request.servlet.filter.TeeServletOutputStream;
import org.metalisx.utils.HttpStatus;
import org.metalisx.utils.HttpUtils;
import org.metalisx.utils.PrettyPrintUtils;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
                "pom.xml");
        return ShrinkWrap
                .create(WebArchive.class, "RequestServletFilterTest.war")
                .addAsLibraries(resolver.artifact("org.metalisx:common-domain").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:common-gson").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-context").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-context-request").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-domain").resolveAsFiles())
                .addAsLibraries(resolver.artifact("commons-io:commons-io").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.tika:tika-core").resolveAsFiles())
                .addClasses(RequestFilter.class, RequestFilterContext.class, TeeHttpServletRequestWrapper.class,
                        TeeHttpServletResponseWrapper.class, TeeServletInputStream.class, TeeServletOutputStream.class,
                        HttpStatus.class, HttpUtils.class, PrettyPrintUtils.class, RequestFilterTest.class,
                        TestServlet.class).addAsManifestResource("test-ds.xml")
                .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
                .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
