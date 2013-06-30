package org.metalisx.monitor.domain.query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.query.PagedQueryProvider;
import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.common.domain.utils.JpaUtils;
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.model.MonitorSession;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;

public class MonitorRequestPagedQueryProvider extends PagedQueryProvider<MonitorRequestList, MonitorRequestFilter> {

	public MonitorRequestPagedQueryProvider(PageContextDto<MonitorRequestFilter> pageContextDto) {
        super(pageContextDto);
    }

	private SimpleQuery pageContextDtoToTypedQuery(
	        PageContextDto<MonitorRequestFilter> pageContextDto, boolean isCountQuery) {
		SimpleQuery simpleQuery = new SimpleQuery();
		if (isCountQuery) {
			simpleQuery.setSelect("count(o)");
		} else {
			simpleQuery.setSelect("o");
		}
		simpleQuery.setFrom(MonitorRequestList.class.getSimpleName() + " o");
		processFilter(simpleQuery, pageContextDto.getFilter());
		if (!isCountQuery) {
			simpleQuery.setOrderBy(pageContextDto.getOrderBy() == null ? "o.startTime desc" : pageContextDto
			        .getOrderBy().getAsString());
			simpleQuery.setLimit(pageContextDto.getLimit());
		}
		return simpleQuery;
	}

	public void processFilter(SimpleQuery simpleQuery, MonitorRequestFilter monitorRequestFilter) {
		if (monitorRequestFilter != null) {
			if (!JpaUtils.isNullOrEmpty(monitorRequestFilter.getSessionId())) {
				simpleQuery.addFrom(", " + MonitorSession.class.getSimpleName() + " s");
				simpleQuery.addWhereWithAnd("o.session = s.id and s.requestedSessionId like :SESSIONID ");
				simpleQuery.addParameter("SESSIONID", JpaUtils.like(monitorRequestFilter.getSessionId()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorRequestFilter.getRequestId())) {
				simpleQuery.addWhereWithAnd("o.requestId like :REQUESTID");
				simpleQuery.addParameter("REQUESTID", JpaUtils.like(monitorRequestFilter.getRequestId()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorRequestFilter.getUrl())) {
				simpleQuery.addWhereWithAnd("o.url like :URL");
				simpleQuery.addParameter("URL", JpaUtils.like(monitorRequestFilter.getUrl()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorRequestFilter.getOrganization())) {
				simpleQuery.addWhereWithAnd("o.organization like :ORGANIZATION");
				simpleQuery.addParameter("ORGANIZATION", JpaUtils.like(monitorRequestFilter.getOrganization()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorRequestFilter.getUsername())) {
				simpleQuery.addWhereWithAnd("o.username like :USERNAME");
				simpleQuery.addParameter("USERNAME", JpaUtils.like(monitorRequestFilter.getUsername()));
			}
			if (monitorRequestFilter.getStartDate() != null) {
				simpleQuery.addWhereWithAnd("o.startTime >= :STARTDATE");
				simpleQuery.addParameter("STARTDATE", monitorRequestFilter.getStartDate());
			}
			if (monitorRequestFilter.getEndDate() != null) {
				simpleQuery.addWhereWithAnd("o.startTime <= :ENDDATE");
				simpleQuery.addParameter("ENDDATE", monitorRequestFilter.getEndDate());
			}
		}
	}

	@Override
	public TypedQuery<MonitorRequestList> toQuery(PageContextDto<MonitorRequestFilter> pageContextDto, EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, false).getTypedQuery(MonitorRequestList.class, entityManager);
	}

	@Override
	public TypedQuery<Long> toCountQuery(PageContextDto<MonitorRequestFilter> pageContextDto, EntityManager entityManager) {
		return pageContextDtoToTypedQuery(pageContextDto, true).getTypedQuery(Long.class, entityManager);
	}
	
}
