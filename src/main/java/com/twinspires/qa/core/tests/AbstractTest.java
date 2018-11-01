package com.twinspires.qa.core.tests;

import static com.twinspires.qa.core.util.Driver.driver;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.twinspires.qa.core.sqlqueries.ExternalAdapterQueries;
import com.twinspires.qa.core.testobjects.DeviceInfo;
import com.twinspires.qa.core.util.Util;
import com.twinspires.qa.core.webservices.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.qas.qtest.api.services.execution.model.ExecutionStatus;
import org.qas.qtest.api.services.execution.model.TestLog;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.asserts.IAssert;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.enums.TestStatus;
import com.twinspires.qa.core.sqlqueries.ADWQueries;
import com.twinspires.qa.core.sqlqueries.BDSQueries;
import com.twinspires.qa.core.sqlqueries.CAMQueries;
import com.twinspires.qa.core.sqlqueries.FundingQueries;
import com.twinspires.qa.core.sqlqueries.ProspectQueries;
import com.twinspires.qa.core.sqlqueries.SQLQueries;
import com.twinspires.qa.core.sqlqueries.VIPREQueries;
import com.twinspires.qa.core.util.Assertions;
import com.twinspires.qa.core.util.QTestService;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;

/**
 * Created by chad.justice on 9/13/2016.
 */

@Listeners({com.twinspires.qa.core.util.TestNGListener.class})
public abstract class AbstractTest {

    protected TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
    protected Affiliate affiliate = Affiliate.fromString(System.getProperty("aff", "ts"));
    protected String mainWindow;
    protected String currentWindow;
    protected Assertions asserts;
    protected ADWQueries adwQueries = new ADWQueries();
    protected BDSQueries bdsQueries = new BDSQueries();
    protected CAMQueries camQueries = new CAMQueries();
    protected ExternalAdapterQueries externalAdapterQueries = new ExternalAdapterQueries();
    protected FundingQueries fundingQueries = new FundingQueries();
    protected ProspectQueries prospectQueries = new ProspectQueries();
    protected SQLQueries sqlQueries = new SQLQueries();
    protected static TestDataQueries testDataQueries = new TestDataQueries();
    protected VIPREQueries vipreQueries = new VIPREQueries();
    protected WsRaceInfo wsRaceInfo;

    String browserType = System.getProperty("browser", "chrome");
   String qTestReporting = System.getProperty("reportToQTest", "false");
    QTestService qTestService;
    Date testStartTime;
    Date testEndTime;
    String errorMsgExeVars = "";
    String testMethodName = "";
    String testCaseName = "";
    String groupName = "";
    String testFeature = "";

    private List<String> windowHandles;
    private String errorMsgBeforeSuite = "";
    private String errorMsgBeforeClass = "";
    private String errorMsgBeforeMethod = "";
    private boolean qtestReportSetupSuccess = false;
    private boolean qtestReportMethodSuccess = false;
    private boolean assertAllPerformed;
    private boolean reportSteps;

    public AbstractTest() {
        qTestService = new QTestService();
    }

    //Before suite is used to delete any existing screenshot files in test directory
    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteSetUp() {
        Calendar cal = Calendar.getInstance();
        String workingDir = System.getProperty("user.dir");
        String screenshotFolder = workingDir + "/src/test/resources/screenshots";
        File screenshotDirectory = new File(screenshotFolder);
        errorMsgBeforeSuite = "";
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            try {
                for (File file : screenshotDirectory.listFiles()) {
                    if (file.getName().startsWith("Screenshot-"))
                        file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsgBeforeSuite += "Before Suite: " + e.getMessage() + "\n";
            }
        }
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClassSetUp() {
        System.out.print("RISHI : In before Class ");
        String className;
        String[] packageName;
        String moduleName;
        errorMsgExeVars = "";
        errorMsgBeforeClass = "";
//        if (qTestReporting.equalsIgnoreCase("true")) {
//           className = this.getClass().getSimpleName();
//           groupName = System.getProperty("groups");
//           packageName = this.getClass().getPackage().getName().split("\\.");
//           moduleName = packageName[packageName.length - 2];
//
//           try { // Setup qTest
//               errorMsgExeVars += qTestService.setExecutionVariables(testEnv, affiliate, moduleName,
//                       groupName, className, false);
//               errorMsgBeforeClass += qTestService.setReportCycles();
//               errorMsgBeforeClass += qTestService.setReportSuites();
//           } catch (Exception e) {
//           }
//
//             //qTest setup was successful
//           if (errorMsgBeforeClass.isEmpty()) {
//                qtestReportSetupSuccess = true;
//
//                // If any error messages exist, report and flag setup issues
//           } else {
//               errorMsgBeforeClass = "Before Class: qTest Exception: \n" + errorMsgBeforeClass;
//                qtestReportSetupSuccess = false;
//            }
//       }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTestSetUp(Method method) {
        System.out.print("RISHI : In before MEthod ");
        String temp = "";
        assertAllPerformed = false;
        reportSteps = true;
        wsRaceInfo = new WsRaceInfo();
        asserts = new Assertions();
        errorMsgBeforeMethod = "";
        testCaseName = method.getAnnotation(org.testng.annotations.Test.class).testName();
        try { // Find the Features group and acquire associated features
            for (int i = 0; i < method.getAnnotation(Test.class).groups().length; i++) {
                temp = method.getAnnotation(Test.class).groups()[i].toUpperCase();
                if (temp.contains("FEATURE:")) {
                    testFeature = temp.replace("FEATURE:", "");
                    break;
                } else {
                    testFeature = "";
                }
            }
        } catch (Exception e) {
            System.out.println("No feature number listed for the test");
        }

        printTestCaseNameToReport("Executing: " + testCaseName);
        testMethodName = method.getName();
        System.out.print("RISHI Done with : In before MEthod ");
       //If qTest Reporting is turned on, perform setup for this test case
        asserts.setQTestServiceInstance(qTestService);
        System.out.print("RISHI start reporting : In before MEthod ");
        if (qTestReporting.equalsIgnoreCase("true")) {
            if (qtestReportSetupSuccess) {
                qtestReportMethodSuccess = true;

                try {
                    testStartTime = new Date();
                    errorMsgBeforeMethod += qTestService.setupByTestRunName(testCaseName);
                } catch (Exception e) {
                }

                // qTest setup failed if any error messages exist, report and flag setup issues
                if (!errorMsgBeforeMethod.isEmpty()) {
                    errorMsgBeforeMethod = "Before Method: qTest Exception: \n" + errorMsgBeforeMethod;
                    qtestReportMethodSuccess = false;
                }
            } // qTest setup failed, error message should already be reporting
        }
        System.out.print("RISHI Done with reporting : In before MEthod ");
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest(ITestResult testResult) {
        System.out.print("RISHI start: In After MEthod ");
        String outputResult = "";
        String errorMsgAfterMethod = "";
        ExecutionStatus finalStatus = new ExecutionStatus();
        TestLog testLog = new TestLog();
        System.out.print("RISHI end: In After MEthod ");
    }
       // try {for

    /**
     * This method provide devices to run native tests. It will use deviceType system variable to determine whether to
     * get data from automation database or use the information by provided by tester via system variables to test on a
     * particular device.
     */

    @DataProvider(name = "devices", parallel = true)
    public static Object[][] devicesDataProvider() {
        String testPlatform = System.getProperty("platform");
        String deviceType = System.getProperty("deviceType");

        if (deviceType.toLowerCase().contains("cloud")) {
            if (System.getProperty("deviceName") != null || System.getProperty("deviceVersion") != null) {
                return new Object[][]{
                        new Object[]{System.getProperty("platform"),
                                System.getProperty("deviceName"), System.getProperty("deviceVersion")}
                };
            } else {
                ArrayList<DeviceInfo> devices = testDataQueries.getSupportedDevices(testPlatform);
                Object[][] testDevices = new Object[devices.size()][];
                for (int i = 0; i < devices.size(); i++) {
                    testDevices[i] = new Object[]{devices.get(i).getPlatformName(),
                            devices.get(i).getDeviceName(), devices.get(i).getDeviceVersion()};
                }
                return testDevices;
            }
        } else {
            return new Object[][]{
                    new Object[]{System.getProperty("platform"),
                            System.getProperty("deviceName"), System.getProperty("deviceVersion")}
            };
        }
    }

    /**
     * Determine and sets the current test's overall status (finalStatus)
     * flags whether individual steps should be reported to qTest (reportSteps)
     * and generates Execution Warning messages to be added to the report
     *
     * @return any associated Execution Warning messages
     */
    private String setFinalStatus(ExecutionStatus finalStatus, ITestResult testResult) {
        System.out.print("RISHI start: In setFinal status ");
        String exeWarnMessages = "";
        Long expStatus;
        Long retStatus;
       int actStepsSize = qTestService.getBuiltStepsLog().size();
       int expStepsSize = qTestService.getTestCase().getTestSteps().size();

        //No steps were performed
        if (actStepsSize == 0) {
            exeWarnMessages += "    No assertions were performed\n";
            if (testResult.getStatus() == ITestResult.SUCCESS) {
                retStatus = TestStatus.PASSED.getStatus();
                reportSteps = false;

            } else {
                retStatus = TestStatus.FAILED.getStatus();
                reportSteps = false;
            }

            // Steps were performed
        } else {
            expStatus = qTestService.getFinalStatus();
            // All steps were performed, as expected
            if (actStepsSize == expStepsSize) {
                retStatus = expStatus;
                reportSteps = true;

            } else {
                // If test Passed, set qTest status as "Passed w/Mapping"
                if (expStatus.longValue() == TestStatus.PASSED.getStatus()) {
                    retStatus = TestStatus.PASSED_W_MAPPING.getStatus();
                } else {
                    retStatus = expStatus;
                }

                if (actStepsSize < expStepsSize) {
                    exeWarnMessages += "    Fewer steps in script than test case\n";
                    reportSteps = true;

                } else { //if (actStepsSize.longValue() > expStepsSize)
                    exeWarnMessages += "    More steps exist in script than test case\n";
                    reportSteps = false;
                }
            }
        }

        if (!assertAllPerformed) {
            exeWarnMessages += "    End of test was not reached\n";
            retStatus = TestStatus.FAILED.getStatus();
        }

       /// finalStatus.setId(retStatus);
        System.out.print("RISHI end: In setFinal status ");
        return (exeWarnMessages.isEmpty()) ? "" : "EXECUTION WARNINGS:\n" + exeWarnMessages;
    }

    /**
     * Compiles all TestNG @Before/After errorMsg info for output when an error was detected in any of them
     */
//    private String getQtestDebug(String afterClassErrorMsg) {
//        System.out.print("RISHI : In getQtestDebug ");
//        String fullDebugMessage = "";
//
//        fullDebugMessage += (errorMsgBeforeSuite.contains("Before Suite:")) ? errorMsgBeforeSuite : "";
//        fullDebugMessage += (errorMsgBeforeClass.contains("Before Class:")) ? errorMsgBeforeClass : "";
//        fullDebugMessage += (errorMsgBeforeMethod.contains("Before Method:")) ? errorMsgBeforeMethod : "";
//       // if (!fullDebugMessage.isEmpty()
//         //       && qTestReporting.equalsIgnoreCase("true"))
//         //   fullDebugMessage += errorMsgExeVars + qTestService.getExecutionVariableMethod(testMethodName);
//        fullDebugMessage += (afterClassErrorMsg.contains("After Method:")) ? afterClassErrorMsg : "";
//
//        return fullDebugMessage;
//    }

    protected void setImplicitWait(long time, TimeUnit timeUnit, WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(time, timeUnit);
    }

    protected String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    protected String getCallingMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    protected void printTestCaseNameToReport(String testCaseName) {

        printLine(System.getProperty("line.separator"));
        String testName = testCaseName;
        String dottedLine = "";

        for (int i = 0; i < testName.length(); i++) {
            dottedLine = dottedLine + "-";
        }

        printLine(dottedLine);
        printLine(testName);
        printLine(dottedLine);
    }

    protected void printLine(String statement) {
        Reporter.log(statement.replace("\n", "<br />")
                .replace(System.lineSeparator(), System.lineSeparator() + "<br />"));
        System.out.println(statement.replace("<br/>", "\n"));
    }

    protected void switchFocusNewTab(WebDriver driver) {
        mainWindow = driver.getWindowHandle();
        ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(allTabs.get(allTabs.size() - 1));
    }

    protected void waitForElementToBeInvisible(WebDriver driver, By locator, int timeInMilliSeconds) {
        new WebDriverWait(driver, (long) timeInMilliSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForElementToBeVisible(WebDriver driver, By locator, int timeInMilliSeconds) {
        new WebDriverWait(driver, (long) timeInMilliSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected void switchToMainwindow(WebDriver driver) {
        driver.switchTo().window(mainWindow);
    }

    protected String getCurrentUrl(WebDriver driver) {
        return driver.getCurrentUrl();
    }

    protected void doAssert(IAssert assertion) {
        asserts.doAssert(assertion);
    }

    protected void assertAll() {
        printLine("--------------------------------------");
        assertAllPerformed = true;
        asserts.assertAll();
    }

    protected void assertContains(String actual, String expected, String message) {
        String adjExpected = expected;
        if (actual.contains(expected)) {
            adjExpected = actual;
        }
        asserts.assertEquals(actual, adjExpected, message);
    }

    public void assertEqualsIgnoreCase(String actual, String expected, String message) {
        String adjExpected = expected;
        if (actual.equalsIgnoreCase(expected)) {
            adjExpected = actual;
        }
        asserts.assertEquals(actual, adjExpected, message);
    }

    protected void assertEquals(String actual, String expected, String message) {
        asserts.assertEquals(actual, expected, message);
    }

    protected void assertEquals(boolean actual, boolean expected, String message) {
        asserts.assertEquals(actual, expected, message);
    }

    protected void assertEquals(double actual, double expected, String message) {
        asserts.assertEquals(actual, expected, message);
    }

    protected void assertEquals(BigDecimal actual, BigDecimal expected, String message) {
        asserts.assertEquals(actual, expected, message);
    }

    protected void assertEquals(Object actual, Object expected, String message) {
        asserts.assertEquals(actual, expected, message);
    }

    protected void assertNotEquals(Object actual, Object expected, String message) {
        asserts.assertNotEquals(actual, expected, message);
    }

    protected void assertFalse(Boolean condition, String message) {
        asserts.assertFalse(condition, message);
    }

    protected void assertTrue(Boolean condition, String message) {
        asserts.assertTrue(condition, message);
    }

    protected void assertWorkaround(String message) {
        asserts.assertWorkaround(message);
    }

    protected void assertIncomplete(String message) {
        asserts.assertIncomplete(message);
    }

    protected void waitForClassElement(String element, WebDriver driver) {
        WebElement el = driver.findElement(By.className(element));
        Integer numAllowed = 500;
        while (!el.isDisplayed()) {
            if (el.isDisplayed()) {
                System.out.println("Element loaded");
            } else {
                numAllowed--;
                if (numAllowed == 0) {
                    Assertions asserts = new Assertions();
                    asserts.fail("Timed out waiting for element to load");
                }
            }
        }
    }

    public WebElement waitForElement(int milliseconds, WebElement element, WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, milliseconds);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected String parseDownloadUrl(WebDriver driver) {

        String url = "";
        String parsedUrl[] = new String[16];

        //stores current window handle then switches to new window
        String winHandleOriginal = driver.getWindowHandle();
        for (String winHandle : driver.getWindowHandles()) {

            driver.switchTo().window(winHandle);
        }

        url = driver.getCurrentUrl();
        parsedUrl = url.split("&");

        //closes new window and switches to original window
        driver.close();
        driver.switchTo().window(winHandleOriginal);

        return parsedUrl[15];
    }

    protected void maximizeWindow(WebDriver driver) {
        driver.manage().window().maximize();
    }

    public void trigger(String script, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(script, element);
    }

    public Object trigger(String script) {
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    public String openTab() {
        return openTab("");
    }

    public String openTab(String url) {
        String newTabHandle;
        windowHandles = getWindowHandlesList();

        ((JavascriptExecutor) driver).executeScript("window.open()");
        newTabHandle = switchToNewWindow();
        if(!url.trim().equalsIgnoreCase("")) {
            driver.get(url);
        }

        return driver.getWindowHandle();
    }

    public String switchWindow() throws NoSuchWindowException, NoSuchWindowException {
        Set<String> handles = driver.getWindowHandles();
        String current = driver.getWindowHandle();
        handles.remove(current);
        String newTab = handles.iterator().next();
        driver.switchTo().window(newTab);
        return driver.getWindowHandle();
    }

    /**
     * Switches to the window the provided handle
     *
     * @param windowHandle the window handle of the desired window
     * @return the active window handle
     * @throws NoSuchWindowException
     * @throws NoSuchWindowException
     */
    public String switchWindow(String windowHandle) throws NoSuchWindowException {
        driver.switchTo().window(windowHandle);
        return driver.getWindowHandle();
    }

    /**
     * Switches to the window by index (not always chronological)
     *
     * @param windowIndex windowIndex index of the desired focus window
     * @return the active window handle
     * @throws NoSuchWindowException
     * @throws NoSuchWindowException
     */
    public String switchWindow(int windowIndex) throws NoSuchWindowException {
        int idx = Math.abs(windowIndex);

        windowHandles = getWindowHandlesList();
        idx = (idx < windowHandles.size()) ? idx : (windowHandles.size() - 1);
        driver.switchTo().window(windowHandles.get(idx));
        return driver.getWindowHandle();
    }

    /**
     * Compares list of previously available window handles to determine the new window
     *
     * @return the handle of the new window (the first of the new handles, if multiple exist)
     */
    public String switchToNewWindow() {
        List<String> currentHandles = getWindowHandlesList();
        String newHandle = "";

        // Get list of new handles
        for (int cur = currentHandles.size() - 1; cur >= 0; cur--) {
            for (int prev = 0; prev < windowHandles.size(); prev++) {
                if (currentHandles.get(cur).contains(windowHandles.get(prev))) {
                    currentHandles.remove(cur);
                    break;
                }
            }
        }

        // Update windowHandles list and nav to new window[0]
        if (currentHandles.size() > 0) {
            windowHandles = getWindowHandlesList();
            newHandle = currentHandles.get(0);
            driver.switchTo().window(newHandle);
            return newHandle;
        }
        return "";
    }

    public void switchParentWindow() throws NoSuchWindowException, NoSuchWindowException {
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.close();
        driver.switchTo().window(tabs.get(0));
    }

    protected void switchFocusNewTab(WebDriver driver, String pageTitle) {
        mainWindow = driver.getWindowHandle();
        //define custom conditional wait for checking availability of multiple windows
        ExpectedCondition<Boolean> windowCondition = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return driver.getWindowHandles().size() > 1;
                    }
                };
        //call custom conditional wait for ensuring multiple windows are present
        new WebDriverWait(driver, 10).until(windowCondition);
        //get all window handles and switch to window only when window title matches with given title
        ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
        for (String tab : allTabs) {
            driver.switchTo().window(tab);
            currentWindow = driver.getWindowHandle();
            if (driver.getTitle().equalsIgnoreCase(pageTitle)) {
                break;
            }
        }
    }

    /**
     * Closes the current window and switches to the first window
     *
     * @return active window handle, "" if no windows exist
     */
    protected String closeCurrentWindow() {
        // Close window
        driver.close();

        // Update handle tracking list
        windowHandles = getWindowHandlesList();

        // If other windows exist, switch to the first window
        if (windowHandles.size() > 0) {
            driver.switchTo().window(windowHandles.get(0));
            return driver.getWindowHandle();
        }
        return "";
    }

    /**
     * Closes the specified window and returns to the currently selected, if it still exists
     *
     * @param closeWindow the handle of the window to close
     * @return the active window handle if it wasn't closed, else the first window
     */
    protected String closeWindow(String closeWindow) {
//        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        String curWindow = driver.getWindowHandle();

        // Update window handles list
        windowHandles = getWindowHandlesList();

        // If the specified window exists
        if (closeWindow != null && windowHandles.contains(closeWindow)) {

            // the current window is the one getting closed
            if (closeWindow.equalsIgnoreCase(curWindow)) {
                return closeCurrentWindow();

                // switch back to the current window after closing the specified window
            } else {
                driver.switchTo().window(closeWindow);
                driver.close();
                driver.switchTo().window(curWindow);
                return driver.getWindowHandle();
            }
        }

        printLine(String.format("Window - %s not available to be closed", closeWindow));
        return "";
    }

    protected void closeAllWindows() {
        driver.quit();
    }

    protected List<String> getWindowHandlesList() {
        return new ArrayList<>(driver.getWindowHandles());
    }

    public String getUsername(String dataCondition) {
        String username;
        username = testDataQueries.getUsername(testEnv.toString(), affiliate.toString(), dataCondition);
        if (username == null) {
            username = testDataQueries.getUsernameAfterCreation(testEnv.toString(), affiliate.toString(), dataCondition);
        }
        return username;
    }

    public String getPassword(String dataCondition) {
        String password = testDataQueries.getPassword(testEnv.toString(), affiliate.toString(), dataCondition);
        return password;
    }

    public String getUsername() {
        String username = testDataQueries.getUsername(testEnv.toString(), affiliate.toString(), "General account");
        return username;
    }

    /**
     * Allows quick means to end all tests nearly immediately for setup and configuration testing
     *
     * @param toggleFailureOn true will toggle automatic failure on, false will bypass autofailure
     */
    private void autoFail(boolean toggleFailureOn) {
        if (toggleFailureOn == true) {
            assertFalse(true, "AUTOMATIC FAILURE TOGGLED ON");
            assertAll();
        }
    }

    public void openSite(String userChannel) {
        autoFail(false);
        String url = "";
        if (!browserType.equalsIgnoreCase("nativeiOS")) {
            url = testDataQueries.getUrl(testEnv.toString(), affiliate.toString(), userChannel);
            driver.get(url);

            if (url.contains("/new/registration")) {
                //TODO: Find a better solution later
                //This is a refresh needed when dev does not want to dig into automation only issue
                //Affiliates new registration page gets stuck when automation clicks too quick
                //driver.navigate().refresh() will only work if the refresh button shows up quick
                //Resubmit the URL seems to work better at this moment without a long wait.
                sleepTime(1000);
                driver.get(url);
            }
        }
        windowHandles = getWindowHandlesList();
    }

    public String getHost(String hostName) {
        String host = testDataQueries.getHost(testEnv.toString(), hostName);
        return host;
    }

    public void setBonusPlaythroughId(String dataCondition, String offerId) {
        testDataQueries.setBonusPlaythroughId(testEnv.toString(), affiliate.toString(), dataCondition, offerId);
    }

    public String getBonusPlaythroughId(String dataCondition) {
        String bonusId = testDataQueries.getBonusPlaythroughId(testEnv.toString(), affiliate.toString(), dataCondition);
        return bonusId;
    }

    /**
     * TODO : DEPRECATING - currently in use by Mobile/Desktop applications. Current getTrack logic in TrackData class
     * Returns track name, bris code, bds code, track type,
     * and country for whatever track is selected
     *
     * @param dataCondition
     * @return track
     */
    public HashMap<String, String> getTrack(String dataCondition) {
        String testCycle = "";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss a");
        testCycle = (dateFormat.format(date).toString().contains("AM") ? "M" : "A");
        HashMap<String, String> track = testDataQueries.getTrack(dataCondition, testCycle);
        return track;
    }

    public String getCurrentJwtId() {
        int temp = 0;
        String jwtKey = "";
        String cookies = "";
        String getJwtFromConsole = "document.cookie";

        cookies = Util.exeJsGetString(getJwtFromConsole);
        temp = cookies.indexOf("JWT_Session") + 12;
        cookies = cookies.substring(temp);
        temp = cookies.indexOf(";");
        temp = (temp == -1) ? cookies.length() : temp;
        jwtKey = cookies.substring(0, temp);

        return jwtKey;
    }

    public void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*Verify UUID in externaladaptor DB across platforms.
    * @param email
    */
    public void verifyUUID(String email) {
        assertTrue(externalAdapterQueries.isProspectIdExists(prospectQueries.getUUID(email)),
                "Verify UUID exists for email: " + email);
    }
}
