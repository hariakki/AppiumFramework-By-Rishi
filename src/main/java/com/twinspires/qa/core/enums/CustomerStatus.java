package com.twinspires.qa.core.enums;

/**
 * Created by chad.justice on 9/28/2016.
 */
public enum CustomerStatus {

    TRUSTED("Trusted"),
    PROBATION("Probation"),
    PAYUPFRONT("PayUpFront");

    private String text;

    CustomerStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static CustomerStatus fromString(String text) {
        if (text != null) {
            for (CustomerStatus b : CustomerStatus.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

}
