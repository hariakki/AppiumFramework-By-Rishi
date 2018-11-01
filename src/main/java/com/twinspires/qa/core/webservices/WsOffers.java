package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

public class WsOffers extends WebserviceCalls {

    TestDataQueries testDataQueries = new TestDataQueries();
    public String bonusId;
    public String bonusName;
    public String depositAmount;
    public String resource;
    public String parameters;
    private String testEnv;
    private String adapterEndpoint;
    private String adwEndpoint;

    public WsOffers(TestEnv testEnv) {
        this.testEnv = testEnv.toString();
    }

    public ArrayList<String> getServiceAvailableOfferNames(String affiliateId) {
        adwEndpoint = testDataQueries.getServiceEndpoint(testEnv,"twinspires");

        resource = "adw/offers/getOffers";
        parameters = "?username=my_classic&password=StGKwasb&ip=216.26.183.3&affid="+affiliateId+"&affiliateId="+affiliateId+"&output=json";

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(adwEndpoint + resource + parameters);

        ArrayList<String> offerNames = new ArrayList<>();
        try{
            JSONArray bonuses = response.getJSONArray("bonuses");

            for (Object o : bonuses) {
                offerNames.add(((JSONObject) o).get("shortDescription").toString());
            }
        } catch (JSONException e){
            //No Report needed. This will happen if bonuses are not present
        }

        try {
            JSONArray promos = response.getJSONArray("promos");

            for (Object o : promos) {
                offerNames.add(((JSONObject) o).get("displayVipCampaign").toString());
            }
        }catch (JSONException e){
            //No Report needed. This will happen if promos are not present
        }
        Collections.sort(offerNames);
        return offerNames;
    }

    public JSONObject getOffersHistory(String account, String affiliateId) {
        adwEndpoint = testDataQueries.getServiceEndpoint(testEnv,"twinspires");

        resource = "adw/offers/completed";
        parameters = "?username=my_classic&password=StGKwasb&ip=216.26.183.3&affid="+affiliateId+"&affiliateId="+affiliateId+"&output=json&account="+account;

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(adwEndpoint + resource + parameters);
        JSONObject offers = response.getJSONObject("offers");

        return offers;
    }

    public ArrayList<String> getServiceNewAvailOfferNames(String affiliateId) {
        adwEndpoint = testDataQueries.getServiceEndpoint(testEnv,"twinspires");

        resource = "adw/offers/getOffers";
        parameters = "?username=my_classic&password=StGKwasb&ip=216.26.183.3&affid="+affiliateId+"&affiliateId="+affiliateId+"&output=json";

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(adwEndpoint + resource + parameters);

        ArrayList<String> newOffersNames = new ArrayList<>();
        try {
            JSONArray bonuses = response.getJSONArray("bonuses");

            for (Object o : bonuses) {
                if(((JSONObject) o).get("displayNew").toString().equals("true")) {
                    newOffersNames.add(((JSONObject) o).get("shortDescription").toString());
                }
            }
        } catch (JSONException e){
            //intentionally blank
        }

        try {
            JSONArray bonuses = response.getJSONArray("activeBonuses");

            for (Object o : bonuses) {
                if(((JSONObject) o).get("displayNew").toString().equals("true")) {
                    newOffersNames.add(((JSONObject) o).get("shortDescription").toString());
                }
            }
        } catch (JSONException e){
            //intentionally blank
        }

        try {
            JSONArray promos = response.getJSONArray("promos");

            for (Object o : promos) {
                if(((JSONObject) o).get("displayNew").toString().equals("true")) {
                    newOffersNames.add(((JSONObject) o).get("displayVipCampaign").toString());
                }
            }
        } catch (JSONException e){
            //intentionally blank
        }
        System.out.println("**Response: all 'new' offers: " + newOffersNames);
        return newOffersNames;
    }
}