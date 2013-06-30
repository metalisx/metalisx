package org.metalisx.monitor.file.listener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.metalisx.monitor.context.MonitorLogListenerMarker;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.file.parser.LineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a file listener with the function of reading log statement
 * which are meant for the application.
 * 
 * @author stefan.oude.nijhuis
 * 
 */
public class FileListener implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(FileListener.class);

    private Thread thread = null; // Juck

    private String filename = null;

    private LineParser lineParser;

    private int sleepInterval = 1000;

    private boolean keepReading = true;

    private MonitorLogService monitorLogService;

    private Set<String> disabledListenerThreads = new HashSet<String>();

    private static Pattern p;

    static {
        p = Pattern.compile(MonitorLogListenerMarker.MARKER_REGEXP);
    }

    public FileListener(String filename, LineParser lineParser, MonitorLogService monitorLogService) {
        this.filename = filename;
        this.lineParser = lineParser;
        this.monitorLogService = monitorLogService;
    }

    @Override
    public void run() {
        RandomAccessFile randomAccessFile = null;
        try {
            logger.info("run() -> Monitoring started on file " + this.filename);
            randomAccessFile = new RandomAccessFile(this.filename, "r");
            listen(randomAccessFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.info("run() -> Monitoring stopped on file " + this.filename);
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void listen(RandomAccessFile randomAccessFile) throws IOException, InterruptedException {
        String line = null;
        // Go to the end of the file, we are only interested in new logging.
        randomAccessFile.seek(randomAccessFile.length());
        while (isKeepReading()) {
            line = randomAccessFile.readLine();
            if (line == null) {
                Thread.sleep(sleepInterval);
            } else if (MonitorLogListenerMarker.isMarker(line)) {
                parseMarker(line);
            } else {
                MonitorLog monitorLog = lineParser.parse(line);
                if (monitorLog != null && !disabledListenerThreads.contains(monitorLog.getThread())) {
                    monitorLogService.persist(monitorLog);
                }
            }
        }
    }

    private void parseMarker(String line) {
        Matcher m = p.matcher(line);
        m.find();
        String action = m.group(1);
        String thread = m.group(2);
        if (MonitorLogListenerMarker.DISABLE_ACTION.equals(action)) {
            disabledListenerThreads.add(thread);
        } else if (MonitorLogListenerMarker.ENABLE_ACTION.equals(action)) {
            disabledListenerThreads.remove(thread);
        }
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void stop() {
        this.keepReading = false;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean isKeepReading() {
        return keepReading;
    }

}
