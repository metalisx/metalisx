package org.metalisx.monitor.web.it.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverUtils {

	private static final int RENDER_WAIT_TIME = 1000; // milliseconds

	private static final String SCREENSHOT_DIRECTORY = "target/selenium-screenshots/";

	/**
	 * Helper method for fixing the Web Driver error
	 * "Element is not clickable at point" when adding components to the DOM
	 * tree with javascript. This fix seems to work for ChromeDriver and
	 * FirefoxDriver and is not needed For InternetExplorerDriver.
	 */
	public static void scrollIntoView(WebDriver webDriver, WebElement webElement) {
		if (webDriver instanceof ChromeDriver || webDriver instanceof FirefoxDriver) {
			((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0," + webElement.getLocation().y + ")");
		}
	}

	/**
	 * Helper method to handle some exceptions when a AJAX call is executed and
	 * on return a part of the page is rendered. To prevent the exceptions we
	 * give the rendering some time to finish, this means the test will always
	 * be delayed with this amount of time. If however the time is not enough to
	 * finish the rendering the problem can again occur. These execption occur
	 * when using the WebDriverWait to see if a DOM element is present after an
	 * AJAX call, it is possible that other DOM element are still being added.
	 * For some reason it seems this situation will hang the FireFox WebDriver
	 * and the Chrome driver will throw exceptions on consecutive commands on
	 * the driver, like "Element is not clickable at point" and
	 * "Element is no longer attached to the DOM". This fix seems to work for
	 * ChromeDriver and FirefoxDriver and is not needed For
	 * InternetExplorerDriver.
	 */
	public static void renderWait(WebDriver webDriver) throws InterruptedException {
		if (webDriver instanceof ChromeDriver || webDriver instanceof FirefoxDriver) {
			Thread.sleep(RENDER_WAIT_TIME);
		}
	}

	/**
	 * Helper method to take a screenshot from the current page the
	 * <code>driver</code> points to. It will be written to the
	 * {@link #SCREENSHOT_DIRECTORY} directory.
	 */
	public static void getScreenshot(WebDriver driver) throws IOException {
		if (driver instanceof TakesScreenshot) {
			File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(file, new File(SCREENSHOT_DIRECTORY + "screenshot.png"));
		}
	}

}
