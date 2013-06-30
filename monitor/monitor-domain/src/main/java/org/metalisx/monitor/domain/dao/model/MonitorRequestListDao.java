package org.metalisx.monitor.domain.dao.model;

import javax.inject.Named;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.monitor.domain.dao.MonitorGenericEntityDao;
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;
import org.metalisx.monitor.domain.query.MonitorRequestPagedQueryProvider;

@Named
public class MonitorRequestListDao extends MonitorGenericEntityDao<MonitorRequestList, Long> {

	public PageDto<MonitorRequestList> findPage(PageContextDto<MonitorRequestFilter> pageContextDto) {
		MonitorRequestPagedQueryProvider pagedQueryProvider = new MonitorRequestPagedQueryProvider(pageContextDto);
		return findPage(pagedQueryProvider);
	}

}
