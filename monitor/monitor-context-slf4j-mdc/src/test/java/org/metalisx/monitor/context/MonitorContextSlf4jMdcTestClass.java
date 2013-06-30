package org.metalisx.monitor.context;

import javax.inject.Named;

import org.metalisx.monitor.context.InterfaceMonitorContext;
import org.metalisx.monitor.context.MonitorContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MonitorContextSlf4jMdcTestClass {

    private static final Logger logger = LoggerFactory.getLogger(MonitorContextSlf4jMdcTestClass.class);

    public MonitorContextSlf4jMdcTestClass() {
    }

    public void run() {
        InterfaceMonitorContext monitorContext = MonitorContextFactory.getCurrentInstance();
        logger.info(monitorContext.format("A message"));
        logger.info(monitorContext.profileFormat("A message", 1));
    }

}
