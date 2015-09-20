package org.metalisx.monitor.web.it;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.metalisx.monitor.web.it.database.InitializeTestDatabase;

public class Deployments {
	
	private Deployments() {
	}

	
	private static File[] addFiles(File[] files, File[] filesToAdd) {
        for (File f : filesToAdd) {
        	if (!containsFile(f, files)) {
        		files = ArrayUtils.add(files,  f);
        	}
        }
        return files;
	}
	
	/**
	 * Method to check if a artifact is already added to the list of files.
	 * This is necessary because the projects jars are stored in a temp location
	 * with a number as version and pulled in from that location instead of 
	 * the jars from the maven repository. 
	 */
	private static boolean containsFile(File absoluteFile, File[] files) {
		String v = absoluteFile.getAbsolutePath().substring(0, absoluteFile.getAbsolutePath().lastIndexOf('-') + 1);
		for (File f : files) {
			if (f.getAbsoluteFile().getAbsolutePath().startsWith(v)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *  By using the addAsLibraries method on ShrinkWrap the project artifact are added with a 
	 *  number instead of the actual SNAPHOT version. This will result in loading of duplicate 
	 *  classes, which will result in exceptions of duplicate beans. To prevent this we add the
	 *  project modules to an array list and add this array list with addAsLibraries. This 
	 *  seems to work.
	 *  Other options did not work:
	 *   - import all artifact from the project:
	 *      return ShrinkWrap.create(MavenImporter.class, "testapplication.war")
   	 *			.loadPomFromFile("pom.xml").importBuildOutput().as(WebArchive.class);
   	 *   - add the runtime and test dependencies:
   	 *      ShrinkWrap.create(WebArchive.class, "testapplication.war").
   	 *          addAsLibraries(resolverStage.importRuntimeAndTestDependencies().resolve().withTransitivity().asFile())
	 */
    public static WebArchive createDeployment() throws ZipException, IOException {
		PomEquippedResolveStage resolverStage = Maven.resolver().loadPomFromFile("pom.xml");
        File[] file = resolverStage.resolve("org.metalisx:common-web-resources:war:?").withoutTransitivity().asFile();
        ZipFile zipFile = new ZipFile(file[0]);
        for (File f : file) {
        	System.out.println(f.getAbsolutePath());
        }

        File[] commonGson = resolverStage.resolve("org.metalisx:common-gson").withTransitivity().asFile();
        File[] commonRest = resolverStage.resolve("org.metalisx:common-rest").withTransitivity().asFile();
        File[] monitorContext = resolverStage.resolve("org.metalisx:monitor-context").withTransitivity().asFile();
        File[] monitorContextSlf4jMdc = resolverStage.resolve("org.metalisx:monitor-context-slf4j-mdc").withTransitivity().asFile();
        File[] monitorProfilerSlf4jCdiInterceptor = resolverStage.resolve("org.metalisx:monitor-profiler-slf4j-cdi-interceptor").withTransitivity().asFile();
        File[] monitorProfilerSlf4jServletFilter = resolverStage.resolve("org.metalisx:monitor-profiler-slf4j-servlet-filter").withTransitivity().asFile();
        File[] monitorRequestServletFilter = resolverStage.resolve("org.metalisx:monitor-request-servlet-filter").withTransitivity().asFile();

        File[] files = new File[0];
        files = addFiles(files, commonGson);
        files = addFiles(files, commonRest);
        files = addFiles(files, monitorContext);
        files = addFiles(files, monitorContextSlf4jMdc);
        files = addFiles(files, monitorProfilerSlf4jCdiInterceptor);
        files = addFiles(files, monitorProfilerSlf4jServletFilter);
        files = addFiles(files, monitorRequestServletFilter);

        return ShrinkWrap
                .create(WebArchive.class, "testapplication.war")
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp").as(GenericArchive.class), "/")
                .merge(ShrinkWrap.create(ZipImporter.class, "common-web-resources.war")
                        .importFrom(zipFile)
                        .as(WebArchive.class), "/")
                .addAsLibraries(files)
                .addAsLibraries(resolverStage.resolve("ch.qos.logback:logback-classic").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("commons-io:commons-io").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("com.google.code.gson:gson").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("org.apache.tika:tika-core").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("org.ccil.cowan.tagsoup:tagsoup").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("org.apache.httpcomponents:httpclient").withTransitivity().asFile())
                .addAsLibraries(resolverStage.resolve("org.apache.httpcomponents:httpmime").withTransitivity().asFile())
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
