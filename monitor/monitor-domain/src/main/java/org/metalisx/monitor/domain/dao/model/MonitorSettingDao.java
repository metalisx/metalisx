package org.metalisx.monitor.domain.dao.model;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.metalisx.monitor.domain.dao.MonitorGenericEntityDao;
import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;
import org.metalisx.monitor.domain.model.MonitorSetting;

@Named
public class MonitorSettingDao extends MonitorGenericEntityDao<MonitorSetting, Long> {

	private static final String FIND_BY_CODE = "select o from " + MonitorSetting.class.getSimpleName()
	        + " o where o.code = :CODE";
	
	public MonitorSetting findByCode(String code) {
		TypedQuery<MonitorSetting> query = entityManager.createQuery(FIND_BY_CODE, MonitorSetting.class);
		query.setParameter("CODE", MonitorSettingCode.getValue(code));
		MonitorSetting monitorSetting = query.getSingleResult();
		return monitorSetting;
	}
	
}