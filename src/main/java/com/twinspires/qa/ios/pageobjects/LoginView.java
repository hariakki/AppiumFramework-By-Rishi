package com.twinspires.qa.ios.pageobjects;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Created by dalwinder.singh on 8/9/18.
 */
public class LoginView extends IOSPageObject {

    @FindBy(name = "Username")
    WebElement txtBoxUsername;
    @FindBy(name = "Password")
    WebElement txtBoxPassword;
    @FindBy(name = "SIGN IN")
    WebElement btnSignIn;
    @FindBy(name = "Close")
    WebElement btnCloseSignInView;
    @FindBy(name = "Please provide valid Username or Password")
    WebElement lblInvalidCredentialsErrorMsg;
    @FindBy(name = "Enable Touch ID")
    WebElement lblEnableTouchId;
    @FindBy(xpath = "//XCUIElementTypeSwitch")
    WebElement btnEnableTouchId;
    @FindBy(name = "By signing in, I agree to the Terms & Conditions")
    WebElement lblTermsAndConditions;
    @FindBy(name = "JOIN NOW")
    WebElement btnJoinNow;
    @FindBy(name = "Forgot Password? Reset Here")
    WebElement lblForgotPasswordMsg;

    public LoginView(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void userLogin(String usernameData, String passwordData) {
        waitForElement(5,txtBoxUsername).clear();
        txtBoxPassword.clear();
        txtBoxUsername.sendKeys(usernameData);
        txtBoxPassword.sendKeys(passwordData);
        btnSignIn.click();
    }

    public void clickCloseSignInViewBtn(){
        btnCloseSignInView.click();
    }

    public boolean isInvalidCredentialsErrorMsg(){
        try {
            return waitForElement(5, lblInvalidCredentialsErrorMsg).isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public boolean isTouchIDElementsDisplayed(){
        boolean touchIdDispalyed = false;
        try {
            if(btnEnableTouchId.isDisplayed() && lblEnableTouchId.isDisplayed()) {
                touchIdDispalyed = true;
            }
            return touchIdDispalyed;
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public boolean isTermsAndConditionsTextDisplayed(){
        try {
            return lblTermsAndConditions.isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public boolean isJoinNowBtnDisplayed(){
        try {
            return btnJoinNow.isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public boolean isForgotPasswordTextDisplayed(){
        try {
            return lblForgotPasswordMsg.isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public boolean isLoginFieldsDisplayed(){
        try {
            if(waitForElement(5,txtBoxUsername).isDisplayed() &&
                    waitForElement(5,txtBoxPassword).isDisplayed()) {
                return btnJoinNow.isDisplayed();
            } else {
                return false;
            }
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }
}
