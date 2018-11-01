package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.enums.ClickEventType;

import java.util.HashMap;

/**
 * Created by kasey.sparkman on 6/01/18.
 *
 * Organizes click event database records
 */
public class ClickEvent {

    private String Assert_Description;
    private Integer Click_Event_IDs;
    private ClickEventType Click_Type; // Click_Type_ID
    private String Customer_ID;
    private String Method_Name;
    private String Session_ID;
    private String Time_Stamp;


    public ClickEvent() {
        Click_Event_IDs = null;
        Time_Stamp = null;
        Click_Type = null;
        Customer_ID = null;
        Session_ID = null;
        Assert_Description = null;
        Method_Name = null;
    }

    public ClickEvent(HashMap<String, String> map) {
        Click_Event_IDs = Integer.getInteger(map.get("Click_Event_ID"));
        setClickTypeID(map.get("Click_Type"));
        Assert_Description = map.get("Assert_Description");
        Customer_ID = map.get("Customer_ID");
        Method_Name = map.get("Method_Name");
        Session_ID = map.get("Session_ID");
        Time_Stamp = map.get("Time_Stamp");
    }

    /* GETTER METHODS */
    public String getAssertDescription() {
        return Assert_Description;
    }

    public Integer getClickEventIDs() {
        return Click_Event_IDs;
    }

    public ClickEventType getClickType() {
        return Click_Type;
    }

    public Integer getClickTypeID() {
        return Click_Type.getId();
    }

    public String getCustomerID() {
        return Customer_ID;
    }

    public String getMethodName() {
        return Method_Name;
    }

    public String getSessionID() {
        return Session_ID;
    }

    public String getTimeStamp() {
        return Time_Stamp;
    }

    /* SETTER METHODS */
    public ClickEvent setAssertDescription(String assertDescription) {
        Assert_Description = assertDescription;
        return this;
    }

    public ClickEvent setClickEventIDs(Integer clickEventIDs) {
        Click_Event_IDs = clickEventIDs;
        return this;
    }

    public ClickEvent setClickTypeID(ClickEventType cet) {
        Click_Type = cet;
        return this;
    }

    public ClickEvent setClickTypeID(Integer clickTypeID) {
        Click_Type = ClickEventType.getById(clickTypeID);
        return this;
    }

    public ClickEvent setClickTypeID(String clickTypeID) {
        Click_Type = ClickEventType.getById(clickTypeID);
        return this;
    }

    public ClickEvent setCustomerID(String customerID) {
        Customer_ID = customerID;
        return this;
    }

    public ClickEvent setMethodName(String methodName) {
        Method_Name = methodName;
        return this;
    }

    public ClickEvent setSessionID(String sessionID) {
        Session_ID = sessionID;
        return this;
    }

    public ClickEvent setTimeStamp(String timeStamp) {
        Time_Stamp = timeStamp;
        return this;
    }
}
