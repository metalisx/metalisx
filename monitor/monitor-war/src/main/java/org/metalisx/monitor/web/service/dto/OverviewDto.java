package org.metalisx.monitor.web.service.dto;

import org.metalisx.common.rest.dto.ItemsDto;

public class OverviewDto extends ItemsDto {

    private OverviewSettingsDto settings;

    public OverviewDto() {
    }

    public OverviewSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(OverviewSettingsDto settings) {
        this.settings = settings;
    }

}
