//package com.twinspires.qa.ios.tests;
//
//import org.testng.annotations.Factory;
//import org.testng.annotations.Test;
//
///**
// * Created by dalwinder.singh on 10/10/18.
// */
//public class ProgramViewTest extends IOSAbstractTest  {
//
//    @Factory(dataProvider = "devices")
//    public ProgramViewTest(String platformName, String deviceName, String deviceVersion) {
//        deviceInfo.setPlatformName(platformName);
//        deviceInfo.setDeviceName(deviceName);
//        deviceInfo.setDeviceVersion(deviceVersion);
//    }
//
//    @Test(enabled = true, testName = "Verify video features and functionality",
//            groups = {"iOS.RaceInfo"})
//    public void verifyVideoFunctionality() {
//        String trackName = "";
//        String raceNumber = "";
//        int initialXCoordinate = 0;
//        int initialYCoordinate = 0;
//
//        // 1) Select the first race on By time view and verify video button is displayed on program page
//        trackName = todayRacesView.getFirstTrackNameByTimeView();
//        raceNumber = todayRacesView.getFirstTrackRaceNumberByTimeView();
//        todayRacesView.selectTrackOnByTimeView(trackName);
//        //TODO Verify the track name and race number once ids have been added to respective elements
////        assertTrue(programView.isProgramInfoDisplayed(trackName),"Verify selected track name is displayed correctly on program page");
////        assertTrue(programView.isProgramInfoDisplayed(raceNumber),"Verify race number is displayed correctly on program page");
//        assertTrue(programView.isVideoBtnDisplayed(), "Verify play video button is displayed correctly");
//
//        // 2) Click on video button and verify video container is displayed after logging in
//        programView.clickPlayVideoBtn();
//        //loginView.userLogin(getUsername("iOS General Account"),iosCredentials.getPassword());
//        assertTrue(programView.isVideoContainerDisplayed(),
//                "Verify video container is displayed after logging in");
//
//        // 3-9) Verify the video button and picture in picture  container functionality
//        //Click play video button again to hide video container
//        programView.clickPlayVideoBtn();
//        assertFalse(programView.isVideoContainerDisplayed(),
//                "Verify video container is not displayed after clicking video button again");
//        programView.clickPlayVideoBtn();
//        scroll("down");
//        assertTrue(programView.isPicInPicContainerDisplayed(),
//                "Verify picture in picture video container is displayed after scrolling down on program page");
//        initialXCoordinate = programView.getPipContainerXCoordinate();
//        initialYCoordinate = programView.getPipContainerYCoordinate();
//        moveByCoordinates(initialXCoordinate, initialYCoordinate,
//                programView.getPipContainerFinalXCoordinate(),programView.getPipContainerFinalYCoordinate());
//        assertTrue(programView.isPicInPicContainerMoved(initialXCoordinate, initialYCoordinate),
//                "Verify picture in picture video container can be moved inside the program view");
//        programView.clickOffersTab();
//        programView.clickRacesTab();
//        assertTrue(programView.isPicInPicContainerDisplayed(),
//                "Verify picture in picture video container is still displayed after switching back to races tab from another tab");
//        driver.navigate().back();
//        todayRacesView.selectTrackOnByTimeView(trackName);
//        assertFalse(programView.isPicInPicContainerDisplayed(),
//                "Verify picture in picture video container is not displayed after navigating back to races view and then back to program view");
//        programView.clickPlayVideoBtn();
//        scroll("down");
//        scroll("up");
//        assertFalse(programView.isPicInPicContainerDisplayed(),
//                "Verify picture in picture video container is not displayed after scrolling up on program page");
//        scroll("down");
//        programView.clickPipCloseBtn();
//        assertFalse(programView.isPicInPicContainerDisplayed(),
//                "Verify picture in picture video container is not displayed after clicking close button");
//      //  assertAll();
//    }
//
//
//}
