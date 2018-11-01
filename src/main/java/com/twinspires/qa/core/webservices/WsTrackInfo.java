package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.sqlqueries.ADWQueries;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WsTrackInfo extends WebserviceCalls {

    /**
     * Provides a list of all the scratches for the given track
     *
     * @param trackCode the track code which to check
     * @return scratches
     */
    public ArrayList<String> getScratchesForTrack(String trackCode) {
        String endpoint;
        String trackType = new ADWQueries().getTrackType(trackCode);
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody = null;
        JSONArray integratedScratches = null;
        JSONObject race = null;
        JSONArray entryChanges = null;
        JSONObject horse = null;
        ArrayList<String> scratches = new ArrayList<String>();
        String affId = affiliate.getAffId();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Cdi/IntegratedScratch");
        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("track", trackCode);
        requestBody.put("type", trackType);
        responseBody = postEndpointJSONResponse(endpoint, requestBody);

        // Parse JSON results to get scratches for all races
        integratedScratches = responseBody.getJSONArray("IntegratedScratches");
        for(int i=0; i<integratedScratches.length(); i++) {
            race = integratedScratches.getJSONObject(i);
            entryChanges = race.getJSONArray("EntryChanges");

            //Loops through each horse in the race to see if its scratched or not
            for(int j=0; j<entryChanges.length(); j++) {
                horse = entryChanges.getJSONObject(j);

                if("Scratch".equals(horse.get("ChangeType"))) {
                    scratches.add(horse.getString("ProgramNumber"));
                }
            }
        }

        return scratches;
    }

    /**
     * Provides a list of all the available entries for the track and which/if the entry was changed
     * @param trackCode the track code which to check
     * @return JSON Array of the races and their changes
     */
    public JSONArray getEntriesAndChangesForTrack(String trackCode) {
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody = null;
        JSONArray trackScratches = null;
        String trackType = new ADWQueries().getTrackType(trackCode);
        String endpoint;
        String affId = affiliate.getAffId();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Cdi/IntegratedScratch");
        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("track", trackCode);
        requestBody.put("type", trackType);
        responseBody = postEndpointJSONResponse(endpoint, requestBody);

        trackScratches = responseBody.getJSONArray("IntegratedScratches");
        return trackScratches;
    }

    public JSONArray getTodaysRacesInfo(String state, boolean includeGreyhound) {
        JSONObject responseBody;
        JSONArray tracksArray;
        String endpoint;
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/adw/todays-tracks");
        endpoint += "?affid=" + affId;
        endpoint += "&includeGreyhound=" + includeGreyhound;
        endpoint += "&state=" + state;
        responseBody = getEndpointJSONResponse(endpoint);

        tracksArray = responseBody.getJSONArray("Races");

        return tracksArray;
    }
}