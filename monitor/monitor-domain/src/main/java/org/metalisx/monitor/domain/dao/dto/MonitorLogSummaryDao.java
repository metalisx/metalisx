package org.metalisx.monitor.domain.dao.dto;

import javax.inject.Named;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.monitor.domain.dao.MonitorDao;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.query.MonitorLogFilterToSimpleQuery;
import org.metalisx.monitor.domain.query.MonitorSummaryPagedQueryProvider;

@Named
public class MonitorLogSummaryDao extends MonitorDao {

	MonitorLogFilterToSimpleQuery monitorLogFilterToSimpleQuery = new MonitorLogFilterToSimpleQuery();
	
	public PageDto<MonitorSummary> findPage(PageContextDto<MonitorLogFilter> pageContextDto) {
		MonitorSummaryPagedQueryProvider pagedQueryProvider = new MonitorSummaryPagedQueryProvider(pageContextDto);
		return findPage(MonitorSummary.class, pagedQueryProvider);
	}

}
