package org.metalisx.monitor.request.servlet.filter;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
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
        MavenResolverSystem resolver = Maven.resolver();
        return ShrinkWrap
                .create(WebArchive.class, "RequestServletFilterTest.war")
                .addAsLibraries(resolver.resolve("org.metalisx:common-domain").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:common-gson").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:monitor-context").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:monitor-context-request").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:monitor-domain").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("commons-io:commons-io").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.apache.tika:tika-core").withTransitivity().asFile())
                .addClasses(RequestFilter.class, RequestFilterContext.class, TeeHttpServletRequestWrapper.class,
                        TeeHttpServletResponseWrapper.class, TeeServletInputStream.class, TeeServletOutputStream.class,
                        HttpStatus.class, HttpUtils.class, PrettyPrintUtils.class, RequestFilterTest.class,
                        TestServlet.class).addAsManifestResource("test-ds.xml")
                .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
                .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
