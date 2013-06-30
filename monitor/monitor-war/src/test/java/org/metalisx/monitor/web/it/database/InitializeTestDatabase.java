package org.metalisx.monitor.web.it.database;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.model.MonitorRequest;
import org.metalisx.monitor.domain.model.MonitorSession;
import org.metalisx.monitor.domain.model.MonitorSetting;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.domain.service.MonitorRequestService;
import org.metalisx.monitor.domain.service.MonitorSettingService;

@Startup
@Singleton
@DependsOn("MonitorDatabase")
public class InitializeTestDatabase {

	@EJB
	private MonitorRequestService monitorRequestService;

	@EJB
	private MonitorLogService monitorLogService;

	@EJB
	private MonitorSettingService monitorSettingService;

	@PostConstruct
    public void postConstruct() {

		enableFiltersAndInterceptor();
		
        String sessionId = "a-session-id";
        String requestId = "a-request-id";
        
        Date requestDate = new Date();
        MonitorRequest monitorRequest = new MonitorRequest();
        MonitorSession monitorSession = new MonitorSession();
        monitorSession.setRequestedSessionId(sessionId);
        monitorRequest.setSession(monitorSession);
        monitorRequest.setStartTime(requestDate);
        monitorRequest.setEndTime(requestDate);
        monitorRequest.setRequestId(requestId);
        monitorRequest.setUrl("http://localhost/testapplication");
        monitorRequest = monitorRequestService.persist(monitorRequest);

        Date logDate = new Date();
        MonitorLog monitorLog = new MonitorLog();
        monitorLog.setLogLevel("INFO");
        monitorLog.setLogClass("a.package.AClass");
        monitorLog.setLogDate(logDate);
        monitorLog.setSessionId(sessionId);
        monitorLog.setRequestId(requestId);
        monitorLog.setOrganization("An organization");
        monitorLog.setUsername("An username");
        monitorLog.setMessage("A message");
        monitorLog.setDepth(1);
        monitorLog.setDuration(10);
        monitorLog = monitorLogService.persist(monitorLog);
    }
	
	private void enableFiltersAndInterceptor() {
    	MonitorSetting monitorSetting = monitorSettingService.findByCode(
    			MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_PROFILER_FILTER.name());
    	monitorSetting.setValue("true");
    	monitorSettingService.persist(monitorSetting);
    	
    	monitorSetting = monitorSettingService.findByCode(
    			MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_PROFILER_INTERCEPTOR.name());
    	monitorSetting.setValue("true");
    	monitorSettingService.persist(monitorSetting);

    	monitorSetting = monitorSettingService.findByCode(
    			MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_REQUEST_FILTER.name());
    	monitorSetting.setValue("true");
    	monitorSettingService.persist(monitorSetting);
	}
}
