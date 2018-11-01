package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.util.Util;

/**
 * Created by Gallop/Avinash on 20-12-2016.
 */
public abstract class AbstractEzMoneyDepositData implements IMoneyDepositData {
    public AbstractEzMoneyDepositData() {
    }

    public String getAccountNumber() {
        long randomAccountNumber = Util.randomNumberForDigits(5, 17);
        return Long.toString(randomAccountNumber);
    }
}
