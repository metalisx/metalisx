package org.metalisx.monitor.profiler.servlet.filter;

import javax.inject.Singleton;

@Singleton
public class ProfilerFilterContext {

    private boolean disableFilter = false;

    public boolean isDisableFilter() {
        return disableFilter;
    }

    public void setDisableFilter(boolean disableFilter) {
        this.disableFilter = disableFilter;
    }

}
