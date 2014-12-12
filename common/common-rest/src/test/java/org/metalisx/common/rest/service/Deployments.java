package org.metalisx.common.rest.service;

import java.io.IOException;
import java.util.zip.ZipException;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.metalisx.common.domain.initialize.InitializeDatabase;
import org.metalisx.common.domain.model.User;
import org.metalisx.common.rest.application.RestApplication;
import org.metalisx.common.rest.converter.DataTableFilterDtoToQueryLimitConverter;
import org.metalisx.common.rest.converter.DataTableFilterDtoToQueryOrderByConverter;
import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.ItemsDto;
import org.metalisx.common.rest.dto.MessageDto;
import org.metalisx.common.rest.dto.MessageLevel;
import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.common.rest.dto.entity.ColumnDto;
import org.metalisx.common.rest.dto.entity.EntitiesDto;
import org.metalisx.common.rest.dto.entity.EntityDto;
import org.metalisx.common.rest.dto.entity.EntityFieldDto;
import org.metalisx.common.rest.dto.entity.EntityMetadataDto;
import org.metalisx.common.rest.dto.entity.PageMetadataDto;
import org.metalisx.common.rest.metadata.EntityFieldTypeMapper;
import org.metalisx.common.rest.metadata.MetadataProvider;
import org.metalisx.common.rest.parameter.DateParameter;
import org.metalisx.common.rest.provider.RestJsonProvider;
import org.metalisx.common.rest.utils.RestServiceUtils;

public class Deployments {

	public static WebArchive createDeployment() throws ZipException, IOException {
		PomEquippedResolveStage resolverStage = Maven.resolver().loadPomFromFile("pom.xml");
		return ShrinkWrap
		        .create(WebArchive.class, "crudRestService.war")
		        .addAsLibraries(resolverStage.resolve("org.metalisx:common-domain").withTransitivity().asFile())
		        .addAsLibraries(resolverStage.resolve("org.metalisx:common-gson").withTransitivity().asFile())
		        .addAsLibraries(resolverStage.resolve("ch.qos.logback:logback-classic").withTransitivity().asFile())
		        .addAsLibraries(resolverStage.resolve("com.google.code.gson:gson").withTransitivity().asFile())
		        .addAsLibraries(resolverStage.resolve("org.apache.httpcomponents:httpclient").withTransitivity().asFile())
		        .addClasses(DataTableFilterDtoToQueryLimitConverter.class,
		        		DataTableFilterDtoToQueryOrderByConverter.class)
		        .addClasses(ItemDto.class, ItemsDto.class, MessageDto.class, MessageLevel.class,
		        		MessagesDto.class)
		        .addClasses(ColumnDto.class, EntitiesDto.class, EntityDto.class, EntityFieldDto.class, 
		        		EntityMetadataDto.class, PageMetadataDto.class)
		        .addClasses(EntityFieldTypeMapper.class, MetadataProvider.class)
		        .addClasses(DateParameter.class)
		        .addClasses(RestJsonProvider.class)
		        .addClasses(AbstractEntityRestService.class, RestException.class)
		        .addClasses(RestServiceUtils.class)
		        // test classes
		        .addClasses(InitializeDatabase.class, User.class, RestApplication.class,  CrudRestService.class)
		        .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
		        .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
		        .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
	}

}
