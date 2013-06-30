package org.metalisx.monitor.web.it.junit.rule;

import java.io.IOException;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.metalisx.monitor.web.it.utils.WebDriverUtils;
import org.openqa.selenium.WebDriver;

/**
 * Class for capturing a screenshot when a assertion fails.
 * 
 * Usage.
 * 
 * First create a class property, initialize is with a new instance of this class
 * and annotate it with @Rule
 * 
 * Then set the web driver in a method annotated with @Before.
 */
public class ScreenshotTestWatchman extends TestWatchman {

	private WebDriver webDriver;
	
	public ScreenshotTestWatchman() {
	}
	
	@Override
    public void failed(Throwable e, FrameworkMethod method) {
		if (webDriver != null) {
			try {
		        WebDriverUtils.getScreenshot(webDriver);
	        } catch (IOException e1) {
		        e1.printStackTrace();
	        }
		}
	    super.failed(e, method);
    }

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
}
