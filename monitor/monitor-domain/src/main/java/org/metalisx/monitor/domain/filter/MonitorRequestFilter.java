package org.metalisx.monitor.domain.filter;

import java.util.Date;

import org.metalisx.common.domain.filter.AbstractFilter;
import org.metalisx.common.domain.utils.InterfaceDateRange;


public class MonitorRequestFilter extends AbstractFilter implements InterfaceDateRange {

    private String range = "custom";
    private Date startDate;
    private Date endDate;
    private String sessionId;
    private String requestId;
    private String url;
    private String organization;
    private String username;

    public MonitorRequestFilter() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

}
