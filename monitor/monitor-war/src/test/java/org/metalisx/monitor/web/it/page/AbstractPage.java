package org.metalisx.monitor.web.it.page;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AbstractPage {

    protected static final int REQUEST_WAIT_THRESHOLD = 3; // seconds

    private WebDriver driver;

    private WebDriverWait webDriverWait;

    private URL contextPath;

    public AbstractPage(WebDriver driver, URL contextPath) {
        this.driver = driver;
        this.contextPath = contextPath;
        this.webDriverWait = new WebDriverWait(driver, REQUEST_WAIT_THRESHOLD);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriverWait getWebDriverWait() {
        return webDriverWait;
    }

    public void setWebDriverWait(WebDriverWait webDriverWait) {
        this.webDriverWait = webDriverWait;
    }

    public URL getContextPath() {
        return contextPath;
    }

    public void setContextPath(URL contextPath) {
        this.contextPath = contextPath;
    }
    
}
