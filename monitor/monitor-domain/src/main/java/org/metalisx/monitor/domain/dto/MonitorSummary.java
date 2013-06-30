package org.metalisx.monitor.domain.dto;

import java.util.Date;

public class MonitorSummary {

	private String message;

    private long count;

    private Date startDate;

    private Date endDate;

    private long minDuration;

    private long maxDuration;

    private long averageDuration;

    private long totalDuration;

    public MonitorSummary() {
    }

    public MonitorSummary(String message, long count, Date startDate, Date endDate, long minDuration, long maxDuration,
            double averageDuration, long totalDuration) {
        this.message = message;
        this.count = count;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.averageDuration = Math.round(averageDuration);
        this.totalDuration = totalDuration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
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

    public long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public long getAverageDuration() {
        return averageDuration;
    }

    public void setAverageDuration(long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

}