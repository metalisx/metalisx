package org.metalisx.monitor.domain.enumeration;

public enum MonitorSettingCode {

	MONITOR_WEB_APPLICATION_LISTENER_LOG_FILE, MONITOR_WEB_APPLICATION_LOAD_LOG_FILE, MONITOR_WEB_APPLICATION_DISABLE_REQUEST_FILTER, MONITOR_WEB_APPLICATION_DISABLE_PROFILER_FILTER, MONITOR_WEB_APPLICATION_DISABLE_PROFILER_INTERCEPTOR;

	public static MonitorSettingCode getValue(String code) {
		for (MonitorSettingCode monitorSettingCode : values()) {
			if (monitorSettingCode.name().equals(code)) {
				return monitorSettingCode;
			}
		}
		throw new IllegalStateException("Unknown " + MonitorSettingCode.class.getSimpleName() + " " + code);
	}

}
