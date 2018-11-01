package com.twinspires.qa.core.enums;

/**
 * Created by chad.justice on 9/28/2016.
 */
public enum Affiliate {

    TWINSPIRES("TS"),
    KEENELANDSELECT("KS"),
    OAKLAWNANYWHERE("OK"),
    MYRACEBET("MB"),
    CAPITALOTBBET("CB"),
    RACELINEBET("RB"),
    BETHARNESS("BH"),
    TWINSPIRESTOURNAMENTS("BC"); // Betting Challenge

    private String text;

    Affiliate(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Affiliate fromString(String text) {
        if (text != null) {
            for (Affiliate b : Affiliate.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

    public String getAffId (){
        String affId = "";
        switch (this.text){
            case "TS":
                return "2800";
            case "KS":
                return "4100";
            case "OK":
                return "1400";
            case "MB":
                return "1500";
            case "CB":
                return "4200";
            case "RB":
                return "2200";
            case "BH":
                return "1300";
            case "BC":
                return "4400";
            default:
                break;
        }
        return affId;
    }

    public String getChannelId (){
        String affId = "";
        switch (this.text){
            case "BC":
            case "TS":
                return "2";
            case "KS":
                return "9";
            case "OK":
                return "";
            case "MB":
                return "";
            case "CB":
                return "";
            case "RB":
                return "";
            case "BH":
                return "";
            default:
                break;
        }
        return affId;
    }
}