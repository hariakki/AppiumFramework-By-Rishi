package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.util.Util;

/**
 * Created by thomas.wallace on 10/24/2016.
 */
public abstract class AbstractBonusData implements IBonusData {

    private String bonus;
    private String fixedAmt;
    private String fixedValue;
    private String maxAssignmentPlayer;
    private String maxAssignment;
    private String percentage;
    private String minDeposit;
    private String wagerAmt;

    public AbstractBonusData() {
        bonus = new Util().randomUsername().toString();
        percentage = String.valueOf(Util.random100());
        minDeposit = String.valueOf(Util.random100());
        fixedAmt = String.valueOf(Util.random100());
        wagerAmt = String.valueOf(Util.randomWager5to15());
        fixedValue = String.valueOf(Util.random100());
        maxAssignmentPlayer = String.valueOf(Util.random100());
        maxAssignment = String.valueOf(Util.random100());
    }

    public String getBonus() {
        return bonus;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getMinDeposit() {
        return minDeposit;
    }

    public String getFixedAmt() {
        return fixedAmt;
    }

    public String getWagerAmt() {
        return  wagerAmt;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public String getMaxAssignmentPlayer() {
        return maxAssignmentPlayer;
    }

    public String getMaxAssignment() {
        return maxAssignment;
    }
}
