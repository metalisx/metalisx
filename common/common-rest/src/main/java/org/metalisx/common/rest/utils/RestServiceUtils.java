package org.metalisx.common.rest.utils;

import java.util.Calendar;
import java.util.Date;

public class RestServiceUtils {

    private static final String DAY = "DAY";
	private static final String MONTH = "MONTH";
	private static final String YEAR = "YEAR";

	public static boolean isBlank(String value) {
        return value == null || "".equals(value.trim()) || "null".equals(value);
    }

    public static boolean isBody(String body) {
        return body != null && !"".equals(body) && !"null".equals(body) && !"{}".equals(body);
    }

    public static Calendar floor(Calendar calendar, String precision) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendar.getTime());
        if (YEAR.equals(precision)) {
            cal.set(Calendar.MONTH, 0);
        }
        if (YEAR.equals(precision) || MONTH.equals(precision)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (YEAR.equals(precision) || MONTH.equals(precision) || DAY.equals(precision)) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
        }
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Calendar ceil(Calendar calendar, String precision) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendar.getTime());
        if (YEAR.equals(precision)) {
            cal.set(Calendar.MONTH, 11);
        }
        if (YEAR.equals(precision) || MONTH.equals(precision)) {
            cal.set(Calendar.DAY_OF_MONTH, 31);
        }
        if (YEAR.equals(precision) || MONTH.equals(precision) || DAY.equals(precision)) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
        }
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Date floor(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date ceil(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

}
