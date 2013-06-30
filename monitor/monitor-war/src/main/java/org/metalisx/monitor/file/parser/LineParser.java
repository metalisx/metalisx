package org.metalisx.monitor.file.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.metalisx.monitor.domain.model.MonitorLog;


/**
 * The line parser is parsing the line character by character, it is not using
 * regular expression. Using regular expression would result in a to high
 * performance penalty. Test with a 20MB file: with regular expression: Found
 * 67814 matching lines in 34461 milliseconds. without regular expression: Found
 * 67814 matching lines in 25726 milliseconds.
 * 
 * Test with a file: with regular expression: Found 1251721 matching lines in
 * 553100 milliseconds without regular expression: Found 1251721 matching lines
 * in 499059 milliseconds.
 * 
 * When using an in memory database the loading time in consecutive loads
 * increases quick. So use a file base database if possible.
 * 
 * The line contains the following data:
 * <ul>
 * <li>1 = log time</li>
 * <li>2 = level</li>
 * <li>3 = class</li>
 * <li>4 = thread</li>
 * <li>5 = session id</li>
 * <li>6 = request id</li>
 * <li>7 = organization</li>
 * <li>8 = username</li>
 * <li>9 = depth (optional)</li>
 * <li>10 = message</li>
 * <li>11 = duration</li>
 * </ul>
 * 
 * @author stefan.oude.nijhuis
 * 
 */

@Named
public class LineParser {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    private static final String VALID_LINE_REGEX = ".+ \\[SessionId: .*, RequestId: .*, (ParentRequestId: .*, |)(Organization|Organisatie): .*, (Username|Gebruikersnaam): .*] .+";

    private static final String EXCLUDE_REGEX = ".*(MonitorLog|MonitorRequest|MonitorRequestCertificate|"
            + "MonitorRequestCookie|MonitorRequestFormParameter|MonitorRequestFormParameterValue|"
            + "MonitorRequestHeader|MonitorRequestLocale|MonitorRequestPart|MonitorRequestPartHeader|"
            + "MonitorRequestSecurity|MonitorResponse|MonitorResponseCookie|MonitorResponseHeader|"
            + "MonitorSession|MonitorSetting|" + "mrt_seq|mrc_seq|mce_seq|mfe_seq|mve_seq|mrr_seq|mle_seq|mpt_seq|"
            + "mpr_seq|msy_seq|mre_seq|mne_seq|mnr_seq|msn_seq|msg_seq).*";

    public LineParser() {
    }

    public MonitorLog parse(String line) {
        MonitorLog monitorLog = null;
        if (isValid(line)) {
            LineContext lineContext = new LineContext(line);
            monitorLog = new MonitorLog();
            monitorLog.setDuration(lineContext.getDuration());
            if (lineContext.isValid()) {
                monitorLog.setLogDate(lineContext.getLogDate());
                if (lineContext.isValid()) {
                    monitorLog.setLogLevel(lineContext.getLogLevel());
                    monitorLog.setLogClass(lineContext.getLogClass());
                    monitorLog.setThread(lineContext.getThread());
                    monitorLog.setSessionId(lineContext.getSessionId());
                    monitorLog.setRequestId(lineContext.getRequestId());
                    monitorLog.setParentRequestId(lineContext.getParentRequestId());
                    monitorLog.setOrganization(lineContext.getOrganization());
                    monitorLog.setUsername(lineContext.getUsername());
                    monitorLog.setDepth(lineContext.getDepth());
                    monitorLog.setMessage(lineContext.getMessage());
                } else {
                    monitorLog = null;
                }
            } else {
                monitorLog = null;
            }
        }
        return monitorLog;
    }

    private boolean isValid(String line) {
        boolean isValid = true;
        // Check if the line contains the monitor context keys
        if (!line.matches(VALID_LINE_REGEX)) {
            isValid = false;
        } else if (line.matches(EXCLUDE_REGEX)) {
            isValid = false;
        }
        return isValid;
    }

    private class LineContext {

        private DateFormat formatter;

        private String line;

        private int length;

        private int i = 0;

        private boolean valid = true;

        public LineContext(String line) {
            this.formatter = new SimpleDateFormat(DATE_FORMAT);
            this.line = line;
            this.length = line.length();
        }

        public long getDuration() {
            if (line.charAt(line.length() - 1) == 's' && line.charAt(line.length() - 2) == 'm') {
                length = length - 2; // Remove text 'ms' from the end
                String value = "";
                for (; length > i; length--) {
                    char c = line.charAt(length - 1);
                    if (c != ' ') {
                        value = c + value;
                    } else {
                        break;
                    }
                }
                if (!(line.charAt(line.length() - 2) == ':' && line.charAt(line.length() - 3) == 'e'
                        && line.charAt(line.length() - 3) == 'm' && line.charAt(line.length() - 3) == 'i')
                        && line.charAt(line.length() - 3) == 't') {
                    valid = false;
                }
                length = length - 8; // Remove comma, space, text 'time:' and
                                     // space
                                     // from the end
                return Long.valueOf(value);
            }
            return 0;
        }

        public Date getLogDate() {
            Date date = null;
            String valueDate = getValue(' '); // date part
            skipSpace(); // Skip space
            String valueTime = getValue(' '); // time part
            try {
                date = formatter.parse(valueDate + ' ' + valueTime);
            } catch (ParseException e) {
                // Assume it is not a log line we are interested in.
                valid = false;
            }
            return date;
        }

        public String getLogLevel() {
            skipSpace(); // Skip space
            return getValue(' ');
        }

        public String getLogClass() {
            skipSpace(); // Skip space
            i++; // Skip open bracket
            return getValue(']');
        }

        public String getThread() {
            i++; // Skip closing bracket
            skipSpace(); // Skip space
            i++; // Skip open parenthesis
            return getValue(')');
        }

        public String getSessionId() {
        	String value = null;
        	int tagLength = 14;
        	if (line.length() > i + tagLength && ") [SessionId: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
        		value = getValue(',');
        	} else {
        		valid = false;
        	}
            return value;
        }

        public String getRequestId() {
        	String value = null;
        	int tagLength = 13;
        	if (line.length() > i + tagLength && ", RequestId: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
                value = getValue(',');
        	} else {
        		valid = false;
        	}
        	return value;
        }

        public String getParentRequestId() {
        	String value = null;
        	int tagLength = 19;
        	if (line.length() > i + tagLength && ", ParentRequestId: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
	            value = getValue(',');
        	} else {
        		valid = false;
        	}
        	return value;
        }

        public String getOrganization() {
        	String value = null;
        	int tagLength = 16;
        	int legacyTagLength = 15;
        	if (line.length() > i + tagLength && ", Organization: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
	            value = getValue(',');
        	} else if (line.length() > i + legacyTagLength && ", Organisatie: ".equals(line.substring(i, i + legacyTagLength))) {
        		i = i + legacyTagLength;
	            value = getValue(',');
        	} else {
        		valid = false;
        	}
        	return value;
        }

        public String getUsername() {
        	String value = null;
        	int tagLength = 12;
        	int legacyTagLength = 18;
        	if (line.length() > i + tagLength && ", Username: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
	            value = getValue(']');
        	} else if (line.length() > i + legacyTagLength && ", Gebruikersnaam: ".equals(line.substring(i, i + legacyTagLength))) {
        		i = i + legacyTagLength;
	            value = getValue(']');
        	} else {
        		valid = false;
        	}
        	return value;
        }

        public Integer getDepth() {
            i++; // Skip closing bracket
            Integer depth = 1;
        	int tagLength = 9;
        	if (line.length() > i + tagLength && " (Depth: ".equals(line.substring(i, i + tagLength))) {
        		i = i + tagLength;
                String value = getValue(')');
                if (value != null) {
                    depth = Integer.valueOf(value);
                }
                i++; // Skip closing parenthesis
        	} else {
        		valid = false;
        	}
        	i++; // Skip space
            return depth;
        }

        public boolean isValid() {
            return valid;
        }

        private void skipSpace() {
            for (; i < length; i++) {
                char c = line.charAt(i);
                if (c != ' ') {
                    break;
                }
            }
        }

        public String getMessage() {
            String value = "";
            for (; i < length; i++) {
                char c = line.charAt(i);
                value = value + c;
            }
            return value;
        }

        private String getValue(char stopChar) {
            String value = "";
            for (; i < length; i++) {
                char c = line.charAt(i);
                if (c != stopChar) {
                    value = value + c;
                } else {
                    break;
                }
            }
    		if ("".equals(value)) {
   				value = null;
    		}
            return value;
        }

    }

}
