package org.metalisx.monitor.domain.filter;

import java.util.Date;

import org.metalisx.common.domain.filter.AbstractFilter;
import org.metalisx.common.domain.utils.InterfaceDateRange;


public class MonitorLogFilter extends AbstractFilter implements InterfaceDateRange {

    private String range = "custom";
    private Date startDate;
    private Date endDate;
    private String sessionId;
    private String requestId;
    private String message;
    private String organization;
    private String username;
    private boolean showList = true;
    private boolean showChart = false;
    private boolean showOverviewChart = true;
    private boolean realtime = false;
    private int realtimeInterval = 3000;

    public MonitorLogFilter() {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public boolean isShowList() {
        return showList;
    }

    public void setShowList(boolean showList) {
        this.showList = showList;
    }

    public boolean isShowChart() {
        return showChart;
    }

    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
    }

    public boolean isShowOverviewChart() {
        return showOverviewChart;
    }

    public void setShowOverviewChart(boolean showOverviewChart) {
        this.showOverviewChart = showOverviewChart;
    }

    public boolean isRealtime() {
        return realtime;
    }

    public void setRealtime(boolean realtime) {
        this.realtime = realtime;
    }

    public int getRealtimeInterval() {
        return realtimeInterval;
    }

    public void setRealtimeInterval(int realtimeInterval) {
        this.realtimeInterval = realtimeInterval;
    }
    
}
