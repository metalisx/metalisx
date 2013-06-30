package org.metalisx.monitor.domain.query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.query.PagedQueryProvider;
import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;

public class MonitorLogPagedQueryProvider extends PagedQueryProvider<MonitorLog, MonitorLogFilter> {

	MonitorLogFilterToSimpleQuery monitorLogFilterToSimpleQuery = new MonitorLogFilterToSimpleQuery();
	
	public MonitorLogPagedQueryProvider(PageContextDto<MonitorLogFilter> pageContextDto) {
        super(pageContextDto);
    }

	private SimpleQuery pageContextDtoToTypedQuery(
	        PageContextDto<MonitorLogFilter> pageContextDto, boolean isCountQuery) {
		SimpleQuery simpleQuery = new SimpleQuery();
		if (isCountQuery) {
			simpleQuery.setSelect("count(o)");
		} else {
			simpleQuery.setSelect("o");
		}
		simpleQuery.setFrom(MonitorLog.class.getSimpleName() + " o");
		if (pageContextDto.getFilter() != null) {
			monitorLogFilterToSimpleQuery.processFilter(simpleQuery, pageContextDto.getFilter());
		}
		if (!isCountQuery) {
			simpleQuery.setOrderBy(pageContextDto.getOrderBy() == null ? "o.logDate" : pageContextDto.getOrderBy()
			        .getAsString());
			simpleQuery.setLimit(pageContextDto.getLimit());
		}
		return simpleQuery;
	}

	@Override
	public TypedQuery<MonitorLog> toQuery(PageContextDto<MonitorLogFilter> pageContextDto, EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, false).getTypedQuery(MonitorLog.class, entityManager);
	}

	@Override
	public TypedQuery<Long> toCountQuery(PageContextDto<MonitorLogFilter> pageContextDto, EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, true).getTypedQuery(Long.class, entityManager);
	}
	
}
