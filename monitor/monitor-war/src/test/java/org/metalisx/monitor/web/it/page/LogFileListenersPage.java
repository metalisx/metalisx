package org.metalisx.monitor.web.it.page;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.After;
import org.metalisx.monitor.web.it.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LogFileListenersPage extends AbstractPage {

    private static final String PAGE = "page/log-file-listeners.html";
    private static final String ID_START_LISTENER = "startListener";
    private static final String XPATH_NAVBAR = "//div[contains(@class,'navbar')]";
    private static final String XPATH_STOP = "//div[contains(@id,'fileListeners')]//a";

    public LogFileListenersPage(WebDriver driver, URL contextPath) {
        super(driver, contextPath);
    }
    
    @After
    public void be() throws IOException {
        WebDriverUtils.getScreenshot(getDriver());
    }
    
    public void test() throws Exception {
        getDriver().get(getContextPath() + PAGE);
        WebDriverUtils.renderWait(getDriver());
        getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_NAVBAR)));
        getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.id(ID_START_LISTENER)));
        WebElement startListenerButton = getDriver().findElement(By.id(ID_START_LISTENER));
        WebDriverUtils.scrollIntoView(getDriver(), startListenerButton);
        startListenerButton.click();
        WebDriverUtils.renderWait(getDriver());
        getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_STOP)));
        String result = getDriver().findElement(By.xpath(XPATH_STOP)).getText();
        Assert.assertEquals("Stop", result);
    }

}
