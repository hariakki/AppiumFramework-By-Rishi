package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import org.json.JSONObject;

public class WsBetShare extends WebserviceCalls {

    private String testEnv;
    private String betShareEndpoint;
    private String resource;
    private String parameters;
    TestDataQueries testDataQueries = new TestDataQueries();

    public WsBetShare(TestEnv testEnv) {
        this.testEnv = testEnv.toString();
    }

    public String createBetShare(String trackName, String date, String betShareName, int shares) {
        String affId = affiliate.getAffId();
        betShareEndpoint = testDataQueries.getServiceEndpoint(testEnv, "betshare");
        resource = "betshare";
        parameters = "?username=mobile&affiliateId=" + affId + "&affid=" + affId;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("raceDate", date);
        jsonObject.put("trackName", trackName);
        jsonObject.put("race", "1");
        jsonObject.put("betAmount", 2.0);
        jsonObject.put("betCost", 2.0);
        jsonObject.put("shares", shares);
        jsonObject.put("username", "betshare");
        jsonObject.put("betType", "WN");
        jsonObject.put("runners", "4");
        jsonObject.put("betShareName", betShareName);

        JSONObject response = WebserviceCalls.postEndpointJSONResponse(betShareEndpoint + resource + parameters, jsonObject);

        return response.get("betShareId").toString();
    }

    //This method is used for a captain or non captain to join a betshare. If a captain joins the same betshare
    //it will reserve the shares.
    public void joinBetShare(String betshareId, int shares) {
        String affId = affiliate.getAffId();
        betShareEndpoint = testDataQueries.getServiceEndpoint(testEnv, "betshare");
        resource = "betshare/" + betshareId;
        parameters = "?username=mobile&affiliateId=" + affId + "&affid=" + affId;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("shares", shares);

        JSONObject response = WebserviceCalls.postEndpointJSONResponse(betShareEndpoint + resource + parameters, jsonObject);
        System.out.println(response);

    }
}
