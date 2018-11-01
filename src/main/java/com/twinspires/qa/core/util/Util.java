package com.twinspires.qa.core.util;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.testng.asserts.IAssert;
import org.testng.collections.Maps;
import org.testng.Reporter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.twinspires.qa.core.util.Driver.driver;

/**
 * Created by chad.justice on 9/16/2016.
 */
public class Util {

    private final Map<AssertionError, IAssert> m_errors = Maps.newLinkedHashMap();
    private static String testEnv = System.getProperty("env", "ite");
    private static String affiliateShortName = System.getProperty("aff", "ts");
    private static String gridUrl = System.getProperty("grid", "abc");
    private static String screenshotsUrl = System.getProperty("screenshotsUrl");
    private static String executionHost = System.getProperty("host", "local");

    public static String randomSSN1() {
        Random rand = new Random();
        int randomNum = rand.nextInt((999 - 100) + 1) + 100;
        String SSN1 = Integer.toString(randomNum);
        return SSN1;
    }

    public static String randomSSN2() {
        Random rand = new Random();
        int randomNum = rand.nextInt((99 - 10) + 1) + 10;
        String SSN2 = Integer.toString(randomNum);
        return SSN2;
    }

    public static String randomSSN3() {
        Random rand = new Random();
        int randomNum = rand.nextInt((9999 - 1000) + 1) + 1000;
        String SSN3 = Integer.toString(randomNum);
        return SSN3;
    }

    public static String randomMM() {
        Random rand = new Random();
        int randomNum = rand.nextInt((12 - 1) + 1) + 1;
        if (randomNum < 10 ){
            String MM = Integer.toString(randomNum);
            MM = "0" + MM;
            return MM;
        } else {
            String MM = Integer.toString(randomNum);
            return MM;
        }
    }

    public static String randomDD() {
        Random rand = new Random();
        int randomNum = rand.nextInt((29 - 1) + 1) + 1;
        if (randomNum < 10 ){
            String DD = Integer.toString(randomNum);
            DD = "0" + DD;
            return DD;
        } else {
            String DD = Integer.toString(randomNum);
            return DD;
        }
    }

    public static String randomYYYY() {
        Random rand = new Random();
        int randomNum = rand.nextInt((1995 - 1935) + 1) + 1935;
        String YYYY = Integer.toString(randomNum);
        return YYYY;
    }

    public static String randomUsername() {
        Random rand = new Random();
        int userInt = rand.nextInt((999999 - 100000) + 1) + 100000;
        String userNum = Integer.toString(userInt);
        String userAff = affiliateShortName;
        String userEnv = String.valueOf(testEnv.charAt(0));
        String userName = "cdi" + userAff + userEnv + userNum;
        return userName;
    }

    public static int random2Choice() {
        Random rand = new Random();
        int randomNum = rand.nextInt((1 - 0) + 1) + 0;
        return randomNum;
    }

    public static int random3Choice() {
        Random rand = new Random();
        int randomNum = rand.nextInt((2 - 0) + 1) + 0;
        return randomNum;
    }

    public static int random100() {
        Random rand = new Random();
        int randomNum = rand.nextInt((100 - 2) + 1) + 2;
        return randomNum;
    }

    public static int randomWager5to15() {
        Random rand = new Random();
        int randomNum = rand.nextInt((15 - 5) + 1) + 5;
        return randomNum;
    }

    public static String getAffiliateLongName() {
        switch (affiliateShortName.toUpperCase()) {
            case "BC": return "Breeders' Cup Betting Championship";
            case "BH": return "BetHarness";
            case "CB": return "CapitalOTBBet.com";
            case "TS": return "Twinspires";
            case "KD": return "Kentucky Derby Betting Championship";
            case "KS": return "Keeneland Select";
            case "MB": return "My Race Bet";
            case "OK": return "Oaklawn Park";
            case "RB": return "Raceline Bet";
            default: return "Twinspires";
        }
    }

    public static String generateUniqueUsername() {
        String username = "";
        username = "Bris" + getTimeStamp();
        username = username.substring(0,username.length()-2);
        return username;
    }

    public static String generateUniqueEmail() {
        String email = "";
        email = "bris" + getTimeStamp() + "@twinspires.com";
        return email;
    }

    public static String getTimeStamp(String format) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getTimeStamp() {
        return getTimeStamp("HHmmssSSS");
    }

    public static int getMonthIntegerValue(String monthName) {
        int monthNumber = 0;
        try {
            Date date = new SimpleDateFormat("MMM").parse(monthName);//put your month name here
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            monthNumber = cal.get(Calendar.MONTH);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthNumber;
    }

    public static int randomNumberForRange(int lowerNumber, int higherNumber) {
        Random r = new Random();
        if(lowerNumber == higherNumber) {
            return lowerNumber;
        }
        return r.nextInt(higherNumber - lowerNumber) + lowerNumber;
    }

    public static String getTodaysDate(String format) {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getTodaysDate() {
        return getTodaysDate("yyyy-MM-dd");
    }

    public static String getProductCode(WebDriver driver) {
        String productCode = "";
        String[] currentURL = driver.getCurrentUrl().split("/");
        productCode = currentURL[currentURL.length - 1];
        return productCode;
    }

    public static String getMonthForInt(int num) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, num);
        int selectedMonth = cal.get(Calendar.MONTH);
        String month = "month";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (selectedMonth >= 0 && num <= selectedMonth) {
            month = months[selectedMonth];
        }
        return month;
    }

    public static String getDayOfWeek(int dayNumber) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(c.DATE, dayNumber);
        String dayOfWeek = dateFormat.format(c.getTime());
        return dayOfWeek;
    }

    public static String getAbbrevMonthNDate(int dayNumber) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(c.DATE, dayNumber);
        String monthDate = dateFormat.format(c.getTime());
        return monthDate;
    }

    public static String getAbbrevDate(int dayNumber) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(c.DATE, dayNumber);
        String formattedDate = dateFormat.format(c.getTime());
        return formattedDate;
    }

    /**
     * Gets the active session ID, returns "" if cannot be found
     * @return
     */
    public static String getSessionId() {
        try {
            return driver.manage().getCookieNamed("SessionID").getValue();
        } catch (Exception e) {
            return "";
        }
    }

    public static void scrollUp(WebDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,-250)", "");
    }

    public static void printLine(String statement) {
        Reporter.log(statement);
        System.out.println(statement);
    }

    public static void scrollDownFullPage(WebDriver driver) {
        sleep(2000);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0,document.body.scrollHeight)");
    }

    public static void scrollUpFullPage(WebDriver driver){
        sleep(2000);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0,- document.body.scrollHeight)");
    }
    
    public static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    public static void scrollToBottomOfElementByID(WebDriver driver, String elementID) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("var scrollableBody = document.getElementById('" + elementID + "');"
                + "scrollableBody.scroll(0, scrollableBody.scrollHeight);");
    }

    public static void scrollToBottomOfElement(WebDriver driver, WebElement element) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("var scrollableBody = arguments[0];"
                + "scrollableBody.scroll(0, scrollableBody.scrollHeight);",element);
    }

    public static void scrollToTopOfElementByID(WebDriver driver, String elementID) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("var scrollableBody = document.getElementById('" + elementID + "');"
                + "scrollableBody.scroll(0, 0);");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static long randomNumberForDigits(int lowerDigit, int higherDigit) {
        Random r = new Random();
        int ranDigits=randomNumberForRange(lowerDigit,higherDigit);
        double baseValue=Math.pow(10,ranDigits);
        return(long)(r.nextFloat()*baseValue);
    }

    public long randomNumberRange(long min, long max) {
        long rand = ThreadLocalRandom.current().nextLong(min, max + 1);
        return rand;
    }

    public static String getDatePastOrFuture(String dateFormatText, int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return dateFormat.format(cal.getTime());
    }

    public static void takeScreenShot(String message) {
        String screenshotPath = "";
        String screenshotLink = "";
        String workingDir = "";
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        workingDir = System.getProperty("user.dir");
        screenshotPath = "/src/test/resources/screenshots/Screenshot-"+getTimeStamp() + ".png";
        File destFile = new File(workingDir + screenshotPath);
        try {
            FileUtils.copyFile(scrFile, destFile);
            Reporter.setEscapeHtml(false);
            if(executionHost.equalsIgnoreCase("local")) {
                screenshotLink = destFile.getAbsolutePath();
            }
            else {
                screenshotLink = screenshotsUrl + screenshotPath;
            }
            Reporter.log("<a href='" + screenshotLink + "'>" + message + "</a>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(initVector));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Capitalizes the first letter in each word
     * 
     * @param input "i am a String"
     * @return titleCase "I Am A String"
     */
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        
        input = input.toLowerCase();

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static String formatDateTime(Timestamp dateTime, String format) {
        String formattedDateTime;

        formattedDateTime = new SimpleDateFormat(format).format(dateTime);

        return formattedDateTime;
    }
    
    public static String formatDateTime(Date date, String format) {
        String formattedDateTime;
        
        formattedDateTime = new SimpleDateFormat(format).format(date);
        
        return formattedDateTime;
    }

    public static String randomEmail() {
        Random rand = new Random();
        if(testEnv.equalsIgnoreCase("LOAD")){
            return "Selenium" + rand.nextInt(999999999 - 100000000) + 100000000 + "@blackhole.io";
        } else {
            return "Selenium" + rand.nextInt(999999999 - 100000000) + 100000000 + "@gmail.com";
        }
    }

    public static String getItemFromLocalStorage(String key) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        return (String) jse.executeScript(String.format(
                "return window.localStorage.getItem('%s');", key));
    }
    
    /**
     * Searches for the WebElement's identifying attributes for a JS webElement lookup (for JavascriptExecutor methods)
     *  Attempts to identify in the order:
     *      -id
     *      -name
     *      -className
     * @param element the webelement to identify in the current page
     * @return js document webelement search code
     */
    private static String jsWebElementLookup(WebElement element) {
        String attribute = element.getAttribute("id");
        if (!attribute.isEmpty()) {
            attribute = "document.getElementById('" + attribute + "')";
        } else {
            attribute = element.getAttribute("name");
            if (!attribute.isEmpty()) {
                attribute = "document.getElementsByName('" + attribute + "')[0]";
            } else {
                attribute = element.getAttribute("className");
                if (!attribute.isEmpty()) {
                    attribute = "document.getElementsByClassName('" + attribute + "')[0]";
                } else {
                    throw new ElementNotFoundException("WebElement lookup", "[id|name|class", "null");
                }
            }
        }
        return attribute;
    }
    
    public static void exeJs(String jsCode) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript(jsCode);
    }
    
    public static String exeJsGetString(String jsCode) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        return (String) jse.executeScript("return " + jsCode);
    }
    
    public static Boolean exeJsGetBoolean(String jsCode) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        return (Boolean) jse.executeScript("return " + jsCode);
    }
    
    /**
     * For HTML5 video, returns whether the video is currently playing
     * @param videoElement the HTML5 video element
     * @return true if playing, else false;
     * @throws ElementNotFoundException
     */
    public static boolean isVideoPlayingHTML5(WebElement videoElement) throws ElementNotFoundException {
        String attribute = jsWebElementLookup(videoElement) + ".paused;";
        return !exeJsGetBoolean(attribute);
    }
    
    /**
     * For HTML5 video, plays the video
     * @param videoElement the HTML5 video element
     * @return true if playing, else false;
     * @throws ElementNotFoundException
     */
    public static void playVideoHTML5(WebElement videoElement) throws ElementNotFoundException {
        exeJs(jsWebElementLookup(videoElement) + ".play();");
    }
    
    /**
     * For HTML5 video, pauses the video
     * @param videoElement the HTML5 video element
     * @return true if playing, else false;
     * @throws ElementNotFoundException
     */
    public static void pauseVideoHTML5(WebElement videoElement) throws ElementNotFoundException {
        exeJs(jsWebElementLookup(videoElement) + ".pause();");
    }

    public static String getSessionIdFromLocalStorage(){
        JSONObject jsnobject = new JSONObject(Util.getItemFromLocalStorage("sessionId"));
        Object sessionId = jsnobject.get("id");
        return String.valueOf(sessionId);
    }

    /**
     * Determines the difference between two monetary values
     * @return double indicating the difference between the first (val1) and second (val2) rounded to 2 decimals
     *          Negatives indicate val1 < val2
     *          Zero indicates val1 == val2
     *          Positives indicate val1 > val2
     */
    public static double compareMoneyValues(double val1, double val2) {
        return Math.round((val1 - val2)* 100.00)/100.00;
    }
    public static double compareMoneyValues(double val1, int val2) {
        return compareMoneyValues(val1, Integer.valueOf(val2).doubleValue());
    }
    public static double compareMoneyValues(double val1, BigDecimal val2) {
        return compareMoneyValues(val1, val2.doubleValue());
    }
    public static double compareMoneyValues(double val1, String val2) {
        return compareMoneyValues(val1, convertMoneyStringToDouble(val2));
    }

    public static double compareMoneyValues(int val1, int val2) {
        return compareMoneyValues(Integer.valueOf(val1).doubleValue(), Integer.valueOf(val2).doubleValue());
    }
    public static double compareMoneyValues(int val1, double val2) {
        return compareMoneyValues(Integer.valueOf(val1).doubleValue(), val2);
    }
    public static double compareMoneyValues(int val1, BigDecimal val2) {
        return compareMoneyValues(Integer.valueOf(val1).doubleValue(), val2.doubleValue());
    }
    public static double compareMoneyValues(int val1, String val2) {
        return compareMoneyValues(Integer.valueOf(val1).doubleValue(), convertMoneyStringToDouble(val2));
    }

    public static double compareMoneyValues(BigDecimal val1, int val2) {
        return compareMoneyValues(val1.doubleValue(), Integer.valueOf(val2).doubleValue());
    }
    public static double compareMoneyValues(BigDecimal val1, double val2) {
        return compareMoneyValues(val1.doubleValue(), val2);
    }
    public static double compareMoneyValues(BigDecimal val1, BigDecimal val2) {
        return compareMoneyValues(val1.doubleValue(), val2.doubleValue());
    }
    public static double compareMoneyValues(BigDecimal val1, String val2) {
        return compareMoneyValues(val1.doubleValue(), convertMoneyStringToDouble(val2));
    }

    public static double compareMoneyValues(String val1, int val2) {
        return compareMoneyValues(convertMoneyStringToDouble(val1), Integer.valueOf(val2).doubleValue());
    }
    public static double compareMoneyValues(String val1, double val2) {
        return compareMoneyValues(convertMoneyStringToDouble(val1), val2);
    }
    public static double compareMoneyValues(String val1, BigDecimal val2) {
        return compareMoneyValues(convertMoneyStringToDouble(val1), val2.doubleValue());
    }
    public static double compareMoneyValues(String val1, String val2) {
        return compareMoneyValues(convertMoneyStringToDouble(val1), convertMoneyStringToDouble(val2));
    }

    private static double convertMoneyStringToDouble(String value) {
        Double retVal = 0.0;
        String adjStr = value.replace("$", "")
                .replace(",", "")
                .replace("(", "")
                .replace(")", "")
                .replace("%", "")
                .trim();
        try {
            retVal = Double.valueOf(adjStr);
        } catch (Exception e) {
            printLine("Unable to parse string [" + value + "] to a double ( omitting characters $,()% )");
        }
        return retVal;
    }

    /**
     * Returns the time variance in seconds between the two provided times
     * @param timeA First String time value
     * @param formatA the format which the timeA is formatted (SimpleDateFormat formats)
     * @param timeB Second String time value
     * @param formatB the format which the timeB is formatted (SimpleDateFormat formats)
     * @return timeB's relation to timeA. 1 = 1s after timeA; -5 = 5s before timeA; 0 = same time
     */
    public static int compareTime(String timeA, String formatA, String timeB, String formatB) throws ParseException {
        SimpleDateFormat formatterA = new SimpleDateFormat(formatA);
        SimpleDateFormat formatterB = new SimpleDateFormat(formatB);
        Date dateA = formatterA.parse(timeA);
        Date dateB = formatterB.parse(timeB);
        return Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(dateB.getTime() - dateA.getTime())).intValue();
    }

    public static boolean compareTime(String timeA, String formatA, String timeB, String formatB, int varianceInSeconds) {
        int difference;
        try {
            difference = compareTime(timeA, formatA, timeB, formatB);
        } catch (ParseException e) {
            printLine(e.getMessage());
            return false;
        }
        if(Math.abs(difference) <= varianceInSeconds) {
            return true;
        }
        return false;
    }

    public static boolean compareTime(String timeA, String timeB, int varianceInSeconds) {
        String defaultFormat = "h:mm:ss a/z";
        return compareTime(timeA, defaultFormat, timeB, defaultFormat, varianceInSeconds);
    }
}
