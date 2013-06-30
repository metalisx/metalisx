package org.metalisx.monitor.domain.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.service.AbstractService;
import org.metalisx.monitor.domain.dao.model.MonitorSettingDao;
import org.metalisx.monitor.domain.interceptor.DisableLogListener;
import org.metalisx.monitor.domain.model.MonitorSetting;


@Stateless
@DisableLogListener
public class MonitorSettingService extends AbstractService {

    @Inject
    private MonitorSettingDao monitorSettingDao;

    public PageDto<MonitorSetting> findAll(ContextDto contextDto) {
        return monitorSettingDao.findAll(contextDto);
    }

    public MonitorSetting findByCode(String code) {
        return monitorSettingDao.findByCode(code);
    }
    
    public void persist(MonitorSetting monitorSetting) {
        monitorSettingDao.persist(monitorSetting);
    }

}
