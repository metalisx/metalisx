package org.metalisx.monitor.web.it.page;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.metalisx.monitor.web.it.utils.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RequestsPage extends AbstractPage {

	private static final String PAGE = "page/index.html#/requests";
	private static final String ID_URL = "url";
	private static final String ID_SEARCH = "search";
	private static final String XPATH_NAVBAR = "//div[contains(@class,'navbar')]";
	private static final String XPATH_FIRST_TR = "//div[contains(@id,'requests')]//tr[1]";
	private static final String XPATH_FIRST_TR_CONTAINING_SEARCH_RESULT = "//div[contains(@id,'requests')]//table/tbody/tr[1]/td[4]/div[contains(text(),'http://localhost/testapplication')]";

	public RequestsPage(WebDriver driver, URL contextPath) {
		super(driver, contextPath);
	}

	public void test() throws Exception {
		getDriver().get(getContextPath() + PAGE);
		WebDriverUtils.renderWait(getDriver());
		getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_NAVBAR)));
		getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_FIRST_TR)));
		WebElement urlInput = getDriver().findElement(By.id(ID_URL));
		WebDriverUtils.scrollIntoView(getDriver(), urlInput);
		urlInput.sendKeys("testapplication");
		WebElement searchButton = getDriver().findElement(By.id(ID_SEARCH));
		WebDriverUtils.scrollIntoView(getDriver(), searchButton);
		searchButton.click();
		WebDriverUtils.renderWait(getDriver());
		getWebDriverWait().until(
		        ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_FIRST_TR_CONTAINING_SEARCH_RESULT)));
		String result = getDriver().findElement(By.xpath(XPATH_FIRST_TR_CONTAINING_SEARCH_RESULT)).getText();
		assertEquals("http://localhost/testapplication", result);
	}

}
