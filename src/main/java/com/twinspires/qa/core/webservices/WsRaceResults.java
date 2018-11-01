package com.twinspires.qa.core.webservices;

import org.json.JSONArray;
import org.json.JSONObject;

public class WsRaceResults extends WebserviceCalls {

    public JSONArray getRaceResults(String track, String race) {
        JSONObject responseBody;
        JSONObject results;
        JSONArray entries;
        String endpoint;
        String affId = affiliate.getAffId();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Tote/Results");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&track=" + track;
        endpoint += "&type=" + "Thoroughbred";
        endpoint += "&race=" + race;
        responseBody = getEndpointJSONResponse(endpoint);

        // Return only the json array of track current race details
        results = responseBody.getJSONObject("Results").getJSONObject("WPS");
        entries = results.getJSONArray("Entries");

        return entries;
    }

    public JSONArray getPoolResults(String track, String race) {
        JSONObject responseBody;
        JSONObject results;
        JSONArray pools;
        String endpoint;
        String affId = affiliate.getAffId();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Tote/Results");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&track=" + track;
        endpoint += "&type=" + "Thoroughbred";
        endpoint += "&race=" + race;
        responseBody = getEndpointJSONResponse(endpoint);

        // Return only the json array of track current race details
        results = responseBody.getJSONObject("Results").getJSONObject("Exotics");
        pools = results.getJSONArray("Pools");

        return pools;
    }

    public JSONArray getInstantChart(String track, String race, String date) {
        JSONObject responseBody;
        JSONObject results;
        JSONArray pools;
        String endpoint;
        String affId = affiliate.getAffId();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Result/InstantChart");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&track=" + track;
        endpoint += "&type=" + "Thoroughbred";
        endpoint += "&date=" + date;
        endpoint += "&race=" + race;
        responseBody = getEndpointJSONResponse(endpoint);

        // Return only the json array of track current race details
        pools = responseBody.getJSONArray("InstantChartRaces");

        return pools;
    }
}