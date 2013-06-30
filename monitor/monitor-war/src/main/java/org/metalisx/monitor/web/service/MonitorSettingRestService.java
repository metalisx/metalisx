package org.metalisx.monitor.web.service;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.rest.dto.MessagesDto;
import org.metalisx.common.rest.service.AbstractRestService;
import org.metalisx.monitor.domain.enumeration.MonitorSettingCode;
import org.metalisx.monitor.domain.model.MonitorSetting;
import org.metalisx.monitor.domain.service.MonitorSettingService;
import org.metalisx.monitor.profiler.interceptor.Profile;
import org.metalisx.monitor.profiler.interceptor.ProfilerInterceptorContext;
import org.metalisx.monitor.profiler.servlet.filter.ProfilerFilterContext;
import org.metalisx.monitor.request.servlet.filter.RequestFilterContext;
import org.metalisx.monitor.web.startup.MonitorStartup;


/**
 * REST setting service producing JSON objects.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/settings")
public class MonitorSettingRestService extends AbstractRestService {

    @Inject
    private MonitorStartup monitorApplicationContext;

    @EJB
    private MonitorSettingService monitorSettingService;

    @Inject
    private RequestFilterContext requestFilterContext;

    @Inject
    private ProfilerFilterContext profilerFilterContext;

    @Inject
    ProfilerInterceptorContext profilerInterceptorContext;

    @POST
    @Path("/page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PageDto<MonitorSetting> getPage(ContextDto contextDto) {
        return monitorSettingService.findAll(contextDto);
    }

    @POST
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public MessagesDto setSetting(MonitorSetting monitorSetting) {
        monitorSettingService.persist(monitorSetting);
        updateFiltersAndInterceoptors(monitorSetting);
        return new MessagesDto();
    }

    private void updateFiltersAndInterceoptors(MonitorSetting monitorSetting) {
    	if (MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_REQUEST_FILTER == monitorSetting.getCode()) {
            requestFilterContext.setDisableFilter(Boolean.valueOf(monitorSetting.getValue()));
    	} else if (MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_PROFILER_FILTER == monitorSetting.getCode()) {
            profilerFilterContext.setDisableFilter(Boolean.valueOf(monitorSetting.getValue()));
    	} else if (MonitorSettingCode.MONITOR_WEB_APPLICATION_DISABLE_PROFILER_INTERCEPTOR == monitorSetting.getCode()) {
            profilerInterceptorContext.setDisableInterceptor(Boolean.valueOf(monitorSetting.getValue()));
    	}
    }
    
}
