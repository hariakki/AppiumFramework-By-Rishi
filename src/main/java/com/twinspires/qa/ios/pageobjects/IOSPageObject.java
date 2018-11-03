package com.twinspires.qa.ios.pageobjects;

import com.twinspires.qa.core.pageobjects.AbstractPageObject;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by dalwinder.singh on 8/9/18.
 */
public class IOSPageObject extends AbstractPageObject {

    @FindBy(name = "Login")
    WebElement lnkLogin;
    @FindBy(name = "Logout")
    WebElement lnkLogout;
    @FindBy(xpath = "(//XCUIElementTypeButton[@name='Logout'])[2]")
    WebElement btnLogout;
    @FindBy(name = "Claim Offer")
    WebElement lnkClaimOffer;
    @FindBy(name = "Offers")
    WebElement tabOffers;
    @FindBy(name = "Races")
    WebElement tabRaces;

    public IOSPageObject(WebDriver driver) {
        super(driver);
    }

    public void clickLoginLink(){
        waitForElement(5,lnkLogin).click();
    }

    public boolean isLogoutBtnDisplayed(){
        try {
            return waitForElement(5, lnkLogout).isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public void clickLogoutLink(){
        waitForElement(5,lnkLogout).click();
    }

    public void clickLogoutBtn(){
        waitForElement(5,btnLogout).click();
    }

    public void clickClaimOfferLink(){
        waitForElement(5,lnkClaimOffer).click();
    }

    public boolean isLoginLinkDisplayed(){
        try {
            return waitForElement(5, lnkLogin).isDisplayed();
        } catch (ElementNotVisibleException ex){
            return false;
        }
    }

    public void clickOffersTab(){
        waitForElement(5,tabOffers).click();
    }
    public void clickRacesTab(){
        waitForElement(5,tabRaces).click();
    }
}
