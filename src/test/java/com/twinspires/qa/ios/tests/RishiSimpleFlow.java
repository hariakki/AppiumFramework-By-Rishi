//package com.twinspires.qa.ios.tests;
//
//import com.twinspires.qa.core.testdata.TrackData;
//import org.testng.annotations.Factory;
//import org.testng.annotations.Test;
//import java.util.List;
//
//public class RishiSimpleFlow extends IOSAbstractTest {
//
//    List<String> trackNames;
//    //String randomTrackName = "";
//
//    @Factory(dataProvider = "devices")
//    public RishiSimpleFlow(String platformName, String deviceName, String deviceVersion) {
//        deviceInfo.setPlatformName(platformName);
//        deviceInfo.setDeviceName(deviceName);
//        deviceInfo.setDeviceVersion(deviceVersion);
//    }
//
//    @Test(priority=0)
//    public void verifyLoginFunctionality() {
//
//
//        // Click on Login link and verify various login view elements are displayed correctly
//       // loginView.clickLoginLink();
//
//       // loginView.userLogin("Atimi1", "password1");
//    }
//   @Test
//    public void Trackinfo() throws Exception
//     {
//    loginView.clickLoginLink();
//
//     loginView.userLogin("Atimi1", "password1");
//        Thread.sleep(10000);
//        todayRacesView.clickRishiEarlyLuck9();
//        todayRacesData.setTotalTracks(10);
//        int no = todayRacesData.getTotalTracks();
//        System.out.print("TOtal no. of Races : " + no);
//
//       // todayRacesData.getTotalRacesForATrack("CAL-EXPO");
//    String temp  =todayRacesView.getFirstTrackRaceNumberByTimeView();
//    System.out.print("Race number by normal view :  " + temp);
//
//    programView.clickOffersTab();
//    programView.clickClaimOfferLink();
//    programView.clickRacesTab();
//    String trackname = todayRacesView.getRandomTrackName();
//System.out.print("Track name : " + trackname);
//
//     // System.out.println(  todayRacesView.getRandomTrackName());
//    trackNames =  todayRacesView.getCurrentTrackNames();
//    for(String temp : trackNames)
//      {
//          System.out.println(temp);
//       }
//
//       todayRacesData.getCurrentRaceInfo("CAL-EXPO");
//
//
//    }
//}
