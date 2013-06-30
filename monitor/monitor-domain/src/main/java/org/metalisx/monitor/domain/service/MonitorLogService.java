package org.metalisx.monitor.domain.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.service.AbstractService;
import org.metalisx.common.domain.utils.DatePrecision;
import org.metalisx.monitor.domain.dao.dto.MonitorLogOverviewItemDao;
import org.metalisx.monitor.domain.dao.dto.MonitorLogSummaryDao;
import org.metalisx.monitor.domain.dao.model.MonitorLogDao;
import org.metalisx.monitor.domain.dto.MonitorOverviewItem;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.interceptor.DisableLogListener;
import org.metalisx.monitor.domain.model.MonitorLog;


@Stateless
@DisableLogListener
public class MonitorLogService extends AbstractService {

    @Inject
    private MonitorLogDao monitorLogDao;

    @Inject
    private MonitorLogOverviewItemDao monitorLogOverviewItemDao;

    @Inject
    private MonitorLogSummaryDao monitorLogSummaryDao;

    public MonitorLog findById(long id) {
        return monitorLogDao.findById(id);
    }

    public PageDto<MonitorLog> findPage(PageContextDto<MonitorLogFilter> pageContextDto) {
        return monitorLogDao.findPage(pageContextDto);
    }

    public MonitorLog persist(MonitorLog monitorLog) {
        return monitorLogDao.persist(monitorLog);
    }

    public List<MonitorLog> findByRequestId(String requestId) {
        return monitorLogDao.findByRequestId(requestId);
    }

    public List<MonitorOverviewItem> findOverviewByFilter(MonitorLogFilter monitorLogFilter, DatePrecision datePrecision) {
        return monitorLogOverviewItemDao.findOverviewByFilter(monitorLogFilter, datePrecision);
    }

    public PageDto<MonitorSummary> findPageSummary(PageContextDto<MonitorLogFilter> pageContextDto) {
        return monitorLogSummaryDao.findPage(pageContextDto);
    }

}
