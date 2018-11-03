package com.twinspires.qa.ios.pageobjects;

import com.twinspires.qa.core.testdata.TrackData;
import com.twinspires.qa.core.util.Util;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;
import com.twinspires.qa.ios.dataobjects.TodayRacesData;

/**
 * Created by dalwinder.singh on 9/5/18.
 */
public class TodayRacesView extends IOSPageObject {



    @FindBy(xpath = "//*[@value='EARLY LUCKITY 3']")
    WebElement RishiEarlyLuckity9;


    @FindBy(name = "pageHeader")
    WebElement lblTodaysRaces;
    @FindBy(id = "sortButton")
    WebElement btnSortView;
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name='FAVORITES']")
    WebElement lblFavoritesRaces;
    @FindBy(name = "Login")
    WebElement lnkLogin;
    @FindBy(xpath = "//XCUIElementTypeStaticText[@name='to see your favorites']")
    WebElement lblFavoriteLoginMsg;
    @FindBy(xpath = "(//XCUIElementTypeCell[@name='summaryTableViewCell'])[1]")
    WebElement firstRowByTimeView;
    String trackNamesXpath = "//XCUIElementTypeStaticText[@name='trackName']";
    String raceNumbersXpath = "//XCUIElementTypeStaticText[@name='raceLabel']";
    String raceMTPsXpath = "//XCUIElementTypeStaticText[@name='mtpLabel']";
    String allRacesXpath = "//XCUIElementTypeStaticText[@name='ALL RACES']";
    String pageSource = "";
    String allRacesForATrackByTrackViewXpath = "//XCUIElementTypeCell[XCUIElementTypeStaticText[@label='%s']]" +
            "//XCUIElementTypeCell[@name='raceCollectionCell']";
    String firstRaceForATrackByTrackViewXpath = "(//XCUIElementTypeCell[XCUIElementTypeStaticText[@label='%s']]" +
            "//XCUIElementTypeCell[@name='raceCollectionCell'])[1]";
    String trackNameByTimeViewXpath = "//XCUIElementTypeStaticText[@name='trackName' and @label='%s']";
    TodayRacesData todayRacesData;

    public TodayRacesView(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        todayRacesData = new TodayRacesData();

    }

    public List<String> getCurrentTrackNames() {
        System.out.print("1st Att : "  + pageSource);
        System.out.print("2st Att : "  + trackNamesXpath);
        List<String> trackNames = getAttributeValues(pageSource, trackNamesXpath, "label");
        return trackNames;
    }

    public List<String> getCurrentRaceNumbers() {
        List<String> raceNumbers = getAttributeValues(pageSource, raceNumbersXpath, "label");
        return raceNumbers;
    }

    public List<String> getCurrentRaceMTPs() {
        List<String> raceMTPs = getAttributeValues(pageSource, raceMTPsXpath, "label");
        return raceMTPs;
    }

    public String getTodayRacesLabel() {
        return waitForElement(5, lblTodaysRaces).getText();
    }

    public void getPageSource() {
        pageSource = driver.getPageSource();
    }

    public String getFavoritesRacesLabel() {
        return lblFavoritesRaces.getText();
    }

    public void clickRishiEarlyLuck9()
    {
        RishiEarlyLuckity9.click();
    }


    public String getAllRacesLabel() {
        return getAttributeValue(pageSource, allRacesXpath, "label");
    }

    public String getFavoritesLoginMsg() {
        return lnkLogin.getText() + " " + waitForElement(5, lblFavoriteLoginMsg).getText();
    }

    public int getTotalTracksDisplayed() {
        return getCurrentTrackNames().size();
    }

    public void clickSortBtn() {
        btnSortView.click();
    }

    public String getRandomTrackName() {
        List<String> trackNames = getCurrentTrackNames();
        return trackNames.get(Util.randomNumberForRange(0, trackNames.size()));
    }

    public int geRaceCountForATrack(String trackName) {
        List<String> races = getAttributeValues(pageSource, String.format(allRacesForATrackByTrackViewXpath, trackName), "name");
        print("Number of races: "+races.size());
        return races.size();
    }

    public String compareUpcomingRacesInfo() {
        List<String> trackNames = getCurrentTrackNames();
        List<String> raceNumbers = getCurrentRaceNumbers();
        List<String> raceMTPs = getCurrentRaceMTPs();
        String message = "";
        TrackData trackData;
        todayRacesData.loadTrackArray();

        for (int i = 0; i < trackNames.size(); i++) {
            if (!trackNames.get(i).equalsIgnoreCase("LATE LUCKITY 9")) {
                trackData = todayRacesData.getCurrentRaceInfo(trackNames.get(i));

                if (!raceNumbers.get(i).equalsIgnoreCase("RACE " + trackData.getRaceStr())) {
                    message += "\n For track " + trackNames.get(i) + " Expected: [" + "RACE " + trackData.getRaceStr() + "] Actual: [" + raceNumbers.get(i) + "]";
                }

                if (!raceMTPs.get(i).equalsIgnoreCase(trackData.getRaceStatus())) {
                    message += "\n For track " + trackNames.get(i) + " Expected: [ " + trackData.getRaceStatus() + "] Actual: [" + raceMTPs.get(i) + "]";
                }
            }
        }

        return message;
    }

    public boolean compareTotalTracksCount() {
        if (getTotalTracksDisplayed() == todayRacesData.getTotalTracks()) {
            return true;
        } else {
            print("Total tracks Expected: " + todayRacesData.getTotalTracks() + " Actual: " + getTotalTracksDisplayed());
            return false;
        }
    }

    public boolean isByTrackViewDisplayed() {
        String byTrackViewRaceCollectionCell = getAttributeValue(pageSource, String.format(firstRaceForATrackByTrackViewXpath,
                getRandomTrackName()), "name");
        if (byTrackViewRaceCollectionCell.equalsIgnoreCase("raceCollectionCell")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isByTimeViewDisplayed() {
        return waitForElement(5, firstRowByTimeView).isDisplayed();
    }

    public void selectTrackOnByTimeView(String trackName) {
        driver.findElement(By.xpath(String.format(trackNameByTimeViewXpath, trackName))).click();
    }

    public String getFirstTrackRaceNumberByTimeView() {
        return driver.findElement(By.xpath("(" + raceNumbersXpath + ")[1]")).getText();
    }

    public String getFirstTrackNameByTimeView() {
        return driver.findElement(By.xpath("(" + trackNamesXpath + ")[1]")).getText();
    }

    public void clickFirstRaceByTrackNameOnTrackView(String trackName) {
        String xpath = String.format(firstRaceForATrackByTrackViewXpath, trackName);
        //TODO Implement scroll to element
        driver.findElement(By.xpath(xpath)).click();
    }

}
