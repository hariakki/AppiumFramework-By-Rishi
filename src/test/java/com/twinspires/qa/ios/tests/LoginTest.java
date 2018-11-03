//package com.twinspires.qa.ios.tests;
//
//import org.testng.annotations.Factory;
//import org.testng.annotations.Test;
//
///**
// * Created by dalwinder.singh on 8/9/18.
// */
//public class LoginTest extends IOSAbstractTest {
//
//    @Factory(dataProvider = "devices")
//    public LoginTest(String platformName, String deviceName, String deviceVersion) {
//
//        deviceInfo.setPlatformName(platformName);
//        deviceInfo.setDeviceName(deviceName);
//        deviceInfo.setDeviceVersion(deviceVersion);
//    }
//
//    @Test(enabled = true, testName = "Verify login page functionality",
//            groups = {"iOS.AccountManagement"})
//    public void verifyLoginFunctionality() {
//
//        // 1-4) Click on Login link and verify various login view elements are displayed correctly
//        loginView.clickLoginLink();
//        assertTrue(loginView.isTouchIDElementsDisplayed(),
//                "Verify touch id button and text are displayed correctly");
//        assertTrue(loginView.isJoinNowBtnDisplayed(),
//                "Verify Join Now button is displayed correctly");
//        assertTrue(loginView.isTermsAndConditionsTextDisplayed(),
//                "Verify Terms and conditions text is displayed correctly");
//        assertTrue(loginView.isForgotPasswordTextDisplayed(),
//                "Verify forgot password text is displayed correctly");
//
//        // 5) Click on close button and verify sign in screen is no longer visible to user
//        loginView.clickCloseSignInViewBtn();
//        assertTrue(loginView.isLoginLinkDisplayed(),
//                "Verify sign in screen is no longer visible to user");
//
//        // 6) Verify error message for invalid login credentials
//       // loginView.clickLoginLink();
//        //loginView.userLogin("!Test!", "Test");
//        //assertTrue(loginView.isInvalidCredentialsErrorMsg(),
//              //  "Please provide valid Username or Password");
//
//        // 7) Click on close button and relaunch login view. Verify error message for valid username and invalid password
//       // loginView.clickCloseSignInViewBtn();
//        //loginView.clickLoginLink();
//        //loginView.userLogin(getUsername("iOS General Account"), "Test");
//        //loginView.userLogin();
//        //assertTrue(loginView.isInvalidCredentialsErrorMsg(),
//               // "Verify error message for valid username and invalid password is correct");
////
//        // 8) Click on close button and relaunch login view. Verify valid login
//        //loginView.clickCloseSignInViewBtn();
//       // loginView.clickLoginLink();
//        //loginView.userLogin(getUsername("iOS General Account"),iosCredentials.getPassword());
//        //assertTrue(loginView.isLogoutBtnDisplayed(),
//               // "Verify user is logged in");
//
//        // 9) Logout from app and verify anonymous user cannot access an auth guarded page
//        //loginView.clickLogoutLink();
//        //loginView.clickLogoutBtn();
//       // loginView.clickClaimOfferLink();
//       // assertTrue(loginView.isLoginFieldsDisplayed(),
//             //   "Verify user is logged out and auth guarded page is not visible to anonymous user");
//
//       // assertAll();
//    }
//}
