package com.twinspires.qa.core.enums;

/**
 * Created by chad.justice on 9/15/2016.
 */
public enum TestEnv{

    DEV("DEV"),
    ITE("ITE"),
    STE("STE"),
    PROD("PROD"),
    LOAD("LOAD");

    private String text;

    TestEnv(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static TestEnv fromString(String text) {
        if (text != null) {
            for (TestEnv b : TestEnv.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

}
