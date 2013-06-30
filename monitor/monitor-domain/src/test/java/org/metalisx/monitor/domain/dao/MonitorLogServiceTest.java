package org.metalisx.monitor.domain.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.test.domain.TransactionRollbackTest;
import org.metalisx.monitor.domain.Deployments;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.service.MonitorLogService;

@RunWith(Arquillian.class)
public class MonitorLogServiceTest extends TransactionRollbackTest {

	@EJB
	private MonitorLogService monitorLogService;

	public MonitorLogServiceTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return Deployments.createDeployment();
	}

	@Test
	public void testPersistAndFindById() {
		Date logDate = new Date();
		MonitorLog monitorLog = new MonitorLog();
		monitorLog.setLogDate(logDate);
		monitorLog.setLogLevel("INFO");
		monitorLog.setLogClass("some.class");
		monitorLog.setThread("A");
		monitorLog.setSessionId("123");
		monitorLog.setRequestId("456");
		monitorLog.setOrganization("Organization A");
		monitorLog.setUsername("User A");
		monitorLog.setMessage("Message A");
		monitorLog.setDuration(100L);
		monitorLog = monitorLogService.persist(monitorLog);
		assertNotNull(monitorLog.getId());

		MonitorLog foundMonitorLog = monitorLogService.findById(monitorLog.getId());
		assertNotNull("Object not found when using findById.", foundMonitorLog);
	}

	@Test
	public void testFindByFilter() {
		Date logDate = new Date();
		MonitorLog monitorLog = new MonitorLog();
		monitorLog.setLogDate(logDate);
		monitorLog.setLogLevel("INFO");
		monitorLog.setLogClass("some.class");
		monitorLog.setThread("A");
		monitorLog.setSessionId("123");
		monitorLog.setRequestId("456");
		monitorLog.setOrganization("Organization A");
		monitorLog.setUsername("User A");
		monitorLog.setMessage("Message A");
		monitorLog.setDuration(100L);
		monitorLog = monitorLogService.persist(monitorLog);
		assertNotNull(monitorLog.getId());

		MonitorLogFilter monitorLogFilter = new MonitorLogFilter();
		PageContextDto<MonitorLogFilter> pageContextDto = new PageContextDto<MonitorLogFilter>();
		pageContextDto.setFilter(monitorLogFilter);

		PageDto<MonitorLog> pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record not found when using empty filter.", 1, pageDto.getItems().size());

		monitorLogFilter.setMessage("Message A");
		monitorLogFilter.setOrganization("Organization A");
		monitorLogFilter.setUsername("User A");
		monitorLogFilter.setSessionId("2");
		monitorLogFilter.setRequestId("5");
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record not found when all fields filled, startDate = null and endDate = null.", 1, pageDto
		        .getItems().size());

		monitorLogFilter.setStartDate(logDate);
		monitorLogFilter.setEndDate(logDate);
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = logDate and endDate = logDate.", 1, pageDto.getItems()
		        .size());

		monitorLogFilter.setStartDate(logDate);
		monitorLogFilter.setEndDate(null);
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = logDate and endDate = null.", 1, pageDto.getItems()
		        .size());

		monitorLogFilter.setStartDate(null);
		monitorLogFilter.setEndDate(logDate);
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = null and endDate = logDate.", 1, pageDto.getItems()
		        .size());

		monitorLogFilter.setStartDate(new Date(logDate.getTime() + 1));
		monitorLogFilter.setEndDate(null);
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record found when using startDate = logDate + 1ms and endDate = null.", 0, pageDto.getItems()
		        .size());

		monitorLogFilter.setStartDate(null);
		monitorLogFilter.setEndDate(new Date(logDate.getTime() - 1));
		pageDto = monitorLogService.findPage(pageContextDto);
		assertEquals("Record found when using startDate = null and endDate = logDate - 1ms.", 0, pageDto.getItems()
		        .size());
	}

}
