package org.metalisx.monitor.context;

/**
 * This class is for temporary stopping the log listeners to process log
 * statements per thread on the file they are monitoring. It works by placing
 * disable and enable markers in the log file.
 * 
 * Usage
 * 
 * Disable the processing by calling the {@link #getDisableMarker(String)} with
 * the thread name and then placing the return value in the log file.
 * 
 * Now all log statements for this thread are skipped by the log listeners.
 * 
 * Then enable the processing by calling the {@link #getEnableMarker(String)}
 * with the thread name and then placing the return value in the log file.
 * 
 * Now all log statements for this thread are processed again.
 * 
 * The best way to use the disable and enable markers is by placing them in the
 * log file by the same method, this will prevent every log statements between
 * those commands to be processed by the log listener.
 * 
 * @author Stefan.Oude.Nijhuis
 * 
 */
public class MonitorLogListenerMarker {

    public static final String MARKER_REGEXP = ".*MONITOR APPLICATOIN : LOG LISTENER : ACTION = (.+) : THREAD = (.+)";

    private static final String MARKER = "MONITOR APPLICATOIN : LOG LISTENER : ACTION = %s : THREAD = %s";

    public static final String ENABLE_ACTION = "ENABLE";

    public static final String DISABLE_ACTION = "DISABLE";

    public static String getEnableMarker(String thread) {
        return String.format(MonitorLogListenerMarker.MARKER, MonitorLogListenerMarker.ENABLE_ACTION, thread);
    }

    public static String getDisableMarker(String thread) {
        return String.format(MonitorLogListenerMarker.MARKER, MonitorLogListenerMarker.DISABLE_ACTION, thread);
    }

    public static boolean isMarker(String line) {
        boolean isMarker = false;
        if (line.matches(MonitorLogListenerMarker.MARKER_REGEXP)) {
            isMarker = true;
        }
        return isMarker;
    }

}
