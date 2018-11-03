package com.twinspires.qa.ios.pageobjects;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.WebElement;


import java.util.List;

public class SpeedClassPAceView extends IOSPageObject {


@FindBy(name = "BASIC")
WebElement lnkBasic;
@FindBy(xpath = ".//XCUIElementTypeStaticText[@name='trackName']")
List<WebElement> trackNamestest;
@FindBy(xpath = ".//XCUIElementTypeCell[@name='summaryTableViewCell']")
List<WebElement> noofTracksavialable;
@FindBy(xpath = ".//XCUIElementTypeCell[@name='summaryTableViewCell']")
WebElement singleTrackSelection;


    public SpeedClassPAceView(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }




    public List<String> getTrackList()
    {
    //waitForElement(10, trackNamestest.get(0));
        List<String> tracknameslist = null;
        System.out.println("Check if reaching all : " + trackNamestest.size());
       for(int i = 0 ;i < noofTracksavialable.size();i++)
     {
        System.out.println(trackNamestest.get(i).getAttribute("value"));
     }
           // for(WebElement temp : trackNamestest)
                      // {
                            //tracknameslist.add(temp.getAttribute("value"));
                      //     temp.getAttribute("value");
                     //   }


        return  tracknameslist;
    }

    public void getallRaceinfo()
    {
        for(int i=0 ; i< noofTracksavialable.size();i++)
        {
            System.out.println(noofTracksavialable.get(i).getAttribute("label"));
        }
    }

    public int NoofTracksAvailable()
{
    waitForElement(10,noofTracksavialable.get(0));
    return noofTracksavialable.size();

    }




}
