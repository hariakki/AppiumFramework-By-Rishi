package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class WsReplayTrackRaces extends AbstractWS {
    // Static WS Request Variables
    private static final String REQUEST_METHOD = REQ_METHOD_GET;
    private static final String ENDPOINT = buildEndpoint("/webapi/Replay/TrackRaces");
    private static final String PARAM_USERNAME = "my_tux";
    private static final String PARAM_OUTPUT = "json";
    
    // Other Static Variables
    public static final String FIELD_TRACK_NAME = "Name";
    public static final String FIELD_TRACK_CODE = "BrisCode";
    private static final String FIELD_TRACK_TYPE = "BrisType";
    private static final String FIELD_TRACK_RACE = "Races";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    // Non-Static Request Variables
    private String param_date;
    
    // Response Variables
    private String responseDebug;
    private String responseUtsTimestamp;
    private JSONArray responseTracks;
    private String responseErrorCode;
    private String responseErrorDesc;
    
    private void constructor(String date) {
        responseDebug = "";
        responseUtsTimestamp = "";
        responseErrorCode = "";
        responseErrorDesc = "";
        
        setDate(date);
        submitQuery();
    }
    
    /**
     * Makes the WS request with the provided date
     * <p>
     * The current date is used when the optional date parameter is not provided
     * @param date [Optional] must be in yyyy-MM-dd format
     */
    public WsReplayTrackRaces(String date) {
        constructor(date);
    }
    public WsReplayTrackRaces(Date date) {
        constructor(Util.formatDateTime(date, DATE_FORMAT));
    }
    public WsReplayTrackRaces() {
        constructor(Util.formatDateTime(new Date(), DATE_FORMAT));
    }
    
    /**
     * Parses the WS response into a tracks JSONArray and notes the timestamp
     */
    private void parseResponse() {
        JSONObject fullResponse;
        fullResponse = parseToJSONObject(lastResponseBody);
        
        // if empty
        responseDebug = fullResponse.optString("Debug");
        responseUtsTimestamp = fullResponse.optString("UTS");
        responseTracks = fullResponse.optJSONArray("Tracks");
        if(responseTracks == null) {
            responseErrorCode = fullResponse.getString("ErrorCode");
            responseErrorDesc = fullResponse.getJSONObject("Error").getString("Description");
        } else {
            responseErrorCode = "";
            responseErrorDesc = "";
        }
    }
    
    /**
     * Sets the date for the WS request
     * <p>
     * The current date is used when the optional date parameter is not provided
     * @param date [Optional] must be in yyyy-MM-dd format
     */
    public void setDate(String date) {
        param_date = date;
    }
    public void setDate(Date date) {
        param_date = Util.formatDateTime(date, DATE_FORMAT);
    }
    public void setDate() {
        param_date = Util.formatDateTime(new Date(), DATE_FORMAT);
    }
    
    public void submitQuery(){
        JSONObject requestBody = new JSONObject();
        requestBody.put("date", param_date);
        requestBody.put("username", PARAM_USERNAME);
        requestBody.put("output", PARAM_OUTPUT);
        
        sendRequest(REQUEST_METHOD, ENDPOINT, requestBody);
        parseResponse();
    }
    
    /**
     * Looks up the index of the desired Track Name
     * @param trackName track name to match
     * @return index of the track in the WS response
     */
    public int byTrackName(String trackName) {
        if(responseTracks == null) {
            System.out.println("WsReplayTrackRaces.byTrackName() Error "
                    + responseErrorCode + ": " + responseErrorDesc);
        } else {
            for (int i = 0; i < responseTracks.length(); i++) {
                if (responseTracks.getJSONObject(i).getString(FIELD_TRACK_NAME).trim()
                        .equalsIgnoreCase(trackName.replace("(H)", "")
                                .replace("(G)", "").trim())) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Looks up the index of the desired track's Bris Code
     * @param trackCode bris code to match
     * @return index of the track in the WS response
     */
    public int byTrackCode(String trackCode) {
        if(responseTracks == null) {
            System.out.println("WsReplayTrackRaces.byTrackCode() Error "
                    + responseErrorCode + ": " + responseErrorDesc);
        } else {
            for (int i = 0; i < responseTracks.length(); i++) {
                if (responseTracks.getJSONObject(i).getString(FIELD_TRACK_CODE).trim()
                        .equalsIgnoreCase(trackCode.trim()))
                    return i;
            }
        }
        return -1;
    }
    
    /**
     * Gets the list of track names returned by the WS response
     * @return List of track names
     */
    public List<String> getTrackNamesList() {
        List<String> trackNameList = new ArrayList<>();
        
        if(responseTracks == null) {
            trackNameList.add("Error " + responseErrorCode + ": " + responseErrorDesc);
        } else {
            for(int i = 0; i < responseTracks.length(); i++) {
                trackNameList.add(responseTracks.getJSONObject(i).getString(FIELD_TRACK_NAME));
            }
        }
        return trackNameList;
    }
    
    /**
     * Gets a map of the Track Names to Track Bris Codes returned by the WS response
     * <p>
     * If the optional keyField is not provided, defaults to Track Name as the key and Bris Code as the value
     * @param keyField (Optional) the Field to use as the key (FIELD_TRACK_NAME or FIELD_TRACK_CODE)
     * @return HashMap of track names
     */
    public Map<String, String> getTrackCodesMap(String keyField) {
        HashMap<String, String> trackCodeMap = new HashMap<>();
        JSONObject track;
        String key;
        String value;
        
        // Sets desired key/value fields
        if(keyField == FIELD_TRACK_CODE) {
            key = FIELD_TRACK_CODE;
            value = FIELD_TRACK_NAME;
        } else { // (DEFAULT) if(keyField == FIELD_TRACK_NAME)
            key = FIELD_TRACK_NAME;
            value = FIELD_TRACK_CODE;
        }
        
        if(responseTracks == null) {
            trackCodeMap.put("Error", responseErrorCode + ": " + responseErrorDesc);
        } else {
            for(int i = 0; i < responseTracks.length(); i++) {
                track = responseTracks.getJSONObject(i);
                trackCodeMap.put(track.optString(key, "ERROR"+i)
                        , track.optString(value, "ERROR"+i));
            }
        }
        
        return trackCodeMap;
    }
    public Map<String, String> getTrackCodesMap() {
        return getTrackCodesMap("FIELD_TRACK_NAME");
    }
    
    /**
     * Finds list of races with Replay video feeds (by index)
     * <p>
     * Utilize byTrackCode and byTrackName to obtain correct index
     * @param trackIndex The index of the track which to obtain Replays races
     * @return list of races for the specified track index
     */
    public List<String> getRaces(int trackIndex) {
        List<String> trackRacesList = new ArrayList<>();
        JSONArray trackRacesArray;
        
        // An error exists in the WS response
        if(responseTracks == null) {
            trackRacesList.add("Error " + responseErrorCode + ": " + responseErrorDesc);
        } else {
            // Index is out of range
            if(trackIndex < 0 || trackIndex >= responseTracks.length()) {
                trackRacesList.add("WsReplayTrackRaces.getRaces() Error: Track not found at index (" + trackIndex + ")");
            } else {
                trackRacesArray = responseTracks.getJSONObject(trackIndex).getJSONArray(FIELD_TRACK_RACE);
                for (int i = 0; i < trackRacesArray.length(); i++) {
                    trackRacesList.add(Integer.toString(trackRacesArray.getInt(i)));
                }
            }
        }
        return trackRacesList;
    }
}
