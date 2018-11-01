package com.twinspires.qa.core.testdata;

import java.math.BigDecimal;

/**
 * Created by Gallop/Avinash on 20-12-2016.
 */
public interface IMoneyDepositData {
    public BigDecimal getDepositAmount();
    public String getAccountType();
    public String getRoutingNumber();
    public String getAccountNumber();
}
