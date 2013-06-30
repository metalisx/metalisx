package org.metalisx.monitor.domain.query;

import org.metalisx.common.domain.query.SimpleQuery;
import org.metalisx.common.domain.utils.JpaUtils;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;

public class MonitorLogFilterToSimpleQuery {

	public MonitorLogFilterToSimpleQuery() {
	}
	
	public void processFilter(SimpleQuery simpleQuery, MonitorLogFilter monitorLogFilter) {
		if (monitorLogFilter != null) {
			if (!JpaUtils.isNullOrEmpty(monitorLogFilter.getSessionId())) {
				simpleQuery.addWhereWithAnd("o.sessionId like :SESSIONID");
				simpleQuery.addParameter("SESSIONID", JpaUtils.like(monitorLogFilter.getSessionId()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorLogFilter.getRequestId())) {
				simpleQuery.addWhereWithAnd("o.requestId like :REQUESTID");
				simpleQuery.addParameter("REQUESTID", JpaUtils.like(monitorLogFilter.getRequestId()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorLogFilter.getMessage())) {
				simpleQuery.addWhereWithAnd("o.message like :MESSAGE");
				simpleQuery.addParameter("MESSAGE", JpaUtils.like(monitorLogFilter.getMessage()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorLogFilter.getOrganization())) {
				simpleQuery.addWhereWithAnd("o.organization like :ORGANIZATION");
				simpleQuery.addParameter("ORGANIZATION", JpaUtils.like(monitorLogFilter.getOrganization()));
			}
			if (!JpaUtils.isNullOrEmpty(monitorLogFilter.getUsername())) {
				simpleQuery.addWhereWithAnd("o.username like :USERNAME");
				simpleQuery.addParameter("USERNAME", JpaUtils.like(monitorLogFilter.getUsername()));
			}
			if (monitorLogFilter.getStartDate() != null) {
				simpleQuery.addWhereWithAnd("o.logDate >= :STARTDATE");
				simpleQuery.addParameter("STARTDATE", monitorLogFilter.getStartDate());
			}
			if (monitorLogFilter.getEndDate() != null) {
				simpleQuery.addWhereWithAnd("o.logDate <= :ENDDATE");
				simpleQuery.addParameter("ENDDATE", monitorLogFilter.getEndDate());
			}
		}
	}

}
