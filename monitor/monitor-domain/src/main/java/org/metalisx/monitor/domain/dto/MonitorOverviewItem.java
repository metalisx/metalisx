package org.metalisx.monitor.domain.dto;

import java.util.Calendar;
import java.util.Date;

public class MonitorOverviewItem {

    private long count;

    private Date date;

    private long duration;

    public MonitorOverviewItem() {
    }

    public MonitorOverviewItem(long count, Date date, double duration) {
        init(count, date, duration);
    }

    public MonitorOverviewItem(long count, int year, double duration) {
        this(count, year, 1, 1, 0, 0, 0, duration);
    }

    public MonitorOverviewItem(long count, int year, int month, double duration) {
        this(count, year, month, 1, 0, 0, 0, duration);
    }

    public MonitorOverviewItem(long count, int year, int month, int day, double duration) {
        this(count, year, month, day, 0, 0, 0, duration);
    }

    public MonitorOverviewItem(long count, int year, int month, int day, int hours, double duration) {
        this(count, year, month, day, hours, 0, 0, duration);
    }

    public MonitorOverviewItem(long count, int year, int month, int day, int hours, int minutes, double duration) {
        this(count, year, month, day, hours, minutes, 0, duration);
    }

    public MonitorOverviewItem(long count, int year, int month, int day, int hours, int minutes, int seconds, double duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hours, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        init(count, calendar.getTime(), duration);
    }

    private void init(long count, Date date, double duration) {
        this.count = count;
        this.date = date;
        this.duration = Math.round(duration);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}