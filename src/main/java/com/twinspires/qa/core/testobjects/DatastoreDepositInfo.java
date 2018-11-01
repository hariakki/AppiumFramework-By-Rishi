package com.twinspires.qa.core.testobjects;

import java.math.BigDecimal;

/**
 * Created by dalwinder.singh on 8/20/18.
 */
public class DatastoreDepositInfo {

    String depositMethod;
    String depositAmount;
    String gateway;

    public String getDepositMethod() {
        return depositMethod;
    }

    public void setDepositMethod(String depositMethod) {
        this.depositMethod = depositMethod;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

}