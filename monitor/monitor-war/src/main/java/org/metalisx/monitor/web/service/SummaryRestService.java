package org.metalisx.monitor.web.service;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.utils.DateUtils;
import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.service.AbstractRestService;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.profiler.interceptor.Profile;

/**
 * REST service for summary.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/summary")
public class SummaryRestService extends AbstractRestService {

    @EJB
	private MonitorLogService monitorLogService;

	@POST
	@Path("/filter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ItemDto filter(MonitorLogFilter monitorLogFilter) {
		ItemDto itemDto = new ItemDto(monitorLogFilter);
		return itemDto;
	}

	@POST
	@Path("/page")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PageDto<MonitorSummary> getPage(PageContextDto<MonitorLogFilter> pageContextDto) {
		DateUtils.processDateRange(pageContextDto.getFilter());
		return monitorLogService.findPageSummary(pageContextDto);
	}

}
