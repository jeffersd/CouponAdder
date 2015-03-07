package model;

import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
public class Main {
    private static final String LOGIN_URL = "https://www.safeway.com/ShopStores/OSSO-Login.page";
    private static final String USER_NAME = "***REMOVED***";
    private static final String PASSWORD = "***REMOVED***";
    private static final String PERSONALIZED_PAGE = "https://www.safeway.com/ShopStores/Justforu-PersonalizedDeals.page";
    private static final String COUPON_PAGE = "https://www.safeway.com/ShopStores/Justforu-CouponCenter.page";
    private static final String FILENAME = "safeway_coupons_added.txt";

    private static boolean loggedIn = false;
    private static int coupons = 0;
    private static int j4uDeals = 0;
    
    private static final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);

    private static LinkedList<String> itemsAdded = new LinkedList<String>();

    /**
     * @param args
     *            Standard command line arguments.
     * @throws Exception
     *             Web access.
     */
    public static void main(String[] args) throws Exception {
        // turn off annoying htmlunit warnings
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
                java.util.logging.Level.OFF);

        // note: Chrome fails to access safeway's website
        //webClient = new WebClient(BrowserVersion.FIREFOX_24);
        webClient.getCookieManager().setCookiesEnabled(true);

        System.out.println("Going to Safeway Login site..");
        final HtmlPage mainPage = webClient.getPage(LOGIN_URL);
        if (checkPage(mainPage.getTitleText(), "Safeway - Sign In")) {
            System.out.println("Logging in..");
            final HtmlPage loggedInPage = login(mainPage.getFormByName("Login"));
            if (checkPage(loggedInPage.getTitleText(),
                    "Safeway - Official Site")) {
                loggedIn = true;

                // go to coupons pages
                System.out.println("Going to the personalized deals page..");
                final HtmlPage personalizedDealsPage = webClient
                        .getPage(PERSONALIZED_PAGE);
                webClient.waitForBackgroundJavaScript(10000);
                if (checkPage(personalizedDealsPage.getTitleText(),
                        "Safeway - Personalized Deals")) {
                	HtmlSelect itemsPerPage = personalizedDealsPage.getFirstByXPath("//select[@id='j4u-items-per-page']");
                	itemsPerPage.setSelectedAttribute("-1", true);
                	webClient.waitForBackgroundJavaScript(10000);
                    j4uDeals = addAllCouponTypes(personalizedDealsPage);
                }

                System.out.println("Going to the coupon center page..");
                final HtmlPage couponCenterPage = webClient
                        .getPage(COUPON_PAGE);
                webClient.waitForBackgroundJavaScript(10000);
                if (checkPage(couponCenterPage.getTitleText(),
                        "Safeway - Coupon Center")) {
                	HtmlSelect itemsPerPage = couponCenterPage.getFirstByXPath("//select[@id='j4u-items-per-page']");
                	itemsPerPage.setSelectedAttribute("-1", true);
                	webClient.waitForBackgroundJavaScript(10000);
                    coupons = addAllCouponTypes(couponCenterPage);
                }

                logout(couponCenterPage);
                System.out.println("Closing windows..");
                webClient.closeAllWindows();
                System.out.println("Done");
                System.out.println("Total J4U Deals added: " + j4uDeals);
                System.out.println("Total regular coupons added: " + coupons);
                saveItemsAdded();
            } else {
                System.out.println("Couldn't log in.");
            }
        } else {
            System.out.println("Couldn't access Safeway's login site.");
        }
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
    private static HtmlPage login(HtmlForm loginForm) throws IOException {
        HtmlTextInput userIdField = loginForm.getInputByName("userId");
        HtmlPasswordInput passwordField = loginForm.getInputByName("IDToken2");
        userIdField.type(USER_NAME);
        passwordField.type(PASSWORD);
        HtmlAnchor signInButton = (HtmlAnchor) loginForm
                .getFirstByXPath("//a[@id='SignInBtn']");
        if (signInButton != null) {
            return signInButton.click();
        } else {
            return null;
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
    private static HtmlPage clickLink(HtmlPage page, String xpath)
            throws IOException {
        HtmlAnchor anchor = (HtmlAnchor) page.getFirstByXPath(xpath);
        if (anchor != null) {
            return anchor.click();
        } else {
            return null;
        }
    }

    private static int addAllCouponTypes(HtmlPage page) throws IOException {
        System.out.println("Adding Coupons..");
        int couponsAdded = 0;
        String couponXpathDivClass = "//div[@class='lt-offer  lt-border-enabled-offer lt-offer-program-";
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "mf lt-last-column']");
        couponsAdded += addAllCoupons(page, couponXpathDivClass + "sc lt-last-column']");
        return couponsAdded;
    }

    private static int addAllCoupons(HtmlPage page, String xpath)
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
            itemsAdded.add(name);
            couponAdded++;
            addCouponNode = page.getFirstByXPath(xpath);
        }
        return couponAdded;
    }

    private static boolean checkPage(String current, String desired) {
        if (!current.equals(desired)) {
            System.out.println("Went to the wrong page.");
            System.out.println("Should be: " + desired);
            System.out.println("But is: " + current);
            return false;
        } else {
            System.out.println("At page: " + current);
            return true;
        }
    }

    private static void logout(HtmlPage page) throws IOException {
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

    private static void saveItemsAdded() throws FileNotFoundException,
            UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(FILENAME, "UTF-8");
        for (String item : itemsAdded) {
            writer.println(item);
        }
        writer.close();
    }

}
