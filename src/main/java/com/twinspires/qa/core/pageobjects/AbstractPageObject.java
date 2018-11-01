package com.twinspires.qa.core.pageobjects;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.util.JsWaiter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class AbstractPageObject {

    protected TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
    protected Affiliate affiliate = Affiliate.fromString(System.getProperty("aff", "ts"));
    protected JsWaiter jsWaiter;

    public AbstractPageObject(WebDriver driver) {
        this.driver = driver;
        jsWaiter = new JsWaiter(driver);
    }


    protected WebDriver driver;

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void loadPage(String url) {
        driver.get(url);
        sleep(3000);
    }

    public void waitForRedirect(String expUrl) {
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.urlToBe(expUrl));
    }

    public void waitForRedirect(int seconds, String expUrl) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until(ExpectedConditions.urlToBe(expUrl));
    }

    public void waitForURLContains(String url) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void waitForURLContains(String url, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void waitForLoad() {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1).
                executeScript("return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(pageLoadCondition);
    }

    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = driver1 -> ((JavascriptExecutor) driver1)
                .executeScript("return document.readyState").toString().equals("complete");
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(expectation);
        } catch (Throwable error) {
        }
        jsWaiter.waitJQueryAngular();
    }

    protected WebElement waitForElement(int seconds, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElement(int seconds, By element) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        return wait.until(ExpectedConditions.visibilityOf((WebElement) element));
    }

    /**
     * Waits for the element's presence to match the desired state.
     *
     * @param seconds        the max time to wait for the element to be absent
     * @param element        the element to check for's absence
     * @param desiredPresent true to wait until element is present; false to wait until absent
     * @return the WebElement if it is present, null if it is absent
     */
    protected WebElement waitForElementPresence(int seconds, WebElement element, boolean desiredPresent) {
        int poll = 500;

        if (desiredPresent) {
            return waitForElement(seconds, element);
        } else {
            for (int i = 0; i <= (seconds * 1000); i += poll) {
                try {
                    if (element.isDisplayed() == false) {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
                sleep(poll);
            }
            return element;
        }
    }

    /**
     * Waits for the presence of any number of elements up to the allocated time. Stops when any are found.
     *
     * @param waitInSeconds the total time in seconds to wait for the visibility of any element
     * @param elements      As many web elements to look for isDisplayed() == true
     * @return indicates which element was found. 1 for 1st, etc.  Exception if none are found
     */
    protected int waitForElementsOr(int waitInSeconds, WebElement... elements) {
        int found = 3; // results
        long timeout = 500; // timeout between polling
        long elapsedTime = System.nanoTime(); // Timer start (in nanoseconds)
        long endTime = TimeUnit.SECONDS.toNanos(waitInSeconds) + elapsedTime; // End clock time (seconds to nanoseconds)

        do {
            elapsedTime = System.nanoTime(); // determine elapsed time first to allow for maximum presence checks
            sleep(timeout);

            for (int i = 0; i < elements.length; i++) {
                try {
                    if (elements[i].isDisplayed()) {
                        return i + 1;
                    }
                } catch (Exception e) { /* ignore */ }
            }

        } while (elapsedTime <= endTime); // stop polling if end time was reached

        // None were found.  Perform first check to allow natural exception throwing
        elements[0].isDisplayed();
        return 1; // if this point is reached without exceptions, both elements were displayed
    }

    /**
     * Waits for the presence of both elements up to the allotted amount of time. Stops when both are found
     *
     * @param waitInSeconds the total time in seconds to wait for the visibility of the elements
     * @param elem1         the first element to check for its visibility
     * @param elem2         the second element to check for its visibility
     */
    protected void waitForElementsAnd(int waitInSeconds, WebElement elem1, WebElement elem2) throws Exception {
        int found = 0; // results
        long timeout = 500; // timeout between polling
        long elapsedTime = System.nanoTime(); // Timer start (in nanoseconds)
        long endTime = TimeUnit.SECONDS.toNanos(waitInSeconds) + elapsedTime; // End clock time (seconds to nanoseconds)

        do {
            elapsedTime = System.nanoTime(); // determine elapsed time first to allow for maximum presence checks
            sleep(timeout);

            if (found != 1) { // perform check until elem1 is found, then skip
                try { // check if elem1 is visible
                    if (elem1.isDisplayed()) {
                        found += 1;
                    }
                } catch (Exception e) { /* ignore */ }
            }

            if (found != 2) { // perform check until elem2 is found, then skip
                try { // check if elem2 is visible
                    if (elem2.isDisplayed()) {
                        found += 2;
                    }
                } catch (Exception e) { /* ignore */ }
            }

            if (found == 3) { // when both are found, exit method
                return;
            }
        } while (elapsedTime <= endTime); // stop polling if end time was reached

        // Exception Throwing: Includes both checks in case the elements start displaying, to ensure accuracy
        if (found == 1) {
            elem2.isDisplayed(); // Was NOT found
            elem1.isDisplayed(); // Was found
        } else { // found == 0 -or- found == 2
            elem1.isDisplayed(); // Was NOT found
            elem2.isDisplayed(); // May or may not have been found
        }
    }

    protected WebElement waitForClickableElement(int seconds, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForClickableElement(int seconds, By element) {

        WebDriverWait wait = new WebDriverWait(driver, seconds);
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementToBeInvisible(By locator, int timeInSeconds) {
        new WebDriverWait(driver, (long) timeInSeconds).until(ExpectedConditions.
                invisibilityOfElementLocated(locator));
    }

    protected void waitForElementToBeInvisible(WebElement element, int timeInSeconds) {
        new WebDriverWait(driver, (long) timeInSeconds).until(ExpectedConditions.invisibilityOf(element));
    }

    protected Boolean waitForElementToBeInvisible(int seconds, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        try {
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            return false;
        }
    }

    protected void waitForElementToBeVisible(By locator, int timeInMilliSeconds) {
        new WebDriverWait(driver, (long) timeInMilliSeconds).until(ExpectedConditions.
                presenceOfElementLocated(locator));
    }

    protected void waitForTextToBePresentInElement(WebElement element, int timeInSeconds, String text) {
        new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.
                textToBePresentInElement(element, text));
    }

    protected void waitForInvisibilityOfElementWithText(By by, int timeInSeconds, String text) {
        new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.invisibilityOfElementWithText(by, text));
    }

    protected void waitForValues(Select element) {
        while (element.getOptions().size() <= 1) {
            //Wait action code here
        }
    }

    public void waitUntilCountChanges(int seconds, List<WebElement> elements, int orgCount) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        try {
            wait.until((ExpectedCondition<Boolean>) driver1 -> {
                int elementCount = elements.size();
                if (elementCount != orgCount)
                    return true;
                else
                    return false;
            });
        } catch (Exception ex) {
        }
    }

    /**
     * Checks the element for non-blank text every 500 ms
     *
     * @param textElement the element which to extract the text
     * @param seconds     the total time to wait in seconds
     * @return the text found in the element
     */
    protected String getTextWhenPresent(WebElement textElement, int seconds) {
        String getText = "";
        boolean isDisp = false;
        int timer = seconds * 1000; // ms wait

        while (timer >= 0) {
            try {
                isDisp = textElement.isDisplayed();
            } catch (Exception e) {
                isDisp = false;
                timer -= 500;
                sleepTime(500);
            }

            if (isDisp) {
                getText = textElement.getText().trim();
                if (getText.isEmpty()) {
                    timer -= 500;
                    sleepTime(500);
                } else {
                    break;
                }
            }
        }
        return getText;
    }

    protected List<WebElement> getOptions(Select select) {
        List<WebElement> optionSelect = select.getOptions();
        for (int i = 1; i < optionSelect.size(); i++) {
            System.out.println(optionSelect.get(i).getText());
        }
        return optionSelect;
    }

    protected void removeTSLoadingOverlay() {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('_ts-loading-overlay').style.display = 'none'");
    }

    //This method will return integer in (0, size + 1), i.e. excludes 0 and size + 1
    //which is more suitable for the purpose finding a random element index.
    protected int getRandomElement(int size) {
        return (int) (Math.random()* (size + 1));
    }

    //TODO -- refactor this.
    protected int getRandomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Generates a list of unique random numbers
     *
     * @param min       the minumum random value
     * @param max       the maximum random value
     * @param amtOfRand how many random numbers to generate (up to the number of available numbers)
     * @return Array of unique random Integers
     */
    protected ArrayList<Integer> getUniqueRandomInts(int min, int max, int amtOfRand) throws Exception {
        int availSelections;
        ArrayList<Integer> availableSelections = new ArrayList<>();
        ArrayList<Integer> uniqueSelections = new ArrayList<>();

        if (min < max) {
            availSelections = max - min + 1;
            if (amtOfRand > availSelections) {
                throw new IllegalArgumentException("Cannot acquire more numbers [" + amtOfRand + "] " +
                        "than that which is available [" + availSelections + "]");
            } else if (amtOfRand <= 0) {
                throw new IllegalArgumentException("Amount of integers to acquire must be greater than zero " +
                        "[" + amtOfRand + "]");
            }

            for (int i = min; i <= max; i++) { // Builds a list of all available options
                availableSelections.add(i);
            }

            Collections.shuffle(availableSelections);
            for (int i = 0; i < amtOfRand; i++) {
                uniqueSelections.add(availableSelections.get(i));
            }
            return uniqueSelections;

        } else {
            throw new IllegalArgumentException("'min' [" + min + "] must be less than 'max' [" + max + "]");
        }
    }

    public void waitForPresence(WebDriver driver, String javascript, String s) {
        javascriptWait(driver, javascript, s, true);
    }

    public void waitForAbsence(WebDriver driver, String javascript, String s) {
        javascriptWait(driver, javascript, s, false);
    }

    private String getJavascriptOutputString(WebDriver driver, String javascript) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String returnVal;

        try {
            returnVal = (String) js.executeScript(javascript);
        } catch (ClassCastException e) {
            return null;
        }
        return returnVal;
    }

    private void javascriptWait(WebDriver driver, String javascript, String s, boolean continueCondition) {

        String output;
        boolean isPresent;
        int currentTry = 0;
        int maxTries = 30;

        do {
            output = getJavascriptOutputString(driver, javascript);

            if (null != output) {
                isPresent = output.contains(s);
            } else {
                isPresent = false;
            }

            if (isPresent == continueCondition) {
                sleep(1000);
                System.out.println("Sleeping for 1 second");
            }
            currentTry++;
        } while (isPresent == continueCondition && (currentTry < maxTries));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void waitForFieldToHaveValue(WebDriver driver, WebElement locator, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);

        wait.until((ExpectedCondition<Boolean>) driver1 -> {
            WebElement element = locator;
            String text = element.getText();
            if (!text.equals("")) {
                return true;
            } else {
                return false;
            }
        });
    }


    public void waitForAttribute(WebDriver driver, WebElement locator, String attribute, String value) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        wait.until((ExpectedCondition<Boolean>) driver1 -> {
            WebElement element = locator;
            String enabled = element.getAttribute(attribute).toString();
            if (enabled.equals(value)) {
                return true;
            } else {
                return false;
            }
        });
    }

    public static void print(String statement) {
        Reporter.log(statement);
        System.out.println(statement);
    }

    public boolean compareImages(File fileA, File fileB) {
        try {
            //Buffer data from first image file
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            //Get size of first data buffer
            int sizeA = dbA.getSize();
            //Buffer data from second image file
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            //Get size of second data buffer
            int sizeB = dbB.getSize();
            //compare data buffer objects
            if (sizeA == sizeB) {
                for (int i = 0; i < sizeA; i++) {
                    if (dbA.getElem(i) != dbB.getElem(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to compare image files");
            return false;
        }
    }

    /**
     * Determines if either string contains the other, case-insensitive
     *
     * @param text1
     * @param text2
     * @return true if either string is contained within the other
     */
    protected boolean compareStringsContain(String text1, String text2) {
        String t1Upper = text1.trim().toUpperCase();
        String t2Upper = text2.trim().toUpperCase();

        // When only one is empty, contains() always returns true
        if ((t1Upper.isEmpty() && !t2Upper.isEmpty())
                || (!t1Upper.isEmpty() && t2Upper.isEmpty())) {
            return false;
        } else if (t1Upper.contains(t2Upper)
                || (t2Upper.contains(t1Upper))) {
            return true;
        }
        return false;
    }

    public File getScreenshotOfWebElement(WebElement element, String pathToSaveScreenshot) {
        // Get entire page screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = null;
        try {
            fullImg = ImageIO.read(screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the location of element on the page
        Point point = element.getLocation();

        // Get width and height of the element
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
                eleWidth, eleHeight);
        try {
            ImageIO.write(eleScreenshot, "png", screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Copy the element screenshot to disk
        File savedScreenshot = new File(pathToSaveScreenshot);
        try {
            FileUtils.copyFile(screenshot, savedScreenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedScreenshot;
    }

    public void deleteFile(File file) {
        file.delete();
    }

    public void sendActionsText(WebElement element, String text) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.click();
        actions.sendKeys(text);
        actions.build().perform();
    }

    protected void waitAndSwitchToIframe(int seconds, String frameName) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
    }

    protected void waitAndSwitchToIframe(int seconds, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
    }

    protected void waitForElementToBeVisible(WebElement element, int timeInSeconds) {
        new WebDriverWait(driver, (long) timeInSeconds).until(ExpectedConditions.
                visibilityOf(element));
    }

    public void scrollToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    public boolean isGTMLoaded() {
        waitForPageLoaded();
        String output;
        boolean isPresent;
        int currentTry = 0;
        int maxTries = 10;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        do {
            waitForPageLoaded();
            output = js.executeScript("return window.dataLayer.filter(function(obj) { return obj.event == 'gtm.js'; }).length > 0").toString();

            if (null != output && output.contains("true")) {
                isPresent = true;
            } else {
                print("Console output: " + output);
                isPresent = false;
                js.executeScript("window.location.reload(true);");
            }
            currentTry++;
        } while (isPresent == false && (currentTry < maxTries));
        return isPresent;
    }

    public void waitForSelectValues(Select element) {
        ExpectedCondition<Boolean> hasMoreThanOneValue = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return element.getOptions().size() >= 1;
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(hasMoreThanOneValue);
    }

    public static BigDecimal formatBigDecimal(String bigDecimal) {
        //Handling the $ and spaces in the value
        String bigDecimalUpdated = "";
        try {
            if (bigDecimal.contains("$")) {
                bigDecimalUpdated = bigDecimal.substring(bigDecimal.indexOf("$") + 1).replaceAll(" ", "");
            } else
                bigDecimalUpdated = bigDecimal.replaceAll(" ", "");

            //Format Big Decimal
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "#,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            //Parse Big Decimal
            BigDecimal formattedBigDecimal = (BigDecimal) decimalFormat.parse(bigDecimalUpdated);
            return formattedBigDecimal.setScale(2);
        } catch (Exception e) {
            print("Unable to parse the value " + bigDecimalUpdated);
            return null;
        }
    }

    /**
     * Ensures value of number can be read as USD currency by rounding to two decimal places.
     *
     * @param origValue The value to ensure only has two decimals
     * @return The converted value.
     */
    public static BigDecimal convertToMoneyBigDecimal(double origValue) {
        return convertToMoneyBigDecimal(new BigDecimal(origValue));
    }

    public static BigDecimal convertToMoneyBigDecimal(BigDecimal origValue) {
        return origValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    public static BigDecimal convertToMoneyBigDecimal(String origValue) {
        String strStripped;
        BigDecimal bdVal;

        strStripped = origValue.replace("$", "").replace(",", "").replace(" ", "");
        bdVal = new BigDecimal(strStripped);

        return convertToMoneyBigDecimal(bdVal);
    }

    /**
     * Converts and formates a number into a money string "$#,###.##"
     *
     * @param origValue the value to be converted to a formatted money string
     * @return the formatted string value
     */
    public static String convertToMoneyString(String origValue) {
        String strStripped = origValue.replace("$", "").replace(",", "").replace(" ", "");
        return convertToMoneyString(new BigDecimal(strStripped));
    }

    public static String convertToMoneyString(double origValue) {
        return convertToMoneyString(new BigDecimal(origValue));
    }

    public static String convertToMoneyString(BigDecimal origValue) {
        BigDecimal bdVal = convertToMoneyBigDecimal(origValue);
        DecimalFormat formatUSD = new DecimalFormat("$#,##0.00");
        return formatUSD.format(bdVal).toString();
    }

    public static String convertToMoneyStringLeadingZero(String origValue) {
        String strStripped = origValue.replace("$", "").replace(",", "").replace(" ", "");
        return convertToMoneyStringLeadingZero(new BigDecimal(strStripped));
    }

    public static String convertToMoneyStringLeadingZero(double origValue) {
        return convertToMoneyStringLeadingZero(new BigDecimal(origValue));
    }

    public static String convertToMoneyStringLeadingZero(BigDecimal origValue) {
        BigDecimal bdVal = convertToMoneyBigDecimal(origValue);
        DecimalFormat formatUSD = new DecimalFormat("$#,##0.00");
        return formatUSD.format(bdVal).toString();
    }

    public boolean isFieldEmpty(WebElement element) {
        boolean isEmpty = false;
        if (element.getAttribute("value").isEmpty()) {
            isEmpty = true;
        }
        return isEmpty;
    }

    public void clickUsingJS(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click()", element);
    }

    public boolean isNative() {
        String browserType = System.getProperty("browser", "");
        if (browserType.equalsIgnoreCase("nativeiOS") ||
                browserType.equalsIgnoreCase("nativeAndroid")) {
            return true;
        } // else
        return false;
    }

    /**
     * @return boolean true when mchrome emulator is being utilized
     */
    public boolean isMobile() {
        String browserType = System.getProperty("emulation", "false");
        if (browserType.equalsIgnoreCase("true")) {
            return true;
        } // else
        return false;
    }

    public void clickDropDownValue(List<WebElement> dropdown, String value) {
        int position = -1;

        for (int i = 0; i < dropdown.size(); i++) {
            if (dropdown.get(i).getText().trim().equalsIgnoreCase(value)) {
                position = i;
                break;
            }
        }

        if (position > -1) {
            dropdown.get(position).click();
            waitForPageLoaded();
        } else {
            print("Unable to find [" + value + "] in drop down");
        }
    }

    public void navBack() {
        driver.navigate().back();
        sleepTime(2000);
        waitForPageLoaded();
    }

    public void navForward() {
        driver.navigate().forward();
        sleepTime(1000);
        waitForPageLoaded();
    }

    public void navRefresh() {
        driver.navigate().refresh();
        sleepTime(1000);
        waitForPageLoaded();
    }

    /**
     * Takes a JWT ID as input and splits it into its
     * 3 parts (Header, Body, Signature)
     * then decodes them into plain text
     *
     * @param jwtId
     * @return decodedJWT
     */
    public ArrayList<String> decodeJwtId(String jwtId) {

        //Splits the JWT ID into 3 parts
        String[] split_string = jwtId.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        //Decodes each part
        Base64 base64Url = new Base64(true);
        String header = new String(base64Url.decode(base64EncodedHeader));
        String body = new String(base64Url.decode(base64EncodedBody));
        String signature = new String(base64Url.decode(base64EncodedSignature));

        ArrayList<String> decodedJwt = new ArrayList<String>();
        decodedJwt.add(header);
        decodedJwt.add(body);
        decodedJwt.add(signature);

        return decodedJwt;
    }

    /**
     * This method return a list of values for a xpath attribute based on a xpath expression by parsing the page source
     * @param pageSource
     * @param xpathExpression
     * @param attribute
     * @return
     */

    public List<String> getAttributeValues(String pageSource, String xpathExpression, String attribute) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        XPathFactory factory = XPathFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = null;
        Document doc = null;
        XPath xpath = null;
        String expression;
        NodeList nodeList;
        List<String> attributeValues = new ArrayList<>();

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(pageSource));
            doc = documentBuilder.parse(is);
            xpath = factory.newXPath();
            expression = xpathExpression;
            nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                attributeValues.add(nodeList.item(i).getAttributes().getNamedItem(attribute).getTextContent());
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return attributeValues;
    }


    public String getAttributeValue(String pageSource, String xpathExpression, String attribute) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        XPathFactory factory = XPathFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = null;
        Document doc = null;
        XPath xpath = null;
        Node node;
        String attributeValue = "";

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(pageSource));
            doc = documentBuilder.parse(is);
            xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpression);
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            attributeValue = node.getAttributes().getNamedItem(attribute).getTextContent();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return attributeValue;
    }

    /**
     * Takes in a phrase of at least 1 word and capitalizes the first letter of each word.
     * All other letters will be lower case even if they were capitalized before
     *
     * Input: "capitALIze thESE WOrds"
     * Output: "Capitalize These Words"
     *
     * @param phrase
     * @return result
     */
    public String capitalizeEachWord(String phrase) {
        StringBuilder result = new StringBuilder(phrase.length());
        String words[] = phrase.toLowerCase().split("\\b");

        for (int i = 0; i < words.length; i++) {
            result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");
        }

        //removes excess spaces surrounding punctuation.
        return result.toString().replace(" .", ".").replace("( ", "(").replace(" )", ")").replace("   ", " ").trim();
    }
}