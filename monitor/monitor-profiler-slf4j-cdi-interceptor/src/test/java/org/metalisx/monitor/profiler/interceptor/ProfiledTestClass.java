package org.metalisx.monitor.profiler.interceptor;

import javax.inject.Named;

import org.metalisx.monitor.profiler.interceptor.Profile;

@Named
@Profile
public class ProfiledTestClass {

    public ProfiledTestClass() {
    }

    public void run() {
        assert (true);
    }

}
