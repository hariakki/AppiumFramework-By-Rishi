package com.twinspires.qa.ios.tests;

        import com.twinspires.qa.ios.pageobjects.SpeedClassPAceView;
        import org.testng.annotations.Factory;
        import org.testng.annotations.Test;

public class SpeedClassPAceViewTest extends IOSAbstractTest{

    @Factory(dataProvider = "devices")
    public SpeedClassPAceViewTest(String platformName, String deviceName, String deviceVersion) {
        deviceInfo.setPlatformName(platformName);
        deviceInfo.setDeviceName(deviceName);
        deviceInfo.setDeviceVersion(deviceVersion);
    }


    @Test
    public void printTracks() throws  Exception
    {
        Thread.sleep(4000);
        System.out.println("All the tracks available");
        speedclasspaceView.getallRaceinfo();
        System.out.println( speedclasspaceView.getTrackList());
       //System.out.println(todayRacesData.getAllTrackNames());
        System.out.println("No of tracks Available : " + speedclasspaceView.NoofTracksAvailable());
        //speedclasspaceView.getallRaceinfo();
    }
}
