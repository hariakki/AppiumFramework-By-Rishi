package com.twinspires.qa.core.util;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Created by dalwinder.singh on 4/12/17.
 */

public class TestNGListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        if(!result.getThrowable().toString().contains("AssertionError")){
            String[] errorMessage = result.getThrowable().toString().split("\\(Session info");
            Util.takeScreenShot(errorMessage[0]);
        }
    }

    public void onFinish(ITestContext context) {}

    public void onTestStart(ITestResult result) {   }

    public void onTestSuccess(ITestResult result) {   }

    public void onTestSkipped(ITestResult result) {   }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {   }

    public void onStart(ITestContext context) {   }
}
