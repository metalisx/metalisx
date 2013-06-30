package org.metalisx.monitor.web.service.dto;

import java.util.Date;

public class ChartSettingsDto {

    private Date min;

    private Date max;

    public ChartSettingsDto() {
    }

    public Date getMin() {
        return min;
    }

    public void setMin(Date min) {
        this.min = min;
    }

    public Date getMax() {
        return max;
    }

    public void setMax(Date max) {
        this.max = max;
    }

}
