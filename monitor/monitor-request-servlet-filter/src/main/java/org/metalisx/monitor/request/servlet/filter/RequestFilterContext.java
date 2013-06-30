package org.metalisx.monitor.request.servlet.filter;

import javax.inject.Singleton;

@Singleton
public class RequestFilterContext {

    private boolean disableFilter = false;

    public boolean isDisableFilter() {
        return disableFilter;
    }

    public void setDisableFilter(boolean disableFilter) {
        this.disableFilter = disableFilter;
    }

}
