package com.twinspires.qa.core.util;

import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import com.twinspires.qa.core.testobjects.DeviceInfo;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by dalwinder.singh on 7/29/16.
 */
public class Driver {
    public static WebDriver driver;
    public Map<String, String> driverConfig = new HashMap<String, String>();
    boolean tsWebView = false;
    private static String CDI_DEVELOPER_ID = "5UQQFX75TQ";
    private static String APPIUM_LOCAL_URL = "http://127.0.0.1:4723/wd/hub";
    private static String APPIUM_CLOUD_URL = "https://us1.appium.testobject.com/wd/hub";
    private String TEST_OBJECT_IOS_KEY = "";
    private String TEST_OBJECT_ANDROID_KEY = "";
    ThreadLocal<AndroidDriver> remoteAndroidDriver = new ThreadLocal<AndroidDriver>();
    ThreadLocal<IOSDriver> remoteIOSDriver = new ThreadLocal<IOSDriver>();
    TestDataQueries testDataQueries = new TestDataQueries();


    public Driver() {
        setDriverRunConfigurations();
        setDriver();
        if (driverConfig.get("browserType").equalsIgnoreCase("chrome") ||
                driverConfig.get("browserType").equalsIgnoreCase("firefox")) {
           // driver.manage().window().fullscreen();
            driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        }
    }

    public Driver(String[] testConfigurations) {
        setDriverRunConfigurations();
        setDriverTestConfigurations(testConfigurations); // Test config setting takes priority over run config
        setDriver();
        if (driverConfig.get("browserType").equalsIgnoreCase("chrome") ||
                driverConfig.get("browserType").equalsIgnoreCase("firefox")) {
          //  driver.manage().window().fullscreen();
            driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        }
    }

    public Driver(DeviceInfo deviceInfo) {
        DesiredCapabilities capabilities;
        String deviceType = System.getProperty("deviceType");
        setTestObjectApiKey();
        if(deviceType.toLowerCase().contains("ios")) {
            capabilities = getIOSDeviceCapabilities(deviceInfo.getPlatformName(),
                    deviceInfo.getDeviceName(), deviceInfo.getDeviceVersion());
        } else {
            capabilities = getAndroidDeviceCapabilities(deviceInfo.getPlatformName(),
                    deviceInfo.getDeviceName(), deviceInfo.getDeviceVersion());
        }
        setDriver(capabilities);
    }

    private void setDriverRunConfigurations() {
        driverConfig.put("chromeDriverPath", System.getProperty("chromeDriverPath", "/chromedriver/chromedriver.exe"));
        driverConfig.put("firefoxDriverPath",System.getProperty("firefoxDriverPath", "/firefoxdriver/geckodriver.exe"));
        driverConfig.put("IExplorerDriverPath",System.getProperty("IExplorerDriverPath", "/iedriver/ IEDriverServer.exe"));
        driverConfig.put("browserType", System.getProperty("browser", "chrome"));
        driverConfig.put("operatingSystem", System.getProperty("os.name", "windows").toLowerCase());
        driverConfig.put("gridUrl", System.getProperty("grid", "abc"));
        driverConfig.put("deviceHost", System.getProperty("deviceHost", "localhost"));
        driverConfig.put("emulationEnabled", System.getProperty("emulation", "false"));
        driverConfig.put("geolocationEnabled", System.getProperty("geolocation", "true"));
        driverConfig.put("deviceName", System.getProperty("deviceName", "iphone-6"));
    }

    private void setDriverTestConfigurations(String[] testConfigurations) {
        for (String testConfiguration : testConfigurations) {
            if (testConfiguration.contains("::")) {
                System.out.println("Overriding run configuration with test configuration: " + testConfiguration);
                String[] settingAndValue = testConfiguration.split("::");
                driverConfig.put(settingAndValue[0], settingAndValue[1]);
            }
        }
    }

    public void setDriver(DesiredCapabilities desiredCapabilities){
        String deviceType = System.getProperty("deviceType");
        if(deviceType.toLowerCase().contains("ios")){
            if(deviceType.equalsIgnoreCase("iOS Simulator") || deviceType.equalsIgnoreCase("iOS Real")){
                IOSDriver iosDriver = null;
                try {
                    iosDriver = new IOSDriver(new URL(APPIUM_LOCAL_URL), desiredCapabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                driver = iosDriver;
            } else {
                try {
                    remoteIOSDriver.set(new IOSDriver(
                            new URL(APPIUM_CLOUD_URL),
                            desiredCapabilities));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                driver = remoteIOSDriver.get();
            }
        } else if (deviceType.toLowerCase().contains("android")){
            if(deviceType.equalsIgnoreCase("Android Simulator") || deviceType.equalsIgnoreCase("Android Real")){
                AndroidDriver androidDriver = null;
                try {
                    androidDriver = new AndroidDriver(new URL(APPIUM_LOCAL_URL), desiredCapabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                driver = androidDriver;
            } else {
                try {
                    remoteAndroidDriver.set(new AndroidDriver(
                            new URL(APPIUM_CLOUD_URL),
                            desiredCapabilities));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                driver = remoteAndroidDriver.get();
            }
        }
    }

    public void setDriver() {

        ChromeOptions options = null;
        DesiredCapabilities capabilities = null;

        if (driverConfig.get("gridUrl").contains("wd/hub")) {
            capabilities = new DesiredCapabilities();
            capabilities.setPlatform(Platform.WINDOWS);
            capabilities.setBrowserName(driverConfig.get("browserType").toLowerCase());

            if (driverConfig.get("emulationEnabled").equalsIgnoreCase("true")) {
                capabilities.setCapability(ChromeOptions.CAPABILITY, getEmulationOptions());
            }

            try {
                driver = new RemoteWebDriver(new URL(driverConfig.get("gridUrl")), capabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            if (driverConfig.get("browserType").equalsIgnoreCase("Firefox")) {
                if (driverConfig.get("operatingSystem").contains("windows")) {
                    File firefoxFile = new File(driverConfig.get("firefoxDriverPath"));
                    System.setProperty("webdriver.gecko.driver", firefoxFile.getAbsolutePath());
                }
                driver = new FirefoxDriver();
            } else if (driverConfig.get("browserType").toLowerCase().contains("chrome")) {
                if (driverConfig.get("operatingSystem").contains("windows")) {
                    File chromeFile = new File(driverConfig.get("chromeDriverPath"));
                    System.setProperty("webdriver.chrome.driver", chromeFile.getAbsolutePath());
                }
                if (driverConfig.get("emulationEnabled").equalsIgnoreCase("true")) {
                    driver = new ChromeDriver(getEmulationOptions());
                } else {
                    driver = new ChromeDriver(getStandardOptions());
                }
            } else if (driverConfig.get("browserType").equalsIgnoreCase("IExplorer")) {
                System.setProperty("webdriver.ie.driver", driverConfig.get("iExplorerDriverPath"));
                driver = new InternetExplorerDriver();
            } else if (driverConfig.get("browserType").equalsIgnoreCase("Edge")) {
                System.setProperty("webdriver.edge.driver", driverConfig.get("edgeDriverPath"));
                driver = new EdgeDriver();
            } else if (driverConfig.get("browserType").equalsIgnoreCase("nativeiOS")) {
                AppiumDriver<WebElement> appiumDriver = null;
                File app = new File(driverConfig.get("appPath"));
                capabilities = new DesiredCapabilities();
                capabilities.setCapability("platformVersion", driverConfig.get("mobileOSVersion"));
                capabilities.setCapability("deviceName", driverConfig.get("deviceName"));
                capabilities.setCapability("bundle", app);
                capabilities.setCapability("udid", driverConfig.get("deviceUDID"));
                capabilities.setCapability("bundleId", driverConfig.get("applicationId"));
                capabilities.setCapability("xcodeConfigfile", "Config.xcconfig");
                capabilities.setCapability("automationName", "XCUITest");
                try {
                    appiumDriver = new IOSDriver(new URL("http://" + driverConfig.get("mobileDeviceIp") + "/wd/hub"), capabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                while (tsWebView == false) {
                    System.out.println("Unable to find TS WebView");

                    if (isTSWebViewReady(appiumDriver) == true) {
                        System.out.println("Switching to TS WebView");
                        appiumDriver.context(driverConfig.get("webviewName"));
                        break;
                    }
                }
                driver = appiumDriver;

            } else if (driverConfig.get("browserType").equalsIgnoreCase("nativeAndroid")) {
                AppiumDriver<WebElement> appiumDriver = null;
                File app = new File(driverConfig.get("appPath"));
                capabilities = new DesiredCapabilities();
                capabilities.setCapability("deviceName", "Mobile");
                capabilities.setCapability("app", app.getAbsolutePath());
                capabilities.setCapability("appPackage", "com.twinspires.mobile");
                capabilities.setCapability("appActivity", "com.twinspires.mobile.MainActivity");
                capabilities.setCapability("noReset", true);
                capabilities.setCapability("fullReset", false);
                try {
                    appiumDriver = new AndroidDriver<WebElement>(new URL("http://" + driverConfig.get("mobileDeviceIp") + "/wd/hub"), capabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                while (tsWebView == false) {
                    System.out.println("Unable to find TS WebView");

                    if (isTSWebViewReady(appiumDriver) == true) {
                        System.out.println("Switching to TS WebView");
                        appiumDriver.context(driverConfig.get("webviewName"));
                        System.out.println("Switched to: " + driverConfig.get("webviewName"));
                        break;
                    }
                }
                driver = appiumDriver;
            } else {
                if (driverConfig.get("operatingSystem").contains("windows")) {
                    File firefoxFile = new File(driverConfig.get("firefoxDriverPath"));
                    System.setProperty("webdriver.gecko.driver", firefoxFile.getAbsolutePath());
                }
                driver = new FirefoxDriver();
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public ChromeOptions getEmulationOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        Map<String, String> mobileEmulation = new HashMap<String, String>();

        // Start Chrome maximized
        chromeOptions.addArguments("--start-maximized");

        // Remove "Automation Software" Information bar
        chromeOptions.addArguments("disable-infobars");

        // Enable Flash
        prefs.clear();
        prefs.put("profile.default_content_setting_values.plugins", 1);
        prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
        prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);

        // Enable Mobile Emulation
        mobileEmulation.put("deviceName", driverConfig.get("deviceName").replace("-", " "));
        prefs.put("mobileEmulation", mobileEmulation);
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        // Set Geolocation
        if (driverConfig.get("geolocationEnabled").equalsIgnoreCase("false")) {
            prefs.put("profile.default_content_setting_values.geolocation", 2);
        }

        chromeOptions.setExperimentalOption("prefs", prefs);

        return chromeOptions;
    }

    public ChromeOptions getStandardOptions() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();

        // Start Chrome maximized
        options.addArguments("--start-maximized");
        // Disable Ephemeral Flash Permissions Flag
        options.addArguments("--disable-features=EnableEphemeralFlashPermission");

        // Enable Flash
        prefs.put("profile.default_content_setting_values.plugins", 1);
        prefs.put("profile.content_settings.plugin_whitelist.adobe-flash-player", 1);
        prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.adobe-flash-player", 1);
        // Enable flash for all sites for Chrome 69
        prefs.put("profile.content_settings.exceptions.plugins.*,*.setting", 1);

        // Remove "Automation Software" Information bar
        options.addArguments("disable-infobars");

        // Set Geolocation
        if (driverConfig.get("geolocationEnabled").equalsIgnoreCase("false")) {
            prefs.put("profile.default_content_setting_values.geolocation", 2);
        }

        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private boolean isTSWebViewReady(AppiumDriver<WebElement> driver) {
        System.out.println("Context Handle List:");
        Set<String> contextNames = driver.getContextHandles();
        for (String contextName : contextNames) {
            if (contextName.contains("WEBVIEW")) {
                System.out.println(contextName);
                driverConfig.put("webviewName", contextName);
                tsWebView = true;
                break;
            }
        }
        return tsWebView;
    }

    private DesiredCapabilities getIOSDeviceCapabilities(String platformName, String deviceName, String deviceVersion) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        String deviceType = System.getProperty("deviceType");


        //Setting ipa file path
        String appPath = System.getProperty("appPath","/Twinspires.ipa");
        File app = new File(appPath);

        //Setting desired capabilities
        if(deviceType.equalsIgnoreCase("iOS Simulator") || deviceType.equalsIgnoreCase("iOS Real") ) {
            desiredCapabilities.setCapability("app", app.getAbsolutePath());
            desiredCapabilities.setCapability("automationName", "XCUITest");
            desiredCapabilities.setCapability("xcodeOrgId", CDI_DEVELOPER_ID);
            desiredCapabilities.setCapability("xcodeSigningId", "iPhone Developer");
            if(deviceType.equalsIgnoreCase("iOS Real")) {
                desiredCapabilities.setCapability("udid",System.getProperty("udid"));
            }
        } else if (deviceType.equalsIgnoreCase("iOS Cloud")) {
            desiredCapabilities.setCapability("testobjectApiKey", TEST_OBJECT_IOS_KEY);
            desiredCapabilities.setCapability("testobject_app_id", "1");
            desiredCapabilities.setCapability("tunnelIdentifier", "cdi-tunnel");
        }

        desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 60000);
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("platformName", platformName);
        desiredCapabilities.setCapability("deviceName", deviceName);
        desiredCapabilities.setCapability("platformVersion", deviceVersion);
        return desiredCapabilities;
    }

    private DesiredCapabilities getAndroidDeviceCapabilities(String platformName, String deviceName, String deviceVersion) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        String deviceType = System.getProperty("deviceType");

        //Setting ipa file path
        String appPath = System.getProperty("appPath","/Twinspires.apk");
        File app = new File(appPath);

        //Setting desired capabilities
        if(deviceType.equalsIgnoreCase("Android Simulator") || deviceType.equalsIgnoreCase("Android Real") ) {
            desiredCapabilities.setCapability("app", app.getAbsolutePath());
            desiredCapabilities.setCapability("automationName", "UiAutomator2");
        }
        else if (deviceType.equalsIgnoreCase("Android Cloud")){
            desiredCapabilities.setCapability("testobject_api_key", TEST_OBJECT_ANDROID_KEY );
        }

        desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,30000);
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("deviceName",deviceName );
        desiredCapabilities.setCapability("platformVersion", deviceVersion);
        desiredCapabilities.setCapability("platformName", platformName);
        return desiredCapabilities;
    }

    public void setTestObjectApiKey(){
        String platformName = System.getProperty("platform");
        String environment  = System.getProperty("env");
        String deviceType = System.getProperty("deviceType");
        if(deviceType.toLowerCase().contains("cloud")) {
            if (platformName.equalsIgnoreCase("ios")) {
                TEST_OBJECT_IOS_KEY = testDataQueries.getTestObjectApiKey(platformName, environment);
            } else {
                TEST_OBJECT_ANDROID_KEY = testDataQueries.getTestObjectApiKey(platformName, environment);
            }
        }
    }
}
