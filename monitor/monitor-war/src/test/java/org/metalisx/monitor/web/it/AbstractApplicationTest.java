package org.metalisx.monitor.web.it;

import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.metalisx.monitor.web.it.junit.rule.ScreenshotTestWatchman;
import org.openqa.selenium.WebDriver;

public class AbstractApplicationTest {

	@Rule
    public ScreenshotTestWatchman screenshotTestWatchman = new ScreenshotTestWatchman();

	@Drone
	protected WebDriver webDriver;

	@ArquillianResource
	protected URL contextPath;

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws ZipException, IOException {
		return Deployments.createDeployment();
	}

	@Before
	public void postConstruct() {
		screenshotTestWatchman.setWebDriver(webDriver);
	}
	
	public WebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	public URL getContextPath() {
		return contextPath;
	}

	public void setContextPath(URL contextPath) {
		this.contextPath = contextPath;
	}

}
