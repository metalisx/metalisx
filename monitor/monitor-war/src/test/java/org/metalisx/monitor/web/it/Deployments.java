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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.metalisx.monitor.web.it.database.InitializeTestDatabase;

public class Deployments {
	
	private Deployments() {
	}
	
    public static WebArchive createDeployment() throws ZipException, IOException {
        MavenResolverSystem resolver = Maven.resolver();
        File[] file = resolver.resolve("org.metalisx:common-web-resources:war").withoutTransitivity().asFile();
        ZipFile zipFile = new ZipFile(file[0]);
        return ShrinkWrap
                .create(WebArchive.class, "monitor.war")
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp").as(GenericArchive.class), "/")
                .merge(ShrinkWrap.create(ZipImporter.class, "common-web-resources.war")
                        .importFrom(zipFile)
                        .as(WebArchive.class), "/")
                .addAsLibraries(resolver.resolve("org.metalisx:common-domain").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:common-gson").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:common-rest").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:monitor-domain").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.metalisx:monitor-context-slf4j-mdc").withTransitivity().asFile())
                .addAsLibraries(
                        resolver.resolve("org.metalisx:monitor-profiler-slf4j-cdi-interceptor")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        resolver.resolve("org.metalisx:monitor-profiler-slf4j-servlet-filter")
                                .withTransitivity().asFile())
                .addAsLibraries(
                        resolver.resolve("org.metalisx:monitor-request-servlet-filter").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("ch.qos.logback:logback-classic").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("commons-io:commons-io").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("com.google.code.gson:gson").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.apache.tika:tika-core").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.ccil.cowan.tagsoup:tagsoup").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.apache.httpcomponents:httpclient").withTransitivity().asFile())
                .addAsLibraries(resolver.resolve("org.apache.httpcomponents:httpmime").withTransitivity().asFile())
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
