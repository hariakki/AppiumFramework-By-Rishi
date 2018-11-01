package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.util.Util;
import org.json.JSONObject;

public class WsRaceStats extends WebserviceCalls {

    public JSONObject getRaceStats(String trackCode, String race){
        JSONObject responseBody;
        String endpoint;
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Brisservices/gettrackstats");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&trackId=" + trackCode;
        endpoint += "&raceNumber=" + race;
        endpoint += "&raceDate=" + Util.getTodaysDate();
        endpoint += "&countryId=" + "USA";
        endpoint += "&dayEvenning=" + "D";
        responseBody = getEndpointJSONResponse(endpoint);

        return responseBody;
    }
}