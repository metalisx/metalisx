package org.metalisx.monitor.domain.dao.model;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.monitor.domain.dao.MonitorGenericEntityDao;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.query.MonitorLogPagedQueryProvider;

@Named
public class MonitorLogDao extends MonitorGenericEntityDao<MonitorLog, Long> {

	private static final String FIND_BY_REQUEST_ID = "select o from " + MonitorLog.class.getSimpleName()
	        + " o where o.requestId = :REQUESTID order by o.logDate";
	private static final String FIND_BY_PARENT_REQUEST_ID = "select o from " + MonitorLog.class.getSimpleName()
	        + " o where o.parentRequestId = :PARENTREQUESTID order by o.logDate";

	public List<MonitorLog> findByRequestId(String requestId) {
		TypedQuery<MonitorLog> query = entityManager.createQuery(FIND_BY_REQUEST_ID, MonitorLog.class);
		query.setParameter("REQUESTID", requestId);
		List<MonitorLog> monitorLogs = findAll(query);
		List<MonitorLog> allMonitorLogs = new ArrayList<MonitorLog>();
		for (MonitorLog monitorLog : monitorLogs) {
			List<MonitorLog> childrenMonitorLogs = findByParentRequestId(monitorLog.getRequestId());
			allMonitorLogs.addAll(childrenMonitorLogs);
			allMonitorLogs.add(monitorLog);
		}
		return allMonitorLogs;
	}

	public List<MonitorLog> findByParentRequestId(String parentRequestId) {
		TypedQuery<MonitorLog> query = entityManager.createQuery(FIND_BY_PARENT_REQUEST_ID, MonitorLog.class);
		query.setParameter("PARENTREQUESTID", parentRequestId);
		List<MonitorLog> monitorLogs = findAll(query);
		List<MonitorLog> allMonitorLogs = new ArrayList<MonitorLog>();
		for (MonitorLog monitorLog : monitorLogs) {
			List<MonitorLog> childrenMonitorLogs = findByParentRequestId(monitorLog.getRequestId());
			allMonitorLogs.addAll(childrenMonitorLogs);
			allMonitorLogs.add(monitorLog);
		}
		return allMonitorLogs;
	}

	public PageDto<MonitorLog> findPage(PageContextDto<MonitorLogFilter> pageContextDto) {
		MonitorLogPagedQueryProvider pagedQueryProvider = new MonitorLogPagedQueryProvider(pageContextDto);
		return findPage(pagedQueryProvider);
	}

}
