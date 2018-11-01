package com.twinspires.qa.core.testobjects;

/**
 * Created by dalwinder.singh on 8/16/18.
 */
public class DatastoreWagerInfo {

    String brisCode;
    int raceNumber;
    String wagerType;
    String gateway;
    String totalWagerAmount;
    String raceDate;

    public String getBrisCode() {
        return brisCode;
    }

    public void setBrisCode(String brisCode) {
        this.brisCode = brisCode;
    }

    public int getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(int raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getWagerType() {
        return wagerType;
    }

    public void setWagerType(String wagerType) {
        this.wagerType = wagerType;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getTotalWagerAmount() {
        return totalWagerAmount;
    }

    public void setTotalWagerAmount(String totalWagerAmount) {
        this.totalWagerAmount = totalWagerAmount;
    }

    public String getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(String raceDate) {
        this.raceDate = raceDate;
    }


}
