package com.twinspires.qa.ios.tests;

import com.twinspires.qa.core.testobjects.DeviceInfo;
import com.twinspires.qa.core.tests.AbstractTest;
import com.twinspires.qa.core.util.Driver;
import com.twinspires.qa.ios.pageobjects.LoginView;
import com.twinspires.qa.ios.pageobjects.ProgramView;
import com.twinspires.qa.ios.pageobjects.SpeedClassPAceView;
import com.twinspires.qa.ios.pageobjects.TodayRacesView;
import com.twinspires.qa.ios.testdata.IOSCredentials;
import com.twinspires.qa.ios.dataobjects.TodayRacesData;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

/**
 * Created by dalwinder.singh on 8/9/18.
 */
public class IOSAbstractTest extends AbstractTest {

    protected Driver driverInstance;
    protected WebDriver driver;
    public DeviceInfo deviceInfo = new DeviceInfo();
    LoginView loginView;
    IOSCredentials iosCredentials;
    TodayRacesData todayRacesData;
    TodayRacesView todayRacesView;
    ProgramView programView;
    SpeedClassPAceView speedclasspaceView;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        driverInstance = new Driver(deviceInfo);
        driver = driverInstance.getDriver();
        loginView = new LoginView(driver);
        iosCredentials = new IOSCredentials(testEnv);
        todayRacesData = new TodayRacesData();
        todayRacesView = new TodayRacesView(driver);
        programView = new ProgramView(driver);
        speedclasspaceView = new SpeedClassPAceView(driver);
    }

    @AfterMethod(enabled = true)
    public void tearDown(){
     //  ((IOSDriver) driver).removeApp("com.twinspires.mobile");

    }

    public void resetApp(){
       // ((IOSDriver) driver).resetApp();
        //sleepTime(2000);
    }

    public void runAppInBackground(long seconds){
      //  ((IOSDriver) driver).runAppInBackground(Duration.ofSeconds(seconds));
       // sleepTime(2000);
    }

    public void scroll(String direction){
      HashMap<String, String> scrollObject = new HashMap<>();
      scrollObject.put("direction", direction);
        ((IOSDriver)driver).executeScript("mobile: scroll", scrollObject);
    }

    public void moveByCoordinates(int startX, int startY, int endX, int endY) {
       // TouchAction action = new TouchAction((PerformsTouchActions) driver);
       // action.longPress(PointOption.point(startX, startY)).
              //  waitAction(WaitOptions.waitOptions(Duration.ofMillis(2000))).
             //  moveTo(PointOption.point(endX, endY)).perform().release();
    }

}
