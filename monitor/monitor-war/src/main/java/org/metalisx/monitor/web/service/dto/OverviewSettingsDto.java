package org.metalisx.monitor.web.service.dto;

import java.util.Date;

import org.metalisx.common.domain.utils.DatePrecision;


public class OverviewSettingsDto {

    private Date min;

    private Date max;

    private DatePrecision datePrecision;

    private Date selectionStartDate;

    private Date selectionEndDate;

    public OverviewSettingsDto() {
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

    public DatePrecision getDatePrecision() {
        return datePrecision;
    }

    public void setDatePrecision(DatePrecision datePrecision) {
        this.datePrecision = datePrecision;
    }

    public Date getSelectionStartDate() {
        return selectionStartDate;
    }

    public void setSelectionStartDate(Date selectionStartDate) {
        this.selectionStartDate = selectionStartDate;
    }

    public Date getSelectionEndDate() {
        return selectionEndDate;
    }

    public void setSelectionEndDate(Date selectionEndDate) {
        this.selectionEndDate = selectionEndDate;
    }

}
