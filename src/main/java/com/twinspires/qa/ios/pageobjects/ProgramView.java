package com.twinspires.qa.ios.pageobjects;

import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * Created by dalwinder.singh on 9/26/18.
 */
public class ProgramView extends IOSPageObject {

    @FindBy(xpath = "//XCUIElementTypeNavigationBar/XCUIElementTypeButton[@name='Item']")
    WebElement btnPlayVideo;
    @FindBy(name = "videoPanel")
    WebElement videoContainer;
    @FindBy(id = "Central Play")
    WebElement pipVideoContainer;
    @FindBy(id = "Close")
    WebElement btnClosePipVideoContainer;
    String programLabelNavBarXpath = "//XCUIElementTypeNavigationBar/XCUIElementTypeStaticText[@name='%s']";

    public ProgramView(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public boolean isProgramInfoDisplayed(String label){
        sleepTime(5000);
        return driver.findElement(By.xpath(String.format(programLabelNavBarXpath, label))).isDisplayed();
    }

    public boolean isVideoBtnDisplayed(){
        return waitForElement(5,btnPlayVideo).isDisplayed();
    }
    public void clickPlayVideoBtn(){
        btnPlayVideo.click();
    }

    public boolean isVideoContainerDisplayed(){
        try{
            return waitForElement(5,videoContainer).isDisplayed();
        } catch (Exception e){
            return false;
        }
    }

    public boolean isPicInPicContainerDisplayed(){
        try{
            return waitForElement(5,pipVideoContainer).isDisplayed();
        } catch (Exception e){
            return false;
        }
    }

    public void clickPipCloseBtn(){
        btnClosePipVideoContainer.click();
    }

    public int getPipContainerXCoordinate(){
      return pipVideoContainer.getLocation().getX();
    }

    public int getPipContainerYCoordinate(){
        return pipVideoContainer.getLocation().getY();
    }

    public int getPipContainerFinalYCoordinate(){
        return driver.manage().window().getSize().height/2;
    }

    public int getPipContainerFinalXCoordinate(){
        return driver.manage().window().getSize().width/2;
    }

    public boolean isPicInPicContainerMoved(int pipInitialXCoordinate, int pipInitialYCoordinate){
        if(pipInitialXCoordinate < getPipContainerFinalXCoordinate() &&
                pipInitialYCoordinate > getPipContainerFinalYCoordinate()){
            return true;
        } else {
            return false;
        }
    }
}
