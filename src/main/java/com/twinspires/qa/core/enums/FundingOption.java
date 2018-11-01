package com.twinspires.qa.core.enums;

public enum FundingOption {
    
    EZMONEY("EzMoney"),
    EZBANK("EzBank"),
    CREDITCARDONLINE("CreditCardOnline"),
    CREDITCARDBYPHONE("CreditCardByPhone"),
    PAYPAL("Paypal"),
    MONEYGRAM("MoneyGram"),
    GREENDOT("GreenDot"),
    PAYNEARME("PayNearMe");

    private String text;

    FundingOption(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static FundingOption fromString(String text) {
        if (text != null) {
            for (FundingOption b : FundingOption.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

}
