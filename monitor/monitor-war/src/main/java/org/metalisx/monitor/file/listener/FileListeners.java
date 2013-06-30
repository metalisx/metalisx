package org.metalisx.monitor.file.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.file.parser.LineParser;


public class FileListeners {

    private List<FileListener> fileListeners = new CopyOnWriteArrayList<FileListener>();

    public FileListeners() {
    }

    public List<String> getListenerFilenames() {
        List<String> list = new ArrayList<String>();
        synchronized (fileListeners) {
            for (FileListener fileListener : fileListeners) {
                list.add(fileListener.getFilename());
            }
        }
        return list;
    }

    public boolean isListening(String filename) {
        boolean result = false;
        synchronized (fileListeners) {
            for (FileListener fileListener : fileListeners) {
                if (fileListener.getFilename().equals(filename)) {
                    result = true;
                }
            }
        }
        return result;
    }

    public void startListening(String filename, LineParser lineParser, MonitorLogService monitorLogService) {
        FileListener fileListener = new FileListener(filename, lineParser, monitorLogService);
        Thread thread = new Thread(fileListener);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        fileListener.setThread(thread);
        synchronized (fileListeners) {
            fileListeners.add(fileListener);
        }
    }

    public void stopListening(String filename) {
        synchronized (fileListeners) {
            for (FileListener fileListener : fileListeners) {
                if (fileListener.getFilename().equals(filename)) {
                    fileListener.stop();
                    // We want a nice stop of the thread so we do not use
                    // the stop or interrupt function on the thread.
                    Thread thread = fileListener.getThread();
                    if (thread != null) {
                        while (thread.isAlive()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    fileListeners.remove(fileListener);
                }
            }
        }
    }

}
