package org.metalisx.common.rest.parameter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;

/**
 * Specific type to use as REST parameter. It is a wrapper for a Date and
 * handles converting the input string to a Date instance. The order in which it
 * tries to convert the string to a date is:
 * <ul>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ, last part are milliseconds with time zone</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSS, last part are milliseconds</li>
 * <li>yyyy-MM-dd'T'HH:mm:ssZ, the last part are seconds with time zone</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss, the last part are seconds</li>
 * <li>yyyy-MM-dd'T'HH:mmZ, the last part are minutes with time zone</li>
 * <li>yyyy-MM-dd'T'HH:mm, the last part are minutes</li>
 * </ul>
 * If the last one fails then a IllegalStateException is thrown.
 * 
 * This is less intrusive then a specific REST implementation handler, if it is
 * even provided by the REST implementation.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class DateParameter extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    private static List<String> formats;

    static {
        formats = new ArrayList<String>();
        formats.add("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        formats.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        formats.add("yyyy-MM-dd'T'HH:mm:ssZ");
        formats.add("yyyy-MM-dd'T'HH:mm:ss");
        formats.add("yyyy-MM-dd'T'HH:mmZ");
        formats.add("yyyy-MM-dd'T'HH:mm");
    }

    private Date date;

    public DateParameter(String dateIn) {
        if (dateIn == null || "".equals(dateIn)) {
            this.date = null;
        } else {
            for (String format : formats) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat(format);
                    date = dateFormat.parse(dateIn);
                    break;
                } catch (ParseException e) {
                    // silent
                }
            }
            if (date == null) {
                throw new WebApplicationException(new Throwable("Could not parse date parameter " + dateIn + "."
                        + " Valid formats are " + formatsToString() + "."));
            }
        }
    }

    private String formatsToString() {
        String value = "";
        for (String format : formats) {
            value = "".equals(value) ? format : value + ", " + format;
        }
        return value;
    }

    public Date getDate() {
        return date;
    }

}
