package org.metalisx.monitor.domain.query;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.query.PagedQueryProvider;
import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;

public class MonitorSummaryPagedQueryProvider extends PagedQueryProvider<MonitorSummary, MonitorLogFilter> {

	private static Map<String, String> columnNameMap = new HashMap<String, String>();

	MonitorLogFilterToSimpleQuery monitorLogFilterToSimpleQuery = new MonitorLogFilterToSimpleQuery();

	// The key is the value traversing between client and server, the object is
	// the actual name of the database column
	static {
		columnNameMap.put("message", "message");
		columnNameMap.put("count", "count(o)");
		columnNameMap.put("minDuration", "min(o.duration)");
		columnNameMap.put("maxDuration", "max(o.duration)");
		columnNameMap.put("averageDuration", "avg(o.duration)");
		columnNameMap.put("totalDuration", "sum(o.duration)");
	}

	public MonitorSummaryPagedQueryProvider(PageContextDto<MonitorLogFilter> pageContextDto) {
		super(pageContextDto);
		translateColumnNames(pageContextDto);
	}

	private SimpleQuery pageContextDtoToTypedQuery(PageContextDto<MonitorLogFilter> pageContextDto, boolean isCountQuery) {
		SimpleQuery simpleQuery = new SimpleQuery();
		if (isCountQuery) {
			simpleQuery.setSelect("count(distinct o.message)");
		} else {
			simpleQuery.setSelect("new " + MonitorSummary.class.getName() + "(o.message, count(o), min(o.logDate), "
			        + "max(o.logDate), min(o.duration), max(o.duration), avg(o.duration), sum(o.duration))");
		}
		simpleQuery.setFrom(MonitorLog.class.getSimpleName() + " o");
		monitorLogFilterToSimpleQuery.processFilter(simpleQuery, pageContextDto.getFilter());
		if (!isCountQuery) {
			simpleQuery.setOrderBy(pageContextDto.getOrderBy() == null ? "o.message" : pageContextDto.getOrderBy()
			        .getAsString());
			simpleQuery.setLimit(pageContextDto.getLimit());
			simpleQuery.setGroupBy("o.message");
		}
		return simpleQuery;
	}
	
	private void translateColumnNames(PageContextDto<MonitorLogFilter> pageContextDto) {
		if (pageContextDto.getOrderBy() != null) {
			String actualColumnName = columnNameMap.get(pageContextDto.getOrderBy().getColumnName());
			if (actualColumnName == null) {
				throw new IllegalStateException("Unknown column " + pageContextDto.getOrderBy().getColumnName());
			}
			pageContextDto.getOrderBy().setColumnName(actualColumnName);
		}
	}

	@Override
	public TypedQuery<MonitorSummary> toQuery(PageContextDto<MonitorLogFilter> pageContextDto,
	        EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, false).getTypedQuery(MonitorSummary.class, entityManager);
	}

	@Override
	public TypedQuery<Long> toCountQuery(PageContextDto<MonitorLogFilter> pageContextDto, EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, true).getTypedQuery(Long.class, entityManager);
	}

}
