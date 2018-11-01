package com.twinspires.qa.core.core;

import com.twinspires.qa.core.util.TestNGListener;
import org.testng.TestNG;
import org.testng.TestRunner;
import org.testng.annotations.*;
import org.testng.xml.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasey.sparkman on 8/22/17.
 */
public class TestNGSuiteGenerator{
    boolean launched;

    public TestNGSuiteGenerator() {
        launched = false;
    }
//    @BeforeGroups
//    @BeforeSuite
    public void generateXml() {
        if(!launched) {
            TestNG testNG = new TestNG();
            List<XmlSuite> executableXML;
            Parser parsed;
            String affiliate = System.getProperty("aff", "ts").toUpperCase();
            String exclude = "Aff.*:" + affiliate + ".*";

            System.out.println(":::DEBUG::: GenerateXML()");
            // Gets the existing contents of "Jenkins_Suite.xml" to replicate
            try {
                parsed = new Parser("../Jenkins_Suite.xml");
                executableXML = parsed.parseToList();

            } catch (Exception e) {
                System.out.println("Error parsing xml: " + e.getMessage());
                System.out.println(e.getStackTrace());
                return;
            }

            // Sets the group exclusions for the current affiliate
            executableXML.get(0).getTests().get(0).addExcludedGroup("Aff.*:" + affiliate + ".*");
            System.out.println("GENERATE_XML COMPLETED");
//        System.out.println("Printing TestNG Suite's Xml:");
//        System.out.println(executableXML.get(0).toXml());

            // Sets the altered xml suite as the one to be executed by the TestNG class
            testNG.setXmlSuites(executableXML);
            testNG.setGroups("DebugTest");
            testNG.setExcludedGroups(exclude);
            testNG.setDefaultSuiteName(executableXML.get(0).getName());
            testNG.setDefaultTestName(executableXML.get(0).getName());

//          testNG.setListenerClasses(List<TestNGListener>);
//          System.out.println(testNG.getDefaultSuiteName());
//          System.out.println(testNG.getDefaultTestName());
//          System.out.println("");

            launched = true;
            testNG.run();
        } else {
            System.out.println("Already Launched");
        }
    }

//    @Test
    public void LaunchTests() {
        System.out.println(":::DEBUG:::  LaunchTests()");
    }
}