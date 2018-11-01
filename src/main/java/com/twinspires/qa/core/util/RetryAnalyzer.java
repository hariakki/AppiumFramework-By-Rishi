package com.twinspires.qa.core.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private int maxCount = 0; //Specific amount to retry a failed test
    String retryCount = System.getProperty("retryCount");

    @Override
    public boolean retry(ITestResult result) {
        setMaxCount();
        if(count < maxCount) {
            count++;
            return true;
        }
        return false;
    }

    private void setMaxCount(){
        try{
            if(!retryCount.isEmpty()||retryCount!=null){
                maxCount = Integer.valueOf(retryCount);
            }
        }
        catch (Exception e){
            System.out.println("Finishing initial run and not retrying");
        }
    }
}