package org.metalisx.common.domain.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	public static void processDateRange(InterfaceDateRange dateRange) {
		if (dateRange != null && dateRange.getRange() != null && !"custom".equals(dateRange.getRange())) {
			if ("MAX".equals(dateRange.getRange())) {
				dateRange.setStartDate(null);
				dateRange.setEndDate(null);
			} else {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				dateRange.setEndDate(calendar.getTime());
				if ("1m".equals(dateRange.getRange())) {
					calendar.add(Calendar.MINUTE, -1);
				} else if ("1H".equals(dateRange.getRange())) {
					calendar.add(Calendar.HOUR, -1);
				} else if ("1D".equals(dateRange.getRange())) {
					calendar.add(Calendar.DATE, -1);
				} else if ("1W".equals(dateRange.getRange())) {
					calendar.add(Calendar.DATE, -7);
				} else if ("1M".equals(dateRange.getRange())) {
					calendar.add(Calendar.MONTH, -1);
				} else if ("3M".equals(dateRange.getRange())) {
					calendar.add(Calendar.MONTH, -3);
				} else if ("6M".equals(dateRange.getRange())) {
					calendar.add(Calendar.MONTH, -6);
				} else if ("1Y".equals(dateRange.getRange())) {
					calendar.add(Calendar.YEAR, -1);
				}
				dateRange.setStartDate(calendar.getTime());
			}
		}
	}

	public static DatePrecision getPrecision(Date startDate, Date endDate) {
		DatePrecision f = DatePrecision.YEAR;
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)) {
			f = DatePrecision.MONTH;
		}
		if (f == DatePrecision.MONTH && startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
			f = DatePrecision.DAY;
		}
		if (f == DatePrecision.DAY
		        && startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)) {
			f = DatePrecision.HOUR;
		}
		if (f == DatePrecision.HOUR && startCalendar.get(Calendar.HOUR) == endCalendar.get(Calendar.HOUR)) {
			f = DatePrecision.MINUTE;
		}
		if (f == DatePrecision.MINUTE && startCalendar.get(Calendar.MINUTE) == endCalendar.get(Calendar.MINUTE)) {
			f = DatePrecision.SECOND;
		}
		if (f == DatePrecision.SECOND && startCalendar.get(Calendar.SECOND) == endCalendar.get(Calendar.SECOND)) {
			f = DatePrecision.MILLISECOND;
		}
		return f;
	}

	public static Date floor(Date date, DatePrecision datePrecision) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (datePrecision.ordinal() <= 1) { // YEAR, MONTH
			calendar.set(Calendar.MONTH, 0);
		}
		if (datePrecision.ordinal() <= 2) { // YEAR, MONTH, DAY
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}
		if (datePrecision.ordinal() <= 3) { // YEAR, MONTH, DAY, HOUR
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		}
		if (datePrecision.ordinal() <= 4) { // YEAR, MONTH, DAY, HOUR, MINUTE
			calendar.set(Calendar.MINUTE, 0);
		}
		if (datePrecision.ordinal() <= 5) { // YEAR, MONTH, DAY, HOUR, MINUTE,
											// SECOND
			calendar.set(Calendar.SECOND, 0);
		}
		if (datePrecision.ordinal() <= 6) { // YEAR, MONTH, DAY, HOUR, MINUTE,
											// SECOND, MILLISECOND
			calendar.set(Calendar.MILLISECOND, 0);
		}
		return calendar.getTime();
	}

	public static Date ceil(Date date, DatePrecision datePrecision) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (datePrecision.ordinal() <= 1) { // YEAR, MONTH
			calendar.set(Calendar.MONTH, 11);
		}
		if (datePrecision.ordinal() <= 2) { // YEAR, MONTH, DAY
			calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		if (datePrecision.ordinal() <= 3) { // YEAR, MONTH, DAY, HOUR
			calendar.set(Calendar.HOUR_OF_DAY, 23);
		}
		if (datePrecision.ordinal() <= 4) { // YEAR, MONTH, DAY, HOUR, MINUTE
			calendar.set(Calendar.MINUTE, 59);
		}
		if (datePrecision.ordinal() <= 5) { // YEAR, MONTH, DAY, HOUR, MINUTE,
											// SECOND
			calendar.set(Calendar.SECOND, 59);
		}
		if (datePrecision.ordinal() <= 6) { // YEAR, MONTH, DAY, HOUR, MINUTE,
											// SECOND, MILLISECOND
			calendar.set(Calendar.MILLISECOND, 999);
		}
		return calendar.getTime();
	}

}
