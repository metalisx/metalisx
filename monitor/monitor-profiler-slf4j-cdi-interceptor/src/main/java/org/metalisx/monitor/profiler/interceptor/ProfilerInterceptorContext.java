package org.metalisx.monitor.profiler.interceptor;

import javax.inject.Singleton;

@Singleton
public class ProfilerInterceptorContext {

    private boolean disableInterceptor = false;

    public boolean isDisableInterceptor() {
        return disableInterceptor;
    }

    public void setDisableInterceptor(boolean disableInterceptor) {
        this.disableInterceptor = disableInterceptor;
    }

}
