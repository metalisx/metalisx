package org.metalisx.monitor.web.service.dto;

import org.metalisx.common.rest.dto.ItemsDto;

public class ChartDto extends ItemsDto {

    private ChartSettingsDto settings;

    public ChartDto() {
    }

    public ChartSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(ChartSettingsDto settings) {
        this.settings = settings;
    }
    
}
