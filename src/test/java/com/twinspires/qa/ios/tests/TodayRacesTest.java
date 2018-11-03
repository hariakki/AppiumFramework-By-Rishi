//package com.twinspires.qa.ios.tests;
//
//import org.testng.annotations.Factory;
//import org.testng.annotations.Test;
//import java.util.List;
//
///**
// * Created by dalwinder.singh on 9/5/18.
// */
//
//public class TodayRacesTest extends IOSAbstractTest {
//
//    @Factory(dataProvider = "devices")
//    public TodayRacesTest(String platformName, String deviceName, String deviceVersion) {
//        deviceInfo.setPlatformName(platformName);
//        deviceInfo.setDeviceName(deviceName);
//        deviceInfo.setDeviceVersion(deviceVersion);
//    }
//
//    @Test(enabled = true, testName = "Verify Today races By time view",
//            groups = {"iOS.RaceInfo"})
//    public void verifyByTimeRaceView() {
//        String trackName = "";
//        String raceNumber = "";
//        todayRacesView.getPageSource();
//
//        //1) Verify current race number and mtp is correctly displayed for all tracks
//        assertEquals(todayRacesView.compareUpcomingRacesInfo(),"","Verify current race number and mtp is correctly displayed for all tracks");
//
//        // 2) Verify the number of races displayed is same as that returned in webservice response
//        assertTrue(todayRacesView.compareTotalTracksCount(), "Verify number of total tracks " +
//                "displayed matches the number returned by today tracks webservice ");
//
//        // 3-6) Verify today races, all races and favorite races headers and message shown to anon users to login to see their favorites tracks
//        assertEquals(todayRacesView.getTodayRacesLabel(), "Today's Races", "Verify today races header is correct");
//        assertEquals(todayRacesView.getFavoritesRacesLabel(), "FAVORITES", "Verify favorites races header is correct");
//        assertEquals(todayRacesView.getAllRacesLabel(), "ALL RACES", "Verify all races header is correct");
//        assertEquals(todayRacesView.getFavoritesLoginMsg(), "Login to see your favorites",
//                "Verify favorites message for anon users is correct");
//
//        assertAll();
//    }
//
//    @Test(enabled = true, testName = "Verify Today races By track view and sort toggle",
//            groups = {"iOS.RaceInfo"})
//    public void verifyByTracksRaceViewAndSortToggle() {
//        List<String> trackNames;
//        String randomTrackName = "";
//
//        // 1) Close app to let it run in background and reopen again to verify that default view is still By time view
//        runAppInBackground(2);
//        assertTrue(todayRacesView.isByTimeViewDisplayed(),"Verify after reopening the app By Time view is still displayed as default view");
//
//        // 2) Reset app to verify that default view is still By time view
//        resetApp();
//        assertTrue(todayRacesView.isByTimeViewDisplayed(),"Verify after resetting the app By Time view is still displayed as default view");
//
//        /* 3) Click sort button to change to By Track view and get all track names displayed on the view to
//             verify that tracks are arranged in alphabetical and are same as that returned in webservice response */
//        todayRacesView.clickSortBtn();
//        sleepTime(5000);
//        todayRacesView.getPageSource();
//        todayRacesData.loadTrackArray();
//        trackNames = todayRacesView.getCurrentTrackNames();
//        assertEquals(trackNames,todayRacesData.getAllTrackNames(),"Verify that tracks are arranged in alphabetical order " +
//                "and matches the tracks returned by today tracks webservice"  );
//
//        // 4) Select a random track and verify that total races displayed for that track matches with the number returned by today tracks webservice
//        randomTrackName = todayRacesView.getRandomTrackName();
//        assertEquals(todayRacesView.geRaceCountForATrack(randomTrackName), todayRacesData.getTotalRacesForATrack(randomTrackName),
//                "Verify that total races displayed for a randomly selected track " +randomTrackName +" matches the total races returned by today tracks webservice");
//
//        // 5) Click the first race of first track on track view and verify program page is displayed
//        todayRacesView.clickFirstRaceByTrackNameOnTrackView(trackNames.get(0));
//        //TODO Replace this with track name and race number assertions once ids have been added to respective elements
//        assertTrue(programView.isProgramInfoDisplayed("Program"),"Verify on program page is displayed after selecting a race on tracks view");
//        driver.navigate().back();
//
//        // 6) Close app to let it run in background and reopen again to verify that default view is still By track view
//        runAppInBackground(2);
//        assertTrue(todayRacesView.isByTrackViewDisplayed(),"Verify after reopening the app By Track view is still displayed as default view");
//
//        // 7) Reset app to verify that default view is still By track view
//        resetApp();
//        assertTrue(todayRacesView.isByTrackViewDisplayed(),"Verify after resetting the app By Track view is still displayed as default view");
//       // assertAll();
//    }
//}
