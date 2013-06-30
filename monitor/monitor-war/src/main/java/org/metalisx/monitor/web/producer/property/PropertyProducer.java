package org.metalisx.monitor.web.producer.property;

import java.io.File;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;
import org.metalisx.monitor.domain.model.MonitorSetting;
import org.metalisx.monitor.domain.service.MonitorSettingService;

@Named
public class PropertyProducer {

	private static final String LOG_DIRECTORY = System.getProperty("jboss.server.log.dir");

	private static final String LOG_FILE = "monitor.log";

	@EJB
	MonitorSettingService monitorSettingService;

	@Produces
	@Value
	public String getStringValue(InjectionPoint injectionPoint) {
		return getValue(injectionPoint);
	}

	@Produces
	@Value
	public boolean getBooleanValue(InjectionPoint injectionPoint) {
		return Boolean.valueOf(getStringValue(injectionPoint));
	}

	private String getValue(InjectionPoint injectionPoint) {
		String key = injectionPoint.getAnnotated().getAnnotation(Value.class).value();
		MonitorSetting monitorSetting = monitorSettingService.findByCode(key);
		String value = monitorSetting.getValue();
		if ((MonitorSettingCode.MONITOR_WEB_APPLICATION_LISTENER_LOG_FILE.name().equals(key) || MonitorSettingCode.MONITOR_WEB_APPLICATION_LOAD_LOG_FILE
		        .name().equals(key)) && (value == null || "".equals(value)) && LOG_DIRECTORY != null) {
			value = LOG_DIRECTORY + File.separator + LOG_FILE;
		}
		return value;
	}

}
