package org.metalisx.monitor.domain.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.metalisx.common.domain.service.AbstractService;
import org.metalisx.monitor.domain.dao.model.MonitorLogDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestCertificateDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestCookieDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestFormParameterDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestFormParameterValueDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestHeaderDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestLocaleDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestPartDao;
import org.metalisx.monitor.domain.dao.model.MonitorRequestPartHeaderDao;
import org.metalisx.monitor.domain.dao.model.MonitorResponseCookieDao;
import org.metalisx.monitor.domain.dao.model.MonitorResponseHeaderDao;
import org.metalisx.monitor.domain.interceptor.DisableLogListener;


@Stateless
@DisableLogListener
public class AdminService extends AbstractService {

    @Inject
    private MonitorLogDao monitorLogDao;

    @Inject
    private MonitorRequestDao monitorRequestDao;

    @Inject
    private MonitorRequestCookieDao monitorRequestCookieDao;

    @Inject
    private MonitorRequestFormParameterDao monitorRequestFormParameterDao;

    @Inject
    private MonitorRequestFormParameterValueDao monitorRequestFormParameterValueDao;

    @Inject
    private MonitorRequestHeaderDao monitorRequestHeaderDao;

    @Inject
    private MonitorRequestLocaleDao monitorRequestLocaleDao;

    @Inject
    private MonitorRequestPartDao monitorRequestPartDao;

    @Inject
    private MonitorRequestPartHeaderDao monitorRequestPartHeaderDao;

    @Inject
    private MonitorRequestCertificateDao monitorRequestCertificateDao;

    @Inject
    private MonitorResponseCookieDao monitorResponseCookieDao;

    @Inject
    private MonitorResponseHeaderDao monitorResponseHeaderDao;

    public void removeMonitorLogs() {
        monitorLogDao.removeAll();
    }

    public void removeMonitorRequests() {
        monitorRequestCookieDao.removeAll();
        monitorRequestFormParameterValueDao.removeAll();
        monitorRequestFormParameterDao.removeAll();
        monitorRequestHeaderDao.removeAll();
        monitorRequestLocaleDao.removeAll();
        monitorRequestPartHeaderDao.removeAll();
        monitorRequestPartDao.removeAll();
        monitorRequestCertificateDao.removeAll();
        monitorResponseCookieDao.removeAll();
        monitorResponseHeaderDao.removeAll();
        monitorRequestDao.removeAll();
    }

}
