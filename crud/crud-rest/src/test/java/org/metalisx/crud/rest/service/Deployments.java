package org.metalisx.crud.rest.service;

import java.io.IOException;
import java.util.zip.ZipException;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.metalisx.common.domain.initialize.InitializeDatabase;
import org.metalisx.common.domain.model.User;
import org.metalisx.common.rest.application.RestApplication;
import org.metalisx.common.rest.dto.entity.ColumnDto;
import org.metalisx.common.rest.dto.entity.EntitiesDto;
import org.metalisx.common.rest.dto.entity.EntityDto;
import org.metalisx.common.rest.dto.entity.EntityFieldDto;
import org.metalisx.common.rest.dto.entity.EntityMetadataDto;
import org.metalisx.common.rest.dto.entity.PageMetadataDto;
import org.metalisx.common.rest.metadata.EntityFieldTypeMapper;
import org.metalisx.common.rest.metadata.MetadataProvider;
import org.metalisx.common.rest.service.AbstractRestService;

public class Deployments {

	public static WebArchive createDeployment() throws ZipException, IOException {
		MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
		        "pom.xml");
		return ShrinkWrap
		        .create(WebArchive.class, "crudRestService.war")
		        .addAsLibraries(resolver.artifact("org.metalisx:common-domain").resolveAsFiles())
		        .addAsLibraries(resolver.artifact("org.metalisx:common-gson").resolveAsFiles())
		        .addAsLibraries(resolver.artifact("org.metalisx:common-rest").resolveAsFiles())
		        .addAsLibraries(resolver.artifact("ch.qos.logback:logback-classic").resolveAsFiles())
		        .addAsLibraries(resolver.artifact("com.google.code.gson:gson").resolveAsFiles())
		        .addAsLibraries(resolver.artifact("org.apache.httpcomponents:httpclient").resolveAsFiles())
		        .addClasses(ColumnDto.class, PageMetadataDto.class, EntitiesDto.class, EntityDto.class,
		                EntityFieldDto.class, EntityMetadataDto.class)
		        .addClasses(EntityFieldTypeMapper.class, MetadataProvider.class)
		        .addClasses(AbstractRestService.class)
		        // test classes
		        .addClasses(InitializeDatabase.class, User.class, RestApplication.class,  RestService.class)
		        .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
		        .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
		        .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
	}

}
