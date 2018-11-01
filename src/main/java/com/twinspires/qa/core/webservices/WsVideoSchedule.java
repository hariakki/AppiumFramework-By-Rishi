package com.twinspires.qa.core.webservices;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class WsVideoSchedule extends AbstractWS {
    // Tracks Fields
    private static String BRIS_CODE = "BrisCode";          // String
    private static String TRACK_TYPE = "TrackType";        // String
    private static String DISPLAY_NAME = "DisplayName";    // String
    private static String VIDEO = "Video";                 // JSONObject
    // Track Video Fields
    private static String VIDEO_FEED_ID = "FeedID";        // int
    private static String VIDEO_PROVIDERS = "Providers";   // JSONArray
    // Track Video Providers Fields
    private static String PROVIDERS_NAME = "Name";         // String
    private static String PROVIDERS_TYPE = "Type";         // String
    private static String PROVIDERS_OPTIONS = "Options";   // JSONArray
    // Track Video Provider Options Fields
    private static String OPTIONS_NAME = "Name";           // String
    private static String OPTIONS_TYPE = "Type";           // String
    private static String OPTIONS_VALUES = "Values";       // String list

    private String endpoint;
    private String affCode;
    private JSONObject fullResponse;
    private JSONArray trackList;


    public WsVideoSchedule() {
        String date;
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        date = simpleDateFormat.format(new Date());

        affCode = affiliate.getAffId();
        endpoint = buildEndpoint("/adw/video/schedule/" + date + "/FLV/" + affCode);
        submitRequest();
    }

    public void submitRequest() {
        JSONObject requestBody = new JSONObject();

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affCode);
        requestBody.put("affiliateId", affCode);
        requestBody.put("output", "json");

        // Sends the WS request
        this.sendRequest(REQ_METHOD_GET, endpoint, requestBody);

        // Basic parsing of WS response
        fullResponse = parseToJSONObject();
        trackList = fullResponse.getJSONArray("Tracks");
    }

    /** (Track Level)
     * Returns the value of track index's key
     * @param index index of track to select data
     * @param key the data key to for which to get the value
     * @return the value of the track's key
     */
    private String getTrackData(int index, String key) {
        return trackList.getJSONObject(index).getString(key);
    }

    /** (Video Level)
     * Returns the value of the track index's Video's FeedID
     * @param index index of the track to select data
     * @return the track's video's feed id
     */
    private String getVideoFeedId(int index) {
        return Integer.toString(trackList.getJSONObject(index).getInt(VIDEO_FEED_ID));
    }

    public int getTrackIndexByTrackCode(String brisCode) {
        String searchTerm = brisCode.toLowerCase().trim();
        for (int i = 0; i < trackList.length(); i++) {
            if (getTrackData(i, BRIS_CODE).equalsIgnoreCase(searchTerm)) {
                return i;
            }
        }
        return -1;
    }

    public int getTrackIndexByTrackName(String trackName) {
        String searchTerm = trackName.trim();
        for(int i = 0; i < trackList.length(); i++) {
            if(getTrackData(i, DISPLAY_NAME).equalsIgnoreCase(searchTerm)) {
                return i;
            }
        }
        return -1;
    }

    /** (Providers Level)
     * Determines if a Neulion video feed is available for the track
     */
    public boolean isNeulionVideoAvailable(int index) {
        JSONArray providers = trackList.getJSONObject(index).getJSONObject(VIDEO).getJSONArray(VIDEO_PROVIDERS);
        for(int i = 0; i < providers.length(); i++) {
            if(providers.getJSONObject(i).getString(PROVIDERS_NAME).equalsIgnoreCase("NEULION"))
                return true;
        }
        return false;
    }

    /** (Providers Level)
     * Determines if an RCN video feed is available for the track
     */
    public boolean isRcnVideoAvailable(int index) {
        JSONArray providers = trackList.getJSONObject(index).getJSONObject(VIDEO).getJSONArray(VIDEO_PROVIDERS);
        for(int i = 0; i < providers.length(); i++) {
            if(providers.getJSONObject(i).getString(PROVIDERS_NAME).equalsIgnoreCase("MSRCN"))
                return true;
        }
        return false;
    }

    /**
     * Looks for a track by track code.  It's presence indicates a video feed is available
     * @param brisCode the track code of the desired track
     * @return true if a video feed should be present; else false
     */
    public boolean isVideoAvailByTrackCode(String brisCode) {
        if(getTrackIndexByTrackCode(brisCode) >= 0) {
            return true;
        }
            return false;
    }

    /**
     * Looks for a track by track name.  It's presence indicates a video feed is available
     * @param trackName the desire track name
     * @return true if a video feed should be present; else false
     */
    public boolean isVideoAvailByTrackName(String trackName) {
        if(getTrackIndexByTrackName(trackName)>=0)
            return true;
        else
            return false;
    }
}
