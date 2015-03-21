package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import model.WebCrawler;

import org.junit.Test;

import view.ViewController;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebCrawlerTest {

	/**
	 * Tests logging into a page.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	@Test
	public void loginTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		final WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
		String oldPageTitle = "title";
		String newPageTitle = "title";
		HtmlPage page = client.getPage(WebCrawler.LOGIN_URL);
		oldPageTitle = page.getTitleText();
		page = WC.login(page, "***REMOVED***", "***REMOVED***");
		newPageTitle = page.getTitleText();
		assertTrue(oldPageTitle.equals(WebCrawler.SIGNIN_TITLE));
		assertTrue(newPageTitle.equals(WebCrawler.MAIN_TITLE));
		assertFalse(oldPageTitle.equals(newPageTitle));
	}
	
	/**
	 * Tests the logout action
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	@Test
	public void logoutTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		String oldPageTitle = "title";
		String newPageTitle = "title";
		HtmlPage page = WC.webClient.getPage(WebCrawler.LOGIN_URL);
		HtmlPage failedLogout = WC.logout(page);
		assertNull(failedLogout);
		oldPageTitle = page.getTitleText();
		page = WC.login(page, "***REMOVED***", "***REMOVED***");
		newPageTitle = page.getTitleText();
		assertTrue(oldPageTitle.equals(WebCrawler.SIGNIN_TITLE));
		assertTrue(newPageTitle.equals(WebCrawler.MAIN_TITLE));
		// logged in
		oldPageTitle = page.getTitleText();
		page = WC.logout(page);
		assertNotNull(page.getAnchorByText("Sign In"));
	}
	
	/**
	 * Tests the clicking of a link.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	@Test
	public void clickLinkTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		final HtmlPage page = WC.webClient.getPage("https://www.safeway.com");
		final HtmlPage newPage = WC.clickLink(page, "//a");
		assertNotNull(newPage);
		assertNotEquals(page, newPage);
	}
	
	/**
	 * Tests the 'running' attribute mutators
	 */
	@Test
	public void runningTest() {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		WC.setRunning(false);
		assertFalse(WC.getRunning());
		WC.setRunning(true);
		assertTrue(WC.getRunning());
	}
	
	/**
	 * Tests the username attribute mutators
	 */
	@Test
	public void usernameTest() {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		WC.setUsername("username");
		assertTrue("username".equals(WC.getUsername()));
		WC.setUsername("another");
		assertFalse("username".equals(WC.getUsername()));
	}
	
	/**
	 * Tests the password attribute mutators
	 */
	@Test
	public void passwordTest() {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		WC.setPassword("password");
		assertTrue("password".equals(WC.getPassword()));
		WC.setPassword("another");
		assertFalse("password".equals(WC.getPassword()));
	}
	
	/**
	 * Tests adding all coupons from a single page
	 * @throws IOException 
	 */
	@Test
	public void addAllCouponsFromPageTest() throws IOException {
		ViewController view = new ViewController();
		WebCrawler WC = view.model;
		final HtmlPage loginPage = WC.webClient.getPage(WebCrawler.LOGIN_URL);
		final HtmlPage page = WC.login(loginPage, "***REMOVED***", "***REMOVED***");
		assertNotNull(page);
		WC.addCouponsFromPage(WebCrawler.PERSONALIZED_URL, WebCrawler.PERSONALIZED_PAGE_TITLE);
		int shouldBeZero = WC.addCouponsFromPage(WebCrawler.PERSONALIZED_URL, WebCrawler.PERSONALIZED_PAGE_TITLE);
		assertEquals(0, shouldBeZero);
		WC.addCouponsFromPage(WebCrawler.COUPON_URL, WebCrawler.COUPON_PAGE_TITLE);
		shouldBeZero = WC.addCouponsFromPage(WebCrawler.COUPON_URL, WebCrawler.COUPON_PAGE_TITLE);
		assertEquals(0, shouldBeZero);
	}
	
	
	

}
