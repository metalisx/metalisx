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
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorSession;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;
import org.metalisx.monitor.domain.service.MonitorRequestService;

/**
 * Test case for {@link MonitorRequest}.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
@RunWith(Arquillian.class)
public class MonitorRequestServiceTest extends TransactionRollbackTest {

	@EJB
	private MonitorRequestService monitorRequestService;

	public MonitorRequestServiceTest() {
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		return Deployments.createDeployment();
	}

	@Test
	public void testPersistRequestAndFindRequestById() {
		MonitorRequest monitorRequest = new MonitorRequest();
		MonitorSession monitorSession = new MonitorSession();
		monitorSession.setRequestedSessionId("123");
		monitorRequest.setSession(monitorSession);
		monitorRequest = monitorRequestService.persist(monitorRequest);
		assertNotNull(monitorRequest.getId());

		MonitorRequest foundMonitorRequest = monitorRequestService.findById(monitorRequest.getId());
		assertNotNull("Object not found when using findById.", foundMonitorRequest);
	}

	@Test
	public void testFindRequestsByFilter() {
		Date time = new Date();
		MonitorRequest monitorRequest = new MonitorRequest();
		MonitorSession monitorSession = new MonitorSession();
		monitorSession.setRequestedSessionId("123");
		monitorRequest.setSession(monitorSession);
		monitorRequest.setStartTime(time);
		monitorRequest.setEndTime(time);
		monitorRequest.setRequestId("456");
		monitorRequest.setUrl("http://localhost/monitor");
		monitorRequest = monitorRequestService.persist(monitorRequest);
		assertNotNull(monitorRequest.getId());

		MonitorRequestFilter monitorRequestFilter = new MonitorRequestFilter();
		PageContextDto<MonitorRequestFilter> pageContextDto = new PageContextDto<MonitorRequestFilter>();
		pageContextDto.setFilter(monitorRequestFilter);

		PageDto<MonitorRequestList> pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record not found when using empty filter.", 1, pageDto.getItems().size());

		monitorRequestFilter.setSessionId("2");
		monitorRequestFilter.setRequestId("4");
		monitorRequestFilter.setUrl("http://localhost/monitor");
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record not found when all fields filled, startDate = null and endDate = null.", 1, pageDto
		        .getItems().size());

		monitorRequestFilter.setStartDate(time);
		monitorRequestFilter.setEndDate(time);
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = time and endDate = time.", 1, pageDto.getItems().size());

		monitorRequestFilter.setStartDate(time);
		monitorRequestFilter.setEndDate(null);
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = time and endDate = null.", 1, pageDto.getItems().size());

		monitorRequestFilter.setStartDate(null);
		monitorRequestFilter.setEndDate(time);
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record not found when using startDate = null and endDate = time.", 1, pageDto.getItems().size());

		monitorRequestFilter.setStartDate(new Date(time.getTime() + 1));
		monitorRequestFilter.setEndDate(null);
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record found when using startDate = time + 1ms and endDate = null.", 0, pageDto.getItems().size());

		monitorRequestFilter.setStartDate(null);
		monitorRequestFilter.setEndDate(new Date(time.getTime() - 1));
		pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record found when using startDate = null and endDate = time - 1ms.", 0, pageDto.getItems().size());
	}

	@Test
	public void testFindRequestsByIdWithoutSession() {
		MonitorRequest monitorRequest = new MonitorRequest();
		monitorRequest = monitorRequestService.persist(monitorRequest);
		assertNotNull(monitorRequest.getId());

		MonitorRequestFilter monitorRequestFilter = new MonitorRequestFilter();
		monitorRequestFilter.setSessionId("888");
		monitorRequestFilter.setRequestId("999");

		PageContextDto<MonitorRequestFilter> pageContextDto = new PageContextDto<MonitorRequestFilter>();
		pageContextDto.setFilter(monitorRequestFilter);

		PageDto<MonitorRequestList> pageDto = monitorRequestService.findPage(pageContextDto);
		assertEquals("Record found when using session id on a reqeust without a session.", 0, pageDto.getItems().size());
	}

}
