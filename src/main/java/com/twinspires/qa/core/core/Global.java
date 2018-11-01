package com.twinspires.qa.core.core;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import java.util.ArrayList;

/**
 * Created by dalwinder.singh on 8/2/16.
 */
public abstract class Global {

    String mainWindow = "";

    public WebElement waitForElement(WebDriver driver, int milliseconds, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, milliseconds);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void printTestCaseNameToReport(String testCaseDescription){
        printLine(System.getProperty("line.separator"));
        String testName = "Executing test case: "+testCaseDescription;
        String dottedLine = "";
        for(int i=0;i<testName.length();i++){
            dottedLine = dottedLine+"-";
        }
        printLine(dottedLine);
        printLine(testName);
        printLine(dottedLine);
    }

    public void printLine(String statement){
        Reporter.log(statement);
        System.out.println(statement);
    }

    public void switchFocusNewTab(WebDriver driver) {
        mainWindow = driver.getWindowHandle();
        ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(allTabs.get(allTabs.size()-1));
    }

    public void waitForElementToBeInvisible(WebDriver driver, By locator, int timeInSeconds) {
        new WebDriverWait(driver, (long)timeInSeconds).until(ExpectedConditions.
                invisibilityOfElementLocated(locator));
    }
    public void waitForElementToBeVisible(WebDriver driver, By locator, int timeInSeconds) {
        new WebDriverWait(driver, (long)timeInSeconds).until(ExpectedConditions.
                presenceOfElementLocated(locator));
    }

    public void switchToMainwindow(WebDriver driver){
        driver.switchTo().window(mainWindow);
    }

    public void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1)
                .executeScript("return document.readyState").equals("complete");
    }

}
