package org.metalisx.monitor.web.it;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.metalisx.monitor.web.it.database.InitializeTestDatabase;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() throws ZipException, IOException {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
                "pom.xml");
        File[] file = resolver.artifact("org.metalisx:common-web-resources:war").resolveAsFiles();
        ZipFile zipFile = new ZipFile(file[0]);
        return ShrinkWrap
                .create(WebArchive.class, "monitor.war")
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp").as(GenericArchive.class), "/")
                .merge(ShrinkWrap.create(ZipImporter.class, "common-web-resources.war")
                        .importFrom(zipFile)
                        .as(WebArchive.class), "/")
                .addAsLibraries(resolver.artifact("org.metalisx:common-domain").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:common-gson").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:common-rest").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-domain").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.metalisx:monitor-context-slf4j-mdc").resolveAsFiles())
                .addAsLibraries(
                        resolver.artifact("org.metalisx:monitor-profiler-slf4j-cdi-interceptor")
                                .resolveAsFiles())
                .addAsLibraries(
                        resolver.artifact("org.metalisx:monitor-profiler-slf4j-servlet-filter")
                                .resolveAsFiles())
                .addAsLibraries(
                        resolver.artifact("org.metalisx:monitor-request-servlet-filter").resolveAsFiles())
                .addAsLibraries(resolver.artifact("ch.qos.logback:logback-classic").resolveAsFiles())
                .addAsLibraries(resolver.artifact("commons-io:commons-io").resolveAsFiles())
                .addAsLibraries(resolver.artifact("com.google.code.gson:gson").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.tika:tika-core").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.ccil.cowan.tagsoup:tagsoup").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.httpcomponents:httpclient").resolveAsFiles())
                .addAsLibraries(resolver.artifact("org.apache.httpcomponents:httpmime").resolveAsFiles())
                .addPackages(true, "org.metalisx.monitor.file")
                .addPackages(true, "org.metalisx.monitor.web.application")
                .addPackages(true, "org.metalisx.monitor.web.producer")
                .addPackages(true, "org.metalisx.monitor.web.service")
                .addPackages(true, "org.metalisx.monitor.web.startup")
                .addPackages(true, "org.metalisx.monitor.web.utils")
                // Unit test
                .addClass(InitializeTestDatabase.class);
    }

}
