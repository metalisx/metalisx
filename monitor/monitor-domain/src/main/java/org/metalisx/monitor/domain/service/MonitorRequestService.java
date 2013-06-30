package org.metalisx.monitor.domain.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.service.AbstractService;
import org.metalisx.monitor.domain.dao.model.MonitorRequestCertificateDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestListDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestPartDao;
import org.metalisx.monitor.domain.dao.model.MonitorResponseDao;
import org.metalisx.monitor.domain.filter.MonitorRequestFilter;
import org.metalisx.monitor.domain.interceptor.DisableLogListener;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorRequestCertificate;
import org.metalisx.monitor.domain.model.MonitorRequestPart;
import org.metalisx.monitor.domain.model.MonitorResponse;
import org.metalisx.monitor.domain.model.list.MonitorRequestList;


@Stateless
@DisableLogListener
public class MonitorRequestService extends AbstractService {

    @Inject
    private MonitorRequestDao requestDao;

    @Inject
    private MonitorRequestListDao requestListDao;

    @Inject
    private MonitorResponseDao monitorResponseDao;

    @Inject
    private MonitorRequestPartDao monitorRequestPartDao;

    @Inject
    private MonitorRequestCertificateDao monitorRequestCertificateDao;

    @PostConstruct
    public void initDao() {
    }

    public MonitorRequest findById(Long id) {
        return requestDao.findById(id);
    }

    public MonitorRequest persist(MonitorRequest request) {
        return requestDao.persist(request);
    }

    public PageDto<MonitorRequestList> findPage(PageContextDto<MonitorRequestFilter> pageContextDto) {
        return requestListDao.findPage(pageContextDto);
    }

    public MonitorResponse findResponse(Long id) {
        return monitorResponseDao.findById(id);
    }

    public MonitorRequestPart findPart(Long id) {
        return monitorRequestPartDao.findById(id);
    }

    public MonitorRequestCertificate findCertificate(Long id) {
        return monitorRequestCertificateDao.findById(id);
    }

}
