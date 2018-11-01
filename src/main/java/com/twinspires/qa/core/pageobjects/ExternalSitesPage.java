package com.twinspires.qa.core.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;

public class ExternalSitesPage extends AbstractPageObject {

    public ExternalSitesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    private String tweetBaseText = "https://" + testEnv.toString().toLowerCase() + ".twinspires.com/account-create-get-free-bets?referrer=getfreebets&promo_code=";
    public String facebookBaseText = "https://" + testEnv.toString().toLowerCase() + ".twinspires.com/account-create-get-free-bets?referrer=getfreebets&promo_code=";

    //PayPal (test) Components
    @FindBy(xpath = "//*[@id='loginSection']/div/div[2]/a")
    WebElement btnPayPalInitialLogin;
    @FindBy(id = "btnLogin")
    WebElement btnPayPalLogin;
    @FindBy(id = "confirmButtonTop")
    WebElement btnPayPalContinue;
    @FindBy(id = "transactionCart")
    WebElement lblPayPalCartTotal;
    @FindBy(id = "btnNext")
    WebElement btnPayPalNext;
    @FindBy(id = "email")
    WebElement lblPayPalUsername;
    @FindBy(id = "password")
    WebElement lblPayPalPassword;
    @FindBy(id = "paypalLogo")
    WebElement imgPayPal;
    @FindBy(xpath = "//section[@id='login']//*[@id='cancelLink']")
    WebElement lnkPayPalCancel;
    @FindBy(id = "defaultCancelLink")
    WebElement lnkPayPalReviewCancel;
    @FindBy(xpath = "//span[@class='amountFormatted']//span[@class='ltrOverride ng-binding']")
    WebElement lblPayPalBalance;

    @FindBy(css = "#totalWrapper span.ltrOverride")
    WebElement lblPayPalAmountDetails;
    @FindBy(css = ".detail-items.ng-scope>li:nth-child(1) .ltrOverride")
    WebElement lblPayPalDeposit;
    @FindBy(css = "li.amounts span.ltrOverride ")
    WebElement lblPayPalItemTotalAmount;
    @FindBy(css = "div.subTotal span.ltrOverride")
    WebElement lblPayPalSubTotalAmount;
    @FindBy(css = ".detail-items.ng-scope>li:nth-child(2) .ltrOverride")
    WebElement lblPayPalTransactionFee;


    //Prod (PayPal)
    @FindBy(css = "#miniCart > div.small.wrap.items.totals.item1 > ul > li > span")
    WebElement lblPayPalProdAmountDetails;
    @FindBy(id = "email")
    WebElement txtPayPalProdEmail;
    @FindBy(id = "password")
    WebElement txtPayPalProdPassword;
    @FindBy(id = "btnLogin")
    WebElement btnPayPalProdLogIn;
    @FindBy(xpath = "//button[@ng-click='continue()']")
    WebElement btnPayPalProdContinue;
    @FindBy(id = "confirmButtonTop")
    WebElement btnPaypalConfirmContinue;
    @FindBy(xpath = "//a[contains(@ng-click,'Login')]")
    WebElement btnPayPalProdInitialLogin;

//    //Paypal mobile prod site elements
//    @FindBy(id = "email")
//    WebElement txtPaypalMobileEmail;
//    @FindBy(id = "password")
//    WebElement txtPaypalMobilePassword;
//    @FindBy(id = "btnLogin")
//    WebElement btnPaypalMobileLogIn;
//    @FindBy(xpath = "//button[@ng-click='continue()']")
//    WebElement btnPaypalMobileContinue;
//    @FindBy(id = "confirmButtonTop")
//    WebElement btnPaypalMobileConfirmContinue;

    //Facebook Login elements
    @FindBy(id = "email")
    WebElement txtFacebookEmail;
    @FindBy(id = "pass")
    WebElement txtFacebookPassword;
    @FindBy(id = "loginbutton")
    WebElement btnFacebookLogin;
    @FindBy(id = "homelink")
    WebElement lblFacebookLogo;
    @FindBy(xpath = "//*[@name='share_action_properties']")
    WebElement lblFacebookURL;
    @FindBy(xpath = "//*[@class='unclickableMask']")
    WebElement lblFacebookMessage;

    //Mobile Facebook Login elements
    @FindBy(id = "m_login_email")
    WebElement txtMobileFacebookEmail;
    @FindBy(id = "m_login_password")
    WebElement txtMobileFacebookPassword;
    @FindBy(name = "login")
    WebElement btnMobileFacebookLogin;
    @FindBy(xpath = "//input[@name = 'dialog_url']")
    WebElement lblMobileFacebookURL;
    @FindBy(className = "sharerAttachment")
    WebElement lblMobileFacebookMessage;


    //Twitter Login elements
    @FindBy(id = "username_or_email")
    WebElement txtTwitterEmail;
    @FindBy(id = "password")
    WebElement txtTwitterPassword;
    @FindBy(className = "button selected sumbit")
    WebElement btnTwitterLogin;
    @FindBy(id = "homelink")
    WebElement lblTwitterLogo;
    @FindBy(id = "status")
    WebElement txtTwitterStatus;
    @FindBy(xpath = "//div[@id='bd']")
    private WebElement containerTweetPanel;
    @FindBy(xpath = "//textarea[@id='status']")
    private WebElement txtTweetStatus;

    public void clickPayPalInitialLoginButton() {
        waitForClickableElement(15, btnPayPalInitialLogin).click();
        sleepTime(500);
    }
    public void clickPayPalLoginButton() {
        waitForClickableElement(15, btnPayPalLogin).click();
    }

    public boolean isPayPalLogoDisplayed() {
        try {
            waitForPageLoaded();
            return waitForElement(15, imgPayPal).isDisplayed();
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isPayPalContinueButtonDisplayed() {
        try {
            return waitForClickableElement(15, btnPayPalContinue).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickPayPalContinueButton() {
        sleepTime(1000);
        waitForClickableElement(60, btnPayPalContinue).click();
        sleepTime(1000);
    }

    public String getPayPalCartTotal() {
        String[] total;
        total = waitForElement(15, lblPayPalCartTotal).getText().split(" ");

        return total[0];
    }

    public void clickPayPalNextButton() {
        waitForClickableElement(15,btnPayPalNext).click();
    }

    public void clickPayPalCancelLink() {
        waitForClickableElement(15, lnkPayPalCancel).click();
    }

    public void clickPayPalReviewCancelLink() {
        try {
            waitForClickableElement(15, lnkPayPalReviewCancel).click();
        } catch (Exception e) {
            waitForClickableElement(15, lnkPayPalReviewCancel).click();
        }
    }

    public void loginPayPal(String username, String password, boolean firstTime){
        //First time login will require one more step
        clickPayPalInitialLoginButton();
        waitForPageLoaded();
        sleepTime(1500);
        waitForElement(15, lblPayPalUsername).clear();
        sleepTime(500);
        lblPayPalUsername.sendKeys(username);

        if (firstTime) clickPayPalNextButton();

        sleepTime(500);
        waitForElement(15, lblPayPalPassword).sendKeys(password);
        clickPayPalLoginButton();
        waitForPageLoaded();
        waitForSpinner();
    }

    public BigDecimal getPayPalBalance() {
        waitForElementToBeVisible(lblPayPalBalance, 30);
        return formatBigDecimal(lblPayPalBalance.getText().trim());
    }

    public void clickPayPalAmountDetailsLink() {
        sleepTime(1000);
        waitForElement(10, lblPayPalAmountDetails).click();
    }

    public BigDecimal getPayPalDepositAmount() {
        return formatBigDecimal(lblPayPalDeposit.getText().trim());
    }

    public BigDecimal getPayPalItemTotalAmount() {
        return formatBigDecimal(lblPayPalItemTotalAmount.getText().trim());
    }

    public BigDecimal getPayPalSubTotalAmount() {
        return formatBigDecimal(lblPayPalSubTotalAmount.getText().trim());
    }

    public BigDecimal getPayPalTransactionFee() {
        return formatBigDecimal(lblPayPalTransactionFee.getText().trim());
    }

    public void waitForSpinner() {
        waitForElementToBeInvisible(By.id("spinner"),20);
    }

    // PROD
    public void enterPayPalProdUserName(String username) {
        driver.switchTo().defaultContent();
        txtPayPalProdEmail.clear();
        txtPayPalProdEmail.sendKeys(username);
    }

    // PROD
    public void enterPayPalProdPassword(String password) {
        waitForClickableElement(10, txtPayPalProdPassword).click();
        driver.switchTo().defaultContent();
        txtPayPalProdPassword.sendKeys(password);
    }

    // PROD
    public void clickPayPalProdLogInButton() {
        btnPayPalProdLogIn.click();
        waitForPageLoaded();
    }

    // PROD
//    public void clickPayPalProdContinueButton() {
//        sleepTime(10000);
//        waitForClickableElement(20, btnPayPalProdContinue).click();
//    }

    // PROD
    public BigDecimal getPayPalProdAmountDetails() {
        sleepTime(15000);
        return formatBigDecimal(lblPayPalProdAmountDetails.getText().trim());
    }

    public void paypalProdMobileLogIn(String email, String password) {
        waitForElement(10, btnPayPalInitialLogin).click();
        sleepTime(3000);
        waitForElement(10, txtPayPalProdEmail).click();
        txtPayPalProdEmail.clear();
        txtPayPalProdEmail.sendKeys(email);
        waitForElement(10,txtPayPalProdPassword).click();
        txtPayPalProdPassword.clear();
        txtPayPalProdPassword.sendKeys(password);
        //Click Submit
        btnPayPalProdLogIn.click();
    }

    public void clickPayPalProdContinueButton() {
        sleepTime(10000);
        waitForElement(20, btnPaypalConfirmContinue).click();
        sleepTime(3000);
    }

    public void clickInitialLoginBtn(){
        waitForElement(10, btnPayPalProdInitialLogin).click();
    }

    public void loginToFacebook() {
        String facebookUsername = "Louisvilleqa@gmail.com";
        String facebookPassword = "iGaming1";
        try {
            waitForElement(10, txtFacebookEmail).sendKeys(facebookUsername);
            waitForElement(10, txtFacebookPassword).sendKeys(facebookPassword);
            btnFacebookLogin.click();
        }
        catch (Exception e) {
            print(e.getMessage());
        }
    }

    public void loginToMobileFacebook() {
        String facebookUsername = "Louisvilleqa@gmail.com";
        String facebookPassword = "iGaming1";
        try {
            waitForElement(10, txtMobileFacebookEmail).sendKeys(facebookUsername);
            waitForElement(10, txtMobileFacebookPassword).sendKeys(facebookPassword);
            sleepTime(1000);
            waitForElement(15, btnMobileFacebookLogin).click();
        }
        catch (Exception e) {
            print(e.getMessage());
        }
    }

    public boolean isFacebookLoginPageDisplayed() {
        try {
            return waitForElement(10, lblFacebookLogo).isDisplayed();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String getFacebookURL() {
        waitForElement(15, lblFacebookMessage);
        String[] message = lblFacebookURL.getAttribute("value").split("\":\"");
        String[] message2 = message[1].replaceAll("[\"\\\\}]", "").split(",");
        return message2[0];
    }

    public String getMobileFacebookURL() throws UnsupportedEncodingException {
        waitForElement(15, lblMobileFacebookMessage);
        String[] message = lblMobileFacebookURL.getAttribute("value").split("sharer.php\\?u=");
        String[] message2;

        try {
            message2 = URLDecoder.decode(message[1], "UTF-8").split("&");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        return message2[0];
    }

    public String getTwitterStatus() {
        return waitForElement(15, txtTwitterStatus).getAttribute("value");
    }

    public boolean isTwitterStatusPageDisplayed() {
        try {
            return waitForElement(15, containerTweetPanel).isDisplayed();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    private String getTwitterPrefilledStatus() {
        try {
            return waitForElement(15, txtTweetStatus).getText().trim();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public boolean verifyTweetPrefillContainUrlAndCamId(String camId) {
        String tweet = getTwitterPrefilledStatus();
        try {
            assert tweet != null;
            return tweet.contains(tweetBaseText + camId);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
