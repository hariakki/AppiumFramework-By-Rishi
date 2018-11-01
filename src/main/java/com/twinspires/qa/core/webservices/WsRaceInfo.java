package com.twinspires.qa.core.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.twinspires.qa.core.sqlqueries.ADWQueries;

public class WsRaceInfo extends WebserviceCalls {

    /**
     *
     * @param trackCode (BrisCode NOT BDS Code)
     * @param raceNumber
     * @return HashMap<String, String> raceConditions fir the given track and race
     */
    public HashMap<String, String> getRaceConditions(String trackCode, String raceNumber, String trackType) {
        URL url = null;
        String affId = affiliate.getAffId().toString();
        String endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Tote/getAllRaces"
                + "?username=iphone&password=ru13juhyo&ip=10.20.2.248&affid=" + affId + "&affiliateId=" + affId
                + "&output=json&track=" + trackCode + "&type=" + trackType + "&racenum=" + raceNumber);
        HttpURLConnection httpConnection = null;
        String response = "";
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        HashMap<String, String> raceConditions = new HashMap<String, String>();

        try {
            url = new URL(endpoint);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            bufferedReader = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            while ((response = bufferedReader.readLine()) != null) {
                builder.append(response);
            }

            JSONObject responseObject = new JSONObject(builder.toString());
            JSONArray allRaces = responseObject.getJSONArray("AllRaces");
            JSONObject raceInfo = allRaces.getJSONObject(0);

            raceConditions.put("ageRestriction", raceInfo.getString("AgeRestriction"));
            raceConditions.put("sexRestriction", raceInfo.getString("SexRestriction"));
            raceConditions.put("distance", raceInfo.getInt("Distance") + "");
            raceConditions.put("distanceUnit", raceInfo.getString("DistanceUnit"));
            raceConditions.put("surface", raceInfo.getString("Surface"));
            raceConditions.put("maxClaimPrice", raceInfo.getInt("MaxClaimPrice") + "");
            raceConditions.put("raceType", raceInfo.getString("RaceType"));
            raceConditions.put("raceConditions", raceInfo.getString("RaceConditions"));
            raceConditions.put("purse", raceInfo.getInt("Purse") + "");

            if (trackType.equalsIgnoreCase("harness")) {
                //Gait is the RaceType in the program and the test to compare when it comes to Harness.
                raceConditions.put("gait", raceInfo.getString("Gait"));
            }

        } catch (MalformedURLException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        }

        return raceConditions;
    }

    public String getGreyhoundDistance(String trackCode, String raceNumber) {
        URL url = null;
        String endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Tote/getAllRaces"
                + "?username=iphone&password=ru13juhyo&ip=10.20.2.248&affid=2800&affiliateId=2800"
                + "&affiliateId=2800&output=json&track=" + trackCode + "&type=Greyhound"
                + "&racenum=" + raceNumber);
        HttpURLConnection httpConnection = null;
        String response = "";
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        String distance = "";

        try {
            url = new URL(endpoint);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            bufferedReader = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            while ((response = bufferedReader.readLine()) != null) {
                builder.append(response);
            }

            JSONObject responseObject = new JSONObject(builder.toString());
            JSONArray allRaces = responseObject.getJSONArray("AllRaces");
            JSONObject raceInfo = allRaces.getJSONObject(0);

            distance = raceInfo.getInt("Distance") + "";

        } catch (MalformedURLException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        }

        return distance;
    }

    /**
     *
     * @param trackCode (BrisCode NOT BDS Code)
     * @param raceNumber
     * @return HashMap<String, String> raceConditions fir the given track and race
     */
    public List<HashMap<String, String>> getHorseDetailsForRace(String trackCode, String raceNumber) {
        URL url = null;
        String affId = affiliate.getAffId().toString();
        String endpoint = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Tote/getAllRaces");
        HttpURLConnection httpConnection = null;
        String response = "";
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        List<HashMap<String, String>> runnerList = new ArrayList<>();
        HashMap<String, String> runnerDetails;
        String trackType = new ADWQueries().getTrackType(trackCode);

        endpoint += "?username=iphone"
                + "&password=ru13juhyo"
                + "&ip=10.20.2.248"
                + "&affid=" + affId
                + "&affiliateId=" + affId
                + "&output=json"
                + "&track=" + trackCode
                + "&type=" + trackType
                + "&racenum=" + raceNumber;
        try {
            url = new URL(endpoint);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            bufferedReader = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            while ((response = bufferedReader.readLine()) != null) {
                builder.append(response);
            }

            JSONObject responseObject = new JSONObject(builder.toString());
            JSONArray allRaces = responseObject.getJSONArray("AllRaces");
            JSONObject raceInfo = allRaces.getJSONObject(0);
            JSONArray entries = raceInfo.getJSONArray("Entries");

            for(int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                runnerDetails = new HashMap<String, String>();
                runnerDetails.put("age", entry.getString("Age"));
                runnerDetails.put("averagePaceE1", entry.get("AveragePaceE1").toString());
                runnerDetails.put("averagePaceE2", entry.get("AveragePaceE2").toString());
                runnerDetails.put("averagePaceLp", entry.get("AveragePaceLp").toString());
                runnerDetails.put("averageSpeed", entry.get("AverageSpeed").toString());
                runnerDetails.put("horseName", entry.get("HorseName").toString());
                runnerDetails.put("programNumber", entry.get("ProgramNumber").toString());
                runnerDetails.put("sex", entry.get("Sex").toString());
                runnerDetails.put("jockeyName", entry.get("JockeyName").toString());
                runnerDetails.put("trainerName", entry.get("TrainerName").toString());
                runnerList.add(runnerDetails);
            }
        } catch (MalformedURLException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was a problem trying to get Race Conditions");
            e.printStackTrace();
        }

        return runnerList;
    }

    /**
     * Get the post time, MTP, and race status for the selected track and race
     *
     * @param trackCode
     * @param race
     * @return postTimeInfo
     */
    public HashMap<String, String> getPostTimeInfo(String trackCode, String race) {
        String affId = affiliate.getAffId().toString();
        String endpoint = buildEndpoint("/adw/track/" + trackCode + "/race?"
                + "username=my_tux&ip=10.20.2.248&affid=" + affId + "&affiliateId=" + affId + "&output=json");
        URL url = null;
        HashMap<String, String> postTimeInfo = new HashMap<String, String>();
        HttpURLConnection httpConnection = null;
        String response = "";
        BufferedReader bufferedReader = null;
        StringBuilder builder = new StringBuilder();
        int raceNum = 0;

        //Changes the String to an int and subtracts 1 to adjust for a 0 based array
        raceNum = Integer.valueOf(race);
        raceNum -= 1;

        try {
            url = new URL(endpoint);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            bufferedReader = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            while ((response = bufferedReader.readLine()) != null) {
                builder.append(response);
            }

            JSONArray responseBody = new JSONArray(builder.toString());
            JSONObject raceObject = responseBody.getJSONObject(raceNum);

            postTimeInfo.put("post", raceObject.getString("post"));
            postTimeInfo.put("mtp", raceObject.getInt("mtp") + "");
            postTimeInfo.put("raceStatus", raceObject.getString("raceStatus"));
        } catch (MalformedURLException e) {
            System.out.println("There was a problem trying to get Post Time info");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was a problem trying to get Post Time info");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("Race " + race + " is not available for this track");
            e.printStackTrace();
        }

        return postTimeInfo;
    }

    /**
     * Provides a list of all the entries for the race and which/if the entry was changed
     * @param trackCode the track code which to check
     * @param raceNumber the number of the race to capture the entries list
     * @return HashMap<ProgramNumber, ChangeStatus> A blank "" status indicates no change
     */
    public HashMap<String, String> getEntriesAndChangesForRace(String trackCode, String raceNumber) {
        HashMap<String, String> entryChanges = new HashMap<String, String>();
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody = null;
        JSONArray entryArray = null;
        JSONObject entryObj = null;
        String trackType = new ADWQueries().getTrackType(trackCode);
        String endpoint;
        String programNumber;
        String changeType;
        String adjRace = raceNumber.toLowerCase().replace("race", "").trim();
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Cdi/IntegratedScratch");
        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("track", trackCode);
        requestBody.put("type", trackType);
        requestBody.put("race", adjRace);
        responseBody = postEndpointJSONResponse(endpoint, requestBody);

        // Parse JSON results to acquire an entries/changes list
        entryArray = responseBody.getJSONObject("IntegratedScratches").getJSONArray("EntryChanges");
        for(int i = 0; i < entryArray.length(); i++) {
            entryObj = entryArray.getJSONObject(i);
            programNumber = entryObj.getString("ProgramNumber").trim();
            changeType = entryObj.getString("ChangeType").trim();
            entryChanges.put(programNumber, (changeType.equalsIgnoreCase("New Entry")) ? "" : changeType);
        }
        return entryChanges;
    }

    /**
     * Gets live odds for a specific track/race
     * @param trackCode
     * @param race
     * @param trackType
     * @return
     */
    public HashMap<String, String> getLiveOdds(String trackCode, String race, String trackType) {
        HashMap<String, String> liveOdds = new HashMap<String, String>();
        JSONObject responseBody = null;
        String endpoint;
        JSONObject winOdds = new JSONObject();
        JSONArray entries = new JSONArray();
        JSONObject entry = new JSONObject();
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/webapi/Tote/OddsMtpPost?");
        endpoint += "username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&track=" + trackCode;
        endpoint += "&race=" + race;
        endpoint += "&type=" + trackType;
        responseBody = getEndpointJSONResponse(endpoint);
        
        //Get odds for each horse
        winOdds = responseBody.getJSONObject("WinOdds");
        entries = winOdds.getJSONArray("Entries");

        for(int i=0; i<entries.length(); i++) {
            entry = entries.getJSONObject(i);
            liveOdds.put(entry.getString("ProgramNumber"),
                    entry.getString("TextOdds").replace(" " , ""));
        }

        return liveOdds;
    }

    /**
     * Compiles a list containing information for all running tracks' next race to post
     * @return array of next track/race details
     */
    public JSONArray getUpcomingRacesAllTracks() {
        JSONObject responseBody = null;
        JSONArray tracksArray = null;
        String endpoint;
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/adw/track");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&includeGreyhound=" + "true";
        responseBody = getEndpointJSONResponse(endpoint);

        // Return only the json array of track current race details
        tracksArray = responseBody.getJSONArray("CurrentRace");

        return tracksArray;
    }

    public JSONArray getCalendarRacesByDate(String date) {
        JSONObject responseBody = null;
        JSONArray tracksArray = null;
        String endpoint;
        String affId = affiliate.getAffId().toString();

        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/adw/calendar/" + date + "/");
        endpoint += "?username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&output=" + "json";
        responseBody = getEndpointJSONResponse(endpoint);

        // Return only the json array of track current race details
        tracksArray = responseBody.getJSONArray("Races");

        return tracksArray;
    }
}