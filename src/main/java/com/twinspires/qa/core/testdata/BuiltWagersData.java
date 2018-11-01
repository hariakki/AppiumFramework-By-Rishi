package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.util.Util;
import com.twinspires.qa.core.webservices.WsWager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates lists of wagers and performs cancellations in the @AfterMethod of a test.
 */
public class BuiltWagersData {
    
    /**
     * Sub-class containing all necessary data for the cancellation of a wager.
     */
    public class BuiltWager {
        protected String username;
        protected String password;
        protected String jwtAuthKey;
        protected String affid;
        protected String race;
        protected String trackCode;
        protected String trackType;
        protected String amount;
        protected String betType;
        protected String selection;
        protected String confirmation;
        protected String account;
    
        public BuiltWager() {
            username = "";      // Req to acquire jwtAuthKey
            password = "";      // Req to acquire jwtAuthKey
            jwtAuthKey = "";    // Req for cancel
            affid = "";
            race = "";
            trackCode = "";     // Req for cancel
            trackType = "";     // Req for cancel
            amount = "";        // Req for cancel
            betType = "";
            selection = "";
            confirmation = "";  // Req for cancel
            account = "";
        }
    
        private void construct(JSONObject requestBody, String betConfirmation,
                               String jwtAuthKey, // optional
                               String username, String password) { // optional pair
            this.username = username;
            this.password = password;
            this.jwtAuthKey = jwtAuthKey;
            affid = requestBody.optString("affid");
            race = requestBody.optString("race");
            trackCode = requestBody.optString("track");
            trackType = requestBody.optString("trackType");
            amount = requestBody.optString("amount");
            betType = requestBody.optString("betType");
            selection = requestBody.optString("runList");
            confirmation = betConfirmation;
        }
    
        public void construct(JSONObject requestBody, String betConfirmation) {
            construct(requestBody, betConfirmation, requestBody.optString("authKey"), "", "");
        }
    
        public void construct(JSONObject requestBody, String betConfirmation, String jwtAuthKey) {
            construct(requestBody, betConfirmation, jwtAuthKey, "", "");
        }
    
        public void construct(JSONObject requestBody, String betConfirmation, String username, String password) {
            construct(requestBody, betConfirmation, requestBody.optString("authKey"), username, password);
        }

        public String getDetails(){
            String NL = System.lineSeparator();
            String tab = "    ";
            String message = "{" + NL;

            message += tab + "username:  " + username   + NL;
            message += tab + "affid:     " + affid      + NL;
            message += tab + "trackCode: " + trackCode  + NL;
            message += tab + "trackType: " + trackType  + NL;
            message += tab + "race:      " + race       + NL;
            message += tab + "betType:   " + betType    + NL;
            message += tab + "amount:    " + amount     + NL;
            message += tab + "selection: " + selection  + NL;

            message += "}";
            return message;
        }

        public void setAffid(String affid) {
            this.affid = affid;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setBetType(String betType) {
            this.betType = betType;
        }

        public void setConfirmation(String confirmation) {
            this.confirmation = confirmation;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setRace(String race) {
            this.race = race;
        }

        public void setSelection(String selection) {
            this.selection = selection;
        }

        public void setTrackCode(String trackCode) {
            this.trackCode = trackCode;
        }

        public void setTrackType(String trackType) {
            this.trackType = trackType;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setAccount(String account) {
            this.account = account;
        }
        
        public void setCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    List<BuiltWager> builtWagersList;
    WsWager ws;

    public BuiltWagersData (WsWager wsCalls)
    {
        builtWagersList = new ArrayList<>();
        ws = wsCalls;
    }
    
    /**
     * Sets a new wager to automatically cancel at end of test case by acquiring data from the last placed WS call.
     *
     * @param betConfirmation the code confirming the wager submission
     * @return the newly created BuiltWager so that additional values may be set if necessary
     */
    public BuiltWager newWSPlacedWager(String betConfirmation) {
        BuiltWager newWager = new BuiltWager();
    
        // If bet was confirmed
        if(betConfirmation.trim().length() == 11
                && betConfirmation.contains("-")) {
            newWager.construct(ws.getLastRequestBody(), betConfirmation);
            
            builtWagersList.add(newWager);
        }
        return newWager;
    }
    
    /**
     * Sets a new wager to automatically cancel at end of test case
     * If the bet confirmation code is not valid, does not set to the list
     * <p>
     * Not enough data is provided to make a successful cancellation with this call alone
     *
     * @param betConfirmation the code confirming the wager submission
     * @return the newly created BuiltWager so that additional values may be set if necessary
     */
    public BuiltWager newPlacedWager(String betConfirmation) {
        BuiltWager newWager = new BuiltWager();
    
        // If bet was confirmed
        if(betConfirmation.trim().length() == 11
                && betConfirmation.contains("-")) {
            newWager.setConfirmation(betConfirmation);
            builtWagersList.add(newWager);
        }
        return newWager;
    }
    
    /**
     * Sets a new wager to automatically cancel at end of test case
     * If the bet confirmation code is not valid, does not set to the list
     * <p>
     * Not enough data is provided to make a successful cancellation with this call alone
     *
     * @param requestBody JSONObject request body that contains most of the necessary cancel values
     * @param betConfirmation the code confirming the wager submission
     * @return the newly created BuiltWager so that additional values may be set if necessary
     */
    public BuiltWager newPlacedWager(JSONObject requestBody, String betConfirmation) {
        BuiltWager newWager = new BuiltWager();
        
        // If bet was confirmed
        if(betConfirmation.trim().length() == 11
                && betConfirmation.contains("-")) {
            newWager.construct(requestBody, betConfirmation);
            builtWagersList.add(newWager);
        }
        return newWager;
    }
    
    /**
     * Sets a new wager to automatically cancel at end of test case
     * If the bet confirmation code is not valid, does not set to the list
     * @param requestBody JSONObject request body that contains most of the necessary cancel values
     * @param betConfirmation the code confirming the wager submission
     * @param jwtAuthKey in lieu of username/password, provides association to the account that placed the wager
     * @return the newly created BuiltWager so that additional values may be set if necessary
     */
    public BuiltWager newPlacedWager(JSONObject requestBody, String betConfirmation, String jwtAuthKey) {
        BuiltWager newWager = new BuiltWager();
        
        // If bet was confirmed
        if(betConfirmation.trim().length() == 11
                && betConfirmation.contains("-")) {
            newWager.construct(requestBody, betConfirmation, jwtAuthKey);
            builtWagersList.add(newWager);
        }
        return newWager;
    }
    
    /**
     * Sets a new wager to automatically cancel at end of test case
     * If the bet confirmation code is not valid, does not set to the list
     * @param requestBody JSONObject request body that contains most of the necessary cancel argument values
     * @param betConfirmation code confirming the wager submission
     * @param username of the account used to place the wager.  Necessary to acquire jwtAuthKey call
     * @param password of the account used to place the wager.  Necessary to acquire jwtAuthKey call
     * @return the newly created BuiltWager so that additional values may be set if necessary
     */
    public BuiltWager newPlacedWager(JSONObject requestBody, String betConfirmation, String username, String password) {
        BuiltWager newWager = new BuiltWager();
        
        // If bet was confirmed
        if(betConfirmation.trim().length() == 11
                && betConfirmation.contains("-")) {
            newWager.construct(requestBody, betConfirmation, username, password);
            builtWagersList.add(newWager);
        }
        return newWager;
    }
    
    /**
     * Returns reference to the BuiltWager at the specified index, allowing additional data modification
     * @param i the index in the list of wagers to return
     * @return the BuiltWager object, null if list is empty
     */
    public BuiltWager getWagerByIndex(int i) {
        if(i < builtWagersList.size()
                && i >= 0) {
            return builtWagersList.get(i);
        }
        return null;
    }
    
    /**
     * Returns reference to the last BuiltWager, allowing additional data modification
     * @return the BuiltWager object, null if list is empty
     */
    public BuiltWager getLastWager() {
        if(builtWagersList.size() > 0) {
            return builtWagersList.get(builtWagersList.size()-1);
        }
        return null;
    }
    
    /**
     * The size of the builtWagersList
     * @return the number of BuiltWagers scheduled to be cancelled
     */
    public int size() {
        return builtWagersList.size();
    }
    
    /**
     * Attempt to cancel all wagers in the list
     */
    public void cancelWagers(){
        BuiltWager curWager;
        String lastJwtKey = "";
        String lastUN = "";
        String result = "";
        
        // if WebserviceCalls is not set, do not attempt to cancel wagers
        if(ws == null)
            return;
        
        // For each registered wager
        for(int i = 0; i < builtWagersList.size(); i++) {
            curWager = builtWagersList.get(i);
            // Acquire a new lastJwtKey if the last one used matches the current user
            if( ( lastUN.isEmpty() || !lastUN.equalsIgnoreCase(curWager.username) )
                    || lastJwtKey.isEmpty()) {
                
                // Attempt to use the curWager's previously set jwtAuthKey
                if(!curWager.jwtAuthKey.isEmpty()) {
                    lastJwtKey = curWager.jwtAuthKey;
                    
                // jwtAuthKey was not registered, attempt to acquire one
                } else {
                    try {
                        lastUN = curWager.username;
                        lastJwtKey = ws.postJwtAuthKey(lastUN, curWager.password);
                    } catch (Exception e) {
                        Util.printLine("Cancel Wager: Failed acquiring JWT Auth Key: ");
                        Util.printLine("Due to: " + e.getMessage() + " for data:");
                        Util.printLine(curWager.getDetails());
                        continue; // skip to next wager in list
                    }
                }
            } // Uses last jwtAuthKey
            
            try {
                result = ws.postCancelWager(lastJwtKey,
                        curWager.confirmation,
                        curWager.trackCode,
                        curWager.amount);
            } catch (Exception e) {
                Util.printLine("Cancel Wager: Failed cancelling Wager: ");
                Util.printLine("Due to: " + e.getMessage() + " for data:");
                Util.printLine(curWager.getDetails());
            }
            
//            if(result.equalsIgnoreCase("success")) {
                Util.printLine(result);
//            }
        }
        // Always remove the all existing wagers from the list
        builtWagersList = new ArrayList<>();
    }
}