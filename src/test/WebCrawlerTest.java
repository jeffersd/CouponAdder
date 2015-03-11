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
	 */
	@Test
	public void loginTest() {
		WebCrawler WC = new WebCrawler(new ViewController());
		final WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
		String oldPageTitle = "title";
		String newPageTitle = "title";
		try {
			HtmlPage page = client.getPage("https://www.safeway.com/ShopStores/OSSO-Login.page");
			oldPageTitle = page.getTitleText();
			page = WC.login(page);
			newPageTitle = page.getTitleText();
			
			assertTrue(oldPageTitle.equals("Safeway - Sign In"));
			assertTrue(newPageTitle.equals("Safeway - Official Site"));
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertFalse(oldPageTitle.equals(newPageTitle));
	}
	
	/**
	 * Tests the clicking of a link.
	 */
	@Test
	public void clickLinkTest() {
		WebCrawler WC = new WebCrawler(new ViewController());
		final WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
		try {
			final HtmlPage page = client.getPage("https://www.safeway.com");
			final HtmlPage newPage = WC.clickLink(page, "//a");
			
			assertNotNull(newPage);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}

}
