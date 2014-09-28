package org.metalisx.monitor.domain;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.metalisx.common.test.domain.TransactionRollbackTest;
import org.metalisx.monitor.domain.dao.MonitorDao;
import org.metalisx.monitor.domain.dao.MonitorGenericEntityDao;
import org.metalisx.monitor.domain.dao.dto.MonitorLogOverviewItemDao;
import org.metalisx.monitor.domain.dao.dto.MonitorLogSummaryDao;
import org.metalisx.monitor.domain.dao.model.MonitorLogDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestCertificateDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestListDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestPartDao;
import org.metalisx.monitor.domain.dao.model.MonitorResponseDao;
import org.metalisx.monitor.domain.datasource.MonitorDataSource;
import org.metalisx.monitor.domain.datasource.MonitorDataSourceProducer;
import org.metalisx.monitor.domain.dto.MonitorOverviewItem;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.entitymanager.MonitorEntityManager;
import org.metalisx.monitor.domain.entitymanager.MonitorEntityManagerProducer;
import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorRequestCertificate;
import org.metalisx.monitor.domain.model.MonitorRequestCookie;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameter;
import org.metalisx.monitor.domain.model.MonitorRequestFormParameterValue;
import org.metalisx.monitor.domain.model.MonitorRequestHeader;
import org.metalisx.monitor.domain.model.MonitorRequestLocale;
import org.metalisx.monitor.domain.model.MonitorRequestPart;
import org.metalisx.monitor.domain.model.MonitorRequestPartHeader;
import org.metalisx.monitor.domain.model.MonitorRequestSecurity;
import org.metalisx.monitor.domain.model.MonitorResponse;
import org.metalisx.monitor.domain.model.MonitorResponseCookie;
import org.metalisx.monitor.domain.model.MonitorResponseHeader;
import org.metalisx.monitor.domain.model.MonitorSession;
import org.metalisx.monitor.domain.model.MonitorSetting;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;
import org.metalisx.monitor.domain.query.MonitorLogFilterToSimpleQuery;
import org.metalisx.monitor.domain.query.MonitorLogPagedQueryProvider;
import org.metalisx.monitor.domain.query.MonitorRequestPagedQueryProvider;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.domain.service.MonitorRequestService;
import org.metalisx.monitor.domain.startup.MonitorDatabase;

public class Deployments {

	private Deployments() {
	}
	
    public static WebArchive createDeployment() {
		PomEquippedResolveStage resolverStage = Maven.resolver().loadPomFromFile("pom.xml");
		return ShrinkWrap
		        .create(WebArchive.class, "test.war")
                .addAsLibraries(resolverStage.resolve("org.metalisx:common-domain").withTransitivity().asFile())
		        .addAsLibraries(resolverStage.resolve("org.liquibase:liquibase-core").withTransitivity().asFile())
		        .addClasses(
		        // Database
		        MonitorDatabase.class,
                // Filters
		        MonitorLogFilter.class, MonitorRequestFilter.class,
		        // Enum
		        MonitorSettingCode.class,
		        // Data source
		        MonitorDataSourceProducer.class, MonitorDataSource.class,
		        // Entity manager
		        MonitorEntityManagerProducer.class, MonitorEntityManager.class,
		        // Domain
		        MonitorLog.class,
                MonitorRequest.class, MonitorRequestCertificate.class,
                MonitorRequestCookie.class, MonitorRequestFormParameter.class,
                MonitorRequestFormParameterValue.class, MonitorRequestHeader.class, MonitorRequestList.class,
                MonitorRequestLocale.class, MonitorRequestPart.class, MonitorRequestPartHeader.class,
                MonitorResponse.class, MonitorResponseCookie.class, MonitorResponseHeader.class,
                MonitorRequestSecurity.class,
                MonitorSession.class, MonitorSetting.class,
		        // DTO's
		        MonitorOverviewItem.class, MonitorSummary.class,
		        // DAO's
		        MonitorGenericEntityDao.class, MonitorDao.class,
		        MonitorGenericEntityDao.class, MonitorLogDao.class, 
		        MonitorLogOverviewItemDao.class, MonitorLogSummaryDao.class,
		        MonitorRequestDao.class, MonitorRequestListDao.class, MonitorRequestPartDao.class,
                MonitorRequestCertificateDao.class, MonitorResponseDao.class,
		        // Query
		        MonitorLogFilterToSimpleQuery.class, MonitorLogPagedQueryProvider.class, 
                MonitorRequestPagedQueryProvider.class,
		        // Services
		        MonitorLogService.class, MonitorRequestService.class,
		        // Test
		        TransactionRollbackTest.class)
		        .addAsWebInfResource("db/db.changelog-master.xml",
		                ArchivePaths.create("classes/db/db.changelog-master.xml"))
		        .addAsWebInfResource("db/db.changelog-initial-ddl.xml",
		                ArchivePaths.create("classes/db/db.changelog-initial-ddl.xml"))
		        .addAsWebInfResource("db/db.changelog-initial-dml.xml",
		                ArchivePaths.create("classes/db/db.changelog-initial-dml.xml"))
		        .addAsWebInfResource("META-INF/persistence.xml", ArchivePaths.create("classes/META-INF/persistence.xml"))
		        .addAsWebInfResource("WEB-INF/test-web.xml", ArchivePaths.create("web.xml"))
		        .addAsWebInfResource("WEB-INF/test-beans.xml", ArchivePaths.create("beans.xml"));
    }

}
