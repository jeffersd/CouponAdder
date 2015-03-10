package model;

import java.io.IOException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Main class to run the app. Logs into safeway and adds coupons to the card.
 * 
 * @author dillon
 * 
 */
public class WebCrawler {
    private static final String LOGIN_URL = "https://www.safeway.com/ShopStores/OSSO-Login.page";
    private static final String PERSONALIZED_PAGE = "https://www.safeway.com/ShopStores/Justforu-PersonalizedDeals.page";
    private static final String COUPON_PAGE = "https://www.safeway.com/ShopStores/Justforu-CouponCenter.page";

    private String username;
    private String password;
    private boolean loggedIn = false;
    private String currentStatus;
    
    public final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);

    public WebCrawler() {
    	java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
                java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache").setLevel(
                java.util.logging.Level.OFF);
        webClient.getCookieManager().setCookiesEnabled(true);
        username = "";
        password = "";
    }

    /**
     * @param args
     *            Standard command line arguments.
     * @throws Exception
     *             Web access.
     */
    public int addAllCoupons() {
    	int couponsAdded = 0;
        HtmlPage mainPage;
		try {
			mainPage = webClient.getPage(LOGIN_URL);
		} catch (Exception e) {
			e.printStackTrace();
			updateStatus("Exception opening login page.");
			return -1;
		} 
        if (mainPage.getTitleText().equals("Safeway - Sign In")) {
            HtmlPage loggedInPage;
			try {
				loggedInPage = login(mainPage);
			} catch (IOException e2) {
				e2.printStackTrace();
				updateStatus("Error logging in.");
				return -1;
			}
            if (loggedInPage.getTitleText().equals("Safeway - Official Site")) {
                loggedIn = true;
                HtmlPage personalizedDealsPage;
				try {
					personalizedDealsPage = webClient.getPage(PERSONALIZED_PAGE);
				} catch (Exception e2) {
					e2.printStackTrace();
					updateStatus("Error loading Personalized Deals Page.");
					return -1;
				}
                webClient.waitForBackgroundJavaScript(10000);
                if (personalizedDealsPage.getTitleText().equals("Safeway - Personalized Deals")) {
                	HtmlSelect itemsPerPage = personalizedDealsPage.getFirstByXPath("//select[@id='j4u-items-per-page']");
                	itemsPerPage.setSelectedAttribute("-1", true);
                	webClient.waitForBackgroundJavaScript(10000);
                    try {
						couponsAdded = addAllCouponTypes(personalizedDealsPage);
					} catch (IOException e) {
						e.printStackTrace();
						updateStatus("Error adding coupons from Personalized Page.");
						return -1;
					}
                }

                HtmlPage couponCenterPage;
				try {
					couponCenterPage = webClient.getPage(COUPON_PAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
					updateStatus("Error loading Coupon Center Page.");
					return -1;
				}
                webClient.waitForBackgroundJavaScript(10000);
                if (couponCenterPage.getTitleText().equals("Safeway - Coupon Center")) {
                	HtmlSelect itemsPerPage = couponCenterPage.getFirstByXPath("//select[@id='j4u-items-per-page']");
                	itemsPerPage.setSelectedAttribute("-1", true);
                	webClient.waitForBackgroundJavaScript(10000);
                    try {
						couponsAdded += addAllCouponTypes(couponCenterPage);
					} catch (IOException e) {
						e.printStackTrace();
						updateStatus("Error adding coupons from Coupon Center Page.");
						return -1;
					}
                }

                try {
					logout(couponCenterPage);
				} catch (IOException e) {
					e.printStackTrace();
					updateStatus("Error logging out.");
				}
                updateStatus("Closing windows..");
                webClient.closeAllWindows();
                updateStatus("Done");
            } else {
                updateStatus("Couldn't log in with username: " + username + "and password: " + password);
                couponsAdded = -1;
            }
        } else {
            updateStatus("Couldn't access Safeway's login site.");
            couponsAdded = -1;
        }
        return couponsAdded;
    }

    /**
     * This method logs in to a form and returns the page given by clicking the
     * sign in button.
     * 
     * @param loginForm
     *            The form to fill out
     * @return The page after clicking 'Sign In'
     * @throws IOException
     *             IOException
     */
    public HtmlPage login(HtmlPage page) throws IOException {
    	HtmlForm loginForm = page.getFormByName("Login");
    	if (loginForm == null) {
    		return null;
    	}
        HtmlTextInput userIdField = loginForm.getInputByName("userId");
        HtmlPasswordInput passwordField = loginForm.getInputByName("IDToken2");
        if (userIdField == null || passwordField == null) {
        	return null;
        }
        userIdField.type(username);
        passwordField.type(password);
        HtmlAnchor signInButton = (HtmlAnchor) loginForm
                .getFirstByXPath("//a[@id='SignInBtn']");
        if (signInButton == null) {
            return null;
        } else {
        	return signInButton.click();
        }
    }

    /**
     * This method clicks a link and returns the new page.
     * 
     * @param page
     *            The page we need to search
     * @param xpath
     *            The xpath we will use
     * @return The new page
     * @throws IOException
     *             IO Exception
     */
    public HtmlPage clickLink(HtmlPage page, String xpath) {
        HtmlAnchor anchor = page.getFirstByXPath(xpath);
        if (anchor != null) {
        	try {
				return anchor.click();
			} catch (IOException e) {
				//e.printStackTrace();
			}
        }
        
        return null;
    }

    private int addAllCouponTypes(HtmlPage page) throws IOException {
        System.out.println("Adding Coupons..");
        int couponsAdded = 0;
        String couponXpathDivClass = "//div[@class='lt-offer  lt-border-enabled-offer lt-offer-program-";
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf lt-last-column']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc lt-last-column']");
        return couponsAdded;
    }

    private int addAllCoupons(HtmlPage page, String xpath)
            throws IOException {
        int couponAdded = 0;
        DomNode addCouponNode = page.getFirstByXPath(xpath);
        while (addCouponNode != null) {
            HtmlAnchor addAnchor = addCouponNode.getFirstByXPath(xpath
                    + "//a[@class='lt-add-offer-link lt-buttonContainer']");
            HtmlSpan addSpan = addAnchor.getFirstByXPath("./span");
            page = addSpan.click();
            webClient.waitForBackgroundJavaScript(10000);
            //page = addAnchor.click();
            //webClient.waitForBackgroundJavaScript(10000);
            String name = addAnchor.getAttribute("title");
            name = name.substring(4); // remove 'add ' from title
            System.out.println("Added: " + name);
            couponAdded++;
            addCouponNode = page.getFirstByXPath(xpath);
        }
        return couponAdded;
    }

    private void logout(HtmlPage page) throws IOException {
        if (loggedIn) {
            System.out.println("Logging out..");
            final HtmlPage loggedOffPage = clickLink(page,
                    "//a[@href='javascript:openssoLogoff();']");
            if (loggedOffPage == null) {
                System.out.println("Didn't find logout button.");
            } else {
                System.out.println("At page: " + loggedOffPage.getTitleText());
            }
        }
    }
    
    public void updateStatus(String newStatus) {
    	System.out.println(newStatus);
    	currentStatus = newStatus;
    }
    
    public void setUsername(String newUsername) {
    	username = newUsername;
    }
    
    public void setPassword(String newPassword) {
    	password = newPassword;
    }

}
