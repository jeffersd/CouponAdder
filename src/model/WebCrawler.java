package model;

import java.io.IOException;
import java.util.Observable;

import view.ViewController;

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
public class WebCrawler extends Observable implements Runnable {
    private static final String LOGIN_URL = "https://www.safeway.com/ShopStores/OSSO-Login.page";
    public static final String PERSONALIZED_PAGE = "https://www.safeway.com/ShopStores/Justforu-PersonalizedDeals.page";
    public static final String COUPON_PAGE = "https://www.safeway.com/ShopStores/Justforu-CouponCenter.page";
    public static final String SIGNIN_TITLE = "Safeway - Sign In";
    public static final String MAIN_TITLE = "Safeway - Sign In";
    public static final String COUPON_PAGE_TITLE = "Safeway - Sign In";
    public static final String PERSONALIZED_PAGE_TITLE = "Safeway - Sign In";
    public static final String COUPONS_PER_PAGE_XPATH = "//select[@id='j4u-items-per-page']";
    private String username;
    private String password;
    private boolean loggedIn;
    private boolean running;
    
    public final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);

    public WebCrawler(ViewController observer) {
    	addObserver(observer);
    	java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
                java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache").setLevel(
                java.util.logging.Level.OFF);
        webClient.getCookieManager().setCookiesEnabled(true);
        username = "";
        password = "";
        running = false;
        loggedIn = false;
    }
    
    /**
     * The main method that adds all of the coupons.
     */
    public void addAllCoupons() {
    	updateStatus("Initializing..");
    	int couponsAdded = 0;
        HtmlPage mainPage;
		try {
			mainPage = webClient.getPage(LOGIN_URL);
		} catch (Exception e) {
			updateStatus("Error opening login page");
			return;
		} 
        if (mainPage.getTitleText().equals(SIGNIN_TITLE)) {
            HtmlPage loggedInPage;
			try {
				loggedInPage = login(mainPage, username, password);
			} catch (IOException e2) {
				updateStatus("Error logging in");
				return;
			}
            if (loggedInPage.getTitleText().equals(MAIN_TITLE)) {
                loggedIn = true;
                updateStatus("Logged in");
                addCouponsFromPage(PERSONALIZED_PAGE, PERSONALIZED_PAGE_TITLE);
                addCouponsFromPage(COUPON_PAGE, COUPON_PAGE_TITLE);
                try {
					logout(loggedInPage);
				} catch (IOException e) {
					//e.printStackTrace();
					updateStatus("Error logging out.");
				}
                webClient.closeAllWindows();
                updateStatus("Done, added: " + couponsAdded + " coupons.");
            } else {
                updateStatus("Couldn't log in with username: " + username + "and password: " + password);
            }
        } else {
            updateStatus("Couldn't access Safeway's login site");
        }
    }
    
    public int addCouponsFromPage(String url, String title) {
    	final HtmlPage page;
    	int couponsAdded = 0;
		try {
			//updateStatus("Going to " + pageName + " page..");
			page = webClient.getPage(url);
		} catch (Exception e1) {
			//e1.printStackTrace();
			//updateStatus("Error loading " + pageName + " Page.");
			return -1;
		}
        webClient.waitForBackgroundJavaScript(10000);
        if (page.getTitleText().equals(title)) {
        	//updateStatus("At " + pageName + " page");
        	HtmlSelect itemsPerPage = page.getFirstByXPath(COUPONS_PER_PAGE_XPATH);
        	itemsPerPage.setSelectedAttribute("-1", true);
        	webClient.waitForBackgroundJavaScript(10000);
            try {
				couponsAdded = addAllCouponTypes(page);
			} catch (IOException e) {
				//e.printStackTrace();
				//updateStatus("Error adding coupons from " + pageName + " page");
				couponsAdded = -1;
			}
        } else {
        	couponsAdded = -1;
        }
//        try {
//			//logout(page);
//		} catch (IOException e) {
//			//e.printStackTrace();
//			updateStatus("Error logging out");
//		}
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
    public HtmlPage login(HtmlPage page, String username, String password) throws IOException {
    	updateStatus("Logging in..");
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
        	loggedIn = true; // wrong?
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
				return null;
			}
        }
        return null;
    }

    private int addAllCouponTypes(HtmlPage page) throws IOException {
        updateStatus("Looking for coupons..");
        int couponsAdded = 0;
        String couponXpathDivClass = "//div[@class='lt-offer  lt-border-enabled-offer lt-offer-program-";
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf lt-last-column']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc lt-last-column']");
        return couponsAdded;
    }

    private int addAllCoupons(HtmlPage page, String xpath) throws IOException {
        int couponAdded = 0;
        DomNode addCouponNode = page.getFirstByXPath(xpath);
        while (addCouponNode != null) {
            HtmlAnchor addAnchor = addCouponNode.getFirstByXPath(xpath
                    + "//a[@class='lt-add-offer-link lt-buttonContainer']");
            HtmlSpan addSpan = addAnchor.getFirstByXPath("./span");
            page = addSpan.click();
            webClient.waitForBackgroundJavaScript(10000);
            String name = addAnchor.getAttribute("title");
            name = name.substring(4); // remove 'add ' from title
            updateStatus("Added: " + name);
            couponAdded++;
            addCouponNode = page.getFirstByXPath(xpath);
        }
        return couponAdded;
    }

    public HtmlPage logout(final HtmlPage page) throws IOException {
        if (loggedIn) {
            updateStatus("Logging out..");
            //final HtmlPage loggedOffPage = clickLink(page,"//a[@href='javascript:openssoLogoff();']");
            final HtmlAnchor signOutLink = page.getAnchorByText("Sign Out");
            final HtmlPage loggedOffPage = signOutLink.click();
            if (loggedOffPage != null) {
            	updateStatus("At page: " + loggedOffPage.getTitleText());
            }
            return loggedOffPage;
        }
        return null;
    }
    
    public void updateStatus(String newStatus) {
    	setChanged();
    	notifyObservers(newStatus);
    }
    
    public void setUsername(String newUsername) {
    	username = newUsername;
    }
    
    public String getUsername() {
    	return username;
    }
    
    public void setPassword(String newPassword) {
    	password = newPassword;
    }
    
    public String getPassword() {
    	return password;
    }
    
    public void setRunning(boolean newValue) {
    	running = newValue;
    }
    
    public boolean getRunning() {
    	return running;
    }

	@Override
	public void run() {
		setRunning(true);
		addAllCoupons();
		setRunning(false);
	}

}
