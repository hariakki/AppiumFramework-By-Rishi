package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.sqlqueries.ADWQueries;
import com.twinspires.qa.core.sqlqueries.SQLQueries;
import com.twinspires.qa.core.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Reporter;

import java.math.BigDecimal;
import java.util.*;

public class WsWager extends WebserviceCalls {

    public String postPlaceWinWager(String jwtAuthKey, String trackCode, String raceNumber,
                                    String betAmount, String selection) {
        String affId = affiliate.getAffId().toString();
        String url = buildEndpoint("/adw/legacy/wager/bet");
        String confirmationId = "";
        JSONObject requestBody = new JSONObject();
        String reqTrackType;

        reqTrackType = new ADWQueries().getTrackType(trackCode);

        requestBody.put("username", "iphone");
        requestBody.put("password", "ru13juhyo");
        requestBody.put("ip", "172.16.34.124");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("race", raceNumber);
        requestBody.put("track", trackCode);
        requestBody.put("trackType", reqTrackType);
        requestBody.put("amount", betAmount);
        requestBody.put("betType", "WN");
        requestBody.put("runList", selection);
        requestBody.put("authKey", jwtAuthKey);

        sendRequest("POST", "application/x-www-form-urlencoded", url, requestBody);

        try {
            confirmationId = parseToJSONObject(lastResponseBody).getJSONObject("BetResult").getString("ConfirmationID");

        } catch (Exception e) {
            confirmationId = parseToJSONObject(lastResponseBody).getJSONObject("Error").getString("Description");
            Util.printLine("Error: " + confirmationId);
            confirmationId = "";
        }

        return confirmationId;
    }

    /**
     * Gets a list of all wagers made today for the user which the jwt key belongs
     * @param jwtId a valid jwt authentication token relating to a user
     * @return a JSON array of all wagers placed today
     */
    public JSONArray getTodaysBets(String jwtId) {
        String affId = affiliate.getAffId().toString();
        String url = buildEndpoint("/adw/wager/gettodaysbets" +
                "?username=my_tux&ip=0.0.0.0&affid=" + affId + "&affiliateId=" + affId + "&output=json");
        HashMap<String, String> headers = new HashMap<>();
        String results = "";
        headers.put("Authorization", jwtId);

        results = getEndpointStringResponse(url, headers);
        return parseToJSONObject(results).getJSONArray("todaysWagers");
    }

    /**
     * A getTodaysBets filter which allows checking for specific conditions to match and returns a list of
     * matching wagers
     * @param jwtId a valid jwtId for the user
     * @param filterBys hash map of filter conditions.
     *     Filter field key/value examples:
     *         "baseAmount": "2.6",        "betTypeDisplayName": "Win",            "conditionalWager": null,
     *         "conditions": "none",       "datetimeUTC": "2018-07-02T18:57:43Z",  "eventCode": "LTH",
     *         "eventDate": "2018-07-02",  "failedReason": "",                     "frozenWager": "false",
     *         "futureWager": "false",     "leaderBoardWager": "",                 "payoutAmount": "15.6",
     *         "placedDate": "2018-07-02 11:57:43",    "poolType": "WN",           "race": "4",
     *         "refundAmount": "0.0",      "runnersList": "3",                     "serialNumber": "54aa9-99c1e",
     *         "status": "PAID"            "totalCost": "2.6",                     "finishOrder": null,
     *         "brisCode": "lth",          "trackType": "Thoroughbred",            "trackName": "Hong Kong - Late",
     *         "requestId": null
     * @return list of wagers meeting filter conditions
     */
    public List<Map<String, Object>> getTodaysBetsFilter(String jwtId, HashMap<String, String> filterBys) {
        JSONObject bet;
        JSONArray fullList = getTodaysBets(jwtId);
        List<Map<String, Object>> filteredList = new ArrayList<>();
        Iterator iter;
        Map.Entry filter;
        boolean match;
        String filterKey;
        BigDecimal filterNum;
        BigDecimal betNum;

        // search all today's bets ws response list
        for(int i = 0; i < fullList.length(); i++) {
            bet = fullList.getJSONObject(i);
            match = true;

            if (!filterBys.isEmpty()) {
                // compare against each filter
                iter = filterBys.entrySet().iterator();
                while (iter.hasNext()) {
                    filter = (Map.Entry) iter.next();
                    filterNum = null;
                    betNum = null;
                    try{
                        filterKey = filter.getKey().toString();
                        switch (filterKey) {
                            case "baseAmount":
                            case "payoutAmount":
                            case "refundAmount":
                            case "totalCost":
                                filterNum = new BigDecimal(filter.getValue().toString());
                                betNum = new BigDecimal(bet.getString(filter.getKey().toString()));
                                if(betNum.floatValue() != filterNum.floatValue()) {
                                    match = false;
                                }
                                break;
                            default:
                                if (!bet.getString(filter.getKey().toString())
                                        .equalsIgnoreCase(filter.getValue().toString())) {
                                    // No match, stop processing filters for this record
                                    match = false;
                                    break;
                                }
                        }
                    } catch (Exception e) { /* couldn't find the key in the response, continue */ }
                }
            }

            // match was found
            if(match) {
                filteredList.add(bet.toMap());
            }
        }

        return filteredList;
    }

    /**
     * Cancels all active wagers for the provided user/user's jwtId
     * @param jwtId JWT authentication key. can be acquired via postJwtAuthKey(username, password)
     * @return summary of cancelations and status
     */
    public String postCancelWagersAll(String jwtId) {
        HashMap<String, String> filters = new HashMap<>();
        List<Map<String, Object>> placedWagers;
        String messageDesc = "Cancel All User's Wagers: " + "\n";
        String bdsCode = "";
        String wagerAmount = "";

        // Finds desired wager's transaction ID
        filters.put("status", "PLACED");
        placedWagers = getTodaysBetsFilter(jwtId, filters);
        if(placedWagers.size() == 0) {
            return messageDesc + "No cancelable wagers exist for the user";
        }

        // Attempts to cancel all "PLACED" wagers and builds a report
        for(int i = 0; i < placedWagers.size(); i++) {
            bdsCode = placedWagers.get(i).get("brisCode").toString();
            wagerAmount = placedWagers.get(i).get("totalCost").toString();
            messageDesc += " [" + bdsCode.toUpperCase() + " | $" + wagerAmount + "] "
                    + postCancelWager(jwtId, placedWagers.get(i).get("serialNumber").toString(), bdsCode, wagerAmount)
                    + " \n";
        }

        return messageDesc;
    }

    /**
     * Cancels the latest wager for the user matching the track code and total wager amount
     * @param jwtId JWT authentication key. can be acquired via postJwtAuthKey(username, password)
     * @param trackCode the track code which the wager to cancel was placed against
     * @param totalWagerAmount the total amount of the wager
     * @return The transaction number of the latest matching wager for the jwtId's user
     */
    public String postCancelWager(String jwtId, String trackCode, String totalWagerAmount) {
        HashMap<String, String> filters = new HashMap<>();
        List<Map<String, Object>> placedWagers;
        String transactionId = "";

        // Finds desired wager's transaction ID (most recent match, if multiple)
        filters.put("status", "PLACED");
        filters.put("brisCode", trackCode);
        filters.put("totalCost", totalWagerAmount.replace("$", "").trim());
        placedWagers = getTodaysBetsFilter(jwtId, filters);
        if(placedWagers.size() == 0) {
            Reporter.log("Wager not canceled:  Unable to find matching cancelable wager for the user ["
                    + trackCode + "] [$" + totalWagerAmount + "]");
            return "Wager not canceled";
        }
        transactionId = placedWagers.get(0).get("serialNumber").toString();

        return postCancelWager(jwtId, transactionId, trackCode, totalWagerAmount);
    }

    /**
     * Cancels the specified wager for the jwtId's user
     * @param jwtId JWT authentication key. can be acquired via postJwtAuthKey(username, password)
     * @param transactionId the wager's transaction id, unique to each wager
     * @param trackCode the track code which the wager to cancel was placed against
     * @param totalWagerAmount the total amount of the wager
     * @return The transaction number of the latest matching wager for the jwtId's user
     */
    public String postCancelWager(String jwtId, String transactionId, String trackCode, String totalWagerAmount) {
        String messageDesc = "Canceling wager [" + transactionId + "]: ";
        String affId = affiliate.getAffId().toString();
        String url = buildEndpoint("/webapi/Rtb/CancelWager");
        String response = "";
        String cancelStatus = "";
        String reqTrackType;
        JSONObject responseBody;
        JSONObject requestBody = new JSONObject();

        try {
            reqTrackType = new ADWQueries().getTrackType(trackCode);
            switch (reqTrackType.trim().toLowerCase()) {
                case "thoroughbred":
                    reqTrackType = "1";
                    break;
                case "harness":
                    reqTrackType = "2";
                    break;
                case "greyhound":
                    reqTrackType = "4";
                    break;
                default: // Will fail but will flag that something is wrong
                    reqTrackType = "0";
            }
        } catch (Exception e) {
            return messageDesc + "Error: Unable to determine track type";
        }

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("authKey", jwtId);
        requestBody.put("brisCode", trackCode);
        requestBody.put("trackType", reqTrackType);
        requestBody.put("serialNumber", transactionId);
        requestBody.put("wagerAmount", totalWagerAmount.replace("$", "").trim());
        requestBody.put("status", "Accepted");

        try {
            response = sendRequest("POST", "application/x-www-form-urlencoded", url, requestBody);
            responseBody = parseToJSONObject(response);
            cancelStatus = responseBody.optString("message", "");
            if (cancelStatus.equalsIgnoreCase("failed")) {
                cancelStatus = "Failed: " + responseBody.optString("errors", "Unknown error");
            } else if (cancelStatus.equalsIgnoreCase("success")) {
                cancelStatus = "Success";
            }
        } catch (Exception e) {
            cancelStatus = "Error: Unable to determine status of WS response";
        }
        return messageDesc + cancelStatus;
    }

    /**
     * Returns a list of wagerable races for the specified track
     * @param trackCode the track code containing the race
     * @param raceNum the race to acquire available wagers
     * @return HashMap<WagerType, BaseWagerAmount>
     */
    public HashMap<String, String> getWagerTypesForRace(String trackCode, String raceNum, String trackType){
        SQLQueries queries = new SQLQueries();
        HashMap<String, String> availableWagers = new HashMap<String, String>();
        HashMap<String, String> wagerLookup;
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody = null;
        JSONArray betTypesArray = null;
        JSONObject betTypeObj = null;
        String endpoint;
        String betCode;
        String baseAmount;
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Wager/BetList");
        requestBody.put("username", "iphone");
        requestBody.put("password", "ru13juhyo");
        requestBody.put("ip", "10.20.2.248");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("track", trackCode);
        requestBody.put("trackType", trackType);
        requestBody.put("race", raceNum);
        responseBody = postEndpointJSONResponse(endpoint, requestBody);

        wagerLookup = new ADWQueries().getWagerTypeLookup();

        // Parses JSON results to acquire the race's available wager types and minimum bets.
        betTypesArray = responseBody.getJSONArray("BetTypes");
        for(int b = 0; b < betTypesArray.length(); b++) {
            betTypeObj = betTypesArray.getJSONObject(b);
            betCode = betTypeObj.getString("BetCode").trim();
            baseAmount = betTypeObj.getString("BaseAmount").trim();
            availableWagers.put(wagerLookup.get(betCode), baseAmount);
        }

        return availableWagers;
    }

    /**
     * Returns a list of wagerable races for the specified track
     * @param trackCode the track code of the track which to acquire races
     * @return List of the race numbers
     */
    public static List<String> getWagerableRacesForTrack(String trackCode){
        List<String> wagerableRaces = new ArrayList<>();
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody = null;
        JSONObject temporaryJsonObj = null;
        JSONArray temporaryJsonArr = null;
        String endpoint;

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Wager/BetList");
        requestBody.put("username", "iphone");
        requestBody.put("password", "ru13juhyo");
        requestBody.put("ip", "10.20.2.248");
        requestBody.put("affid", "2800");
        requestBody.put("affiliateId", "2800");
        requestBody.put("output", "json");
        requestBody.put("track", trackCode);
        requestBody.put("trackType", "Thoroughbred");
        responseBody = postEndpointJSONResponse(endpoint, requestBody);

        // Parse JSON results to acquire a wagerable races list.
        temporaryJsonObj = responseBody.getJSONObject("BetTypes");
        temporaryJsonArr = temporaryJsonObj.getJSONArray("Races");
        for(int r = 0; r < temporaryJsonArr.length(); r++) {
            wagerableRaces.add(temporaryJsonArr.getJSONObject(r).getString("Race"));
        }
        return wagerableRaces;
    }

    /**
     * Compiles a list of all tracks with associated video feeds
     * @param returnData data value to return.  If an empty string, defaults to "DisplayName"
     *          Available values:  [TrackType], [EventCode], [DisplayName], [DomesticTrack], [Program], [Tote], [Wager]
     * @return Hashmap<Key, returnData> where Key is the BrisCode
     */
    public HashMap<String, String> getTracksWithVideo(String returnData) {
        HashMap<String, String> tracksWithVideo = new HashMap<String, String>();
        JSONObject responseBody = null;
        JSONArray tracksArray = null;
        JSONObject trackObj = null;
        JSONObject trackVideoObj = null;
        String endpoint;
        String betCode;
        String baseAmount;
        String affId = affiliate.getAffId().toString();

        if(returnData.isEmpty()){
            returnData = "DisplayName";
        }

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Cdi/TrackList");
        endpoint += "?username=" + "iphone";
        endpoint += "&password=" + "ru13juhyo";
        endpoint += "&ip=" + "10.20.2.248";
        endpoint += "&affid=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&vidType=" + "IPHONE";
        endpoint += "&multisource=" + "1";
        responseBody = getEndpointJSONResponse(endpoint);

        tracksArray = responseBody.getJSONArray("Tracks");
        for(int i = 0; i < tracksArray.length(); i++) {
            trackObj = tracksArray.getJSONObject(i);
            trackVideoObj = trackObj.optJSONObject("Video");
            if(trackVideoObj != null) {
                tracksWithVideo.put(trackObj.getString("BrisCode"), trackObj.getString(returnData));
            }
        }

        return tracksWithVideo;
    }
}