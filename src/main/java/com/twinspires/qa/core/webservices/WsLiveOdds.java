package com.twinspires.qa.core.webservices;

import java.util.ArrayList;
import java.util.List;

import com.twinspires.qa.core.util.Util;
import org.json.JSONArray;

import com.twinspires.qa.core.sqlqueries.BDSQueries;

public class WsLiveOdds extends AbstractWS{
    private JSONArray trackObjects;
    private int numTracks;
    private List<String> trackCodes;
    private List<String> trackNames;
    
    public WsLiveOdds(String trackInfoCodes) {
        submitQuery(trackInfoCodes);
    }
    
    public WsLiveOdds(List<String> trackListNames) {
        String trackInfo = getQueryFormattedTrackCodes(trackListNames);
        try {
            submitQuery(trackInfo);
        } catch (NullPointerException e) {
            Util.printLine("ERROR: Failed to submit track codes to Live Odds WS {" + e.getMessage() + "\n" +
                    "  trackListNames { " + trackCodes.toString() + " }\n" +
                    "  trackInfo { " + trackInfo + " }");
        }
    }
    
    /**
     * Gets live odds for all races for a list of tracks
     * @param trackCodes Track Codes must be in the format:
     *				"<trackCode1>:<trackType1>~<trackCode2>:<trackType2>"
     * @return
     */
    public JSONArray getLiveOdds(String trackCodes) {
        JSONArray responseBody = null;
        String endpoint;
        String response;
        String affId = affiliate.getAffId().toString();
    
        // Build endpoint, request data, and make WS call
        endpoint = buildEndpoint("/adw/track/OddsMtpPost?");
        endpoint += "username=" + "my_tux";
        endpoint += "&ip=" + "0.0.0.0";
        endpoint += "&affid=" + affId;
        endpoint += "&affiliateId=" + affId;
        endpoint += "&output=" + "json";
        endpoint += "&tracksInfo=" + trackCodes;
    
        response = getEndpointStringResponse(endpoint);
        responseBody = parseToJSONArray(response);
    
        return responseBody;
    }
    
    public void submitQuery(String trackInfo) {
        trackObjects = getLiveOdds(trackInfo);
        setTrackCodes(trackInfo);
        numTracks = trackObjects.length();
    }
    
    private void setTrackCodes(String trackInfo) {
        String[] tracks = trackInfo.split("~");
        List<String> codes = new ArrayList<>();
        
        for(int i = 0; i < tracks.length; i++) {
            codes.add(tracks[i].split(":")[0]);
        }
        
        trackCodes = codes;
    }
    
    public String getQueryFormattedTrackCodes(List<String> trackNames) {
        BDSQueries bdsQueries = new BDSQueries();
//        List<String> trackCodes = new ArrayList<>();
        String formattedTrackCodes = "";
        
        try {
            this.trackCodes = bdsQueries.getTrackCodes(trackNames);
            this.trackNames = trackNames;
    
            for (int i = 0; i < trackNames.size(); i++) {
                if (i != 0) formattedTrackCodes += "~";
                formattedTrackCodes += this.trackCodes.get(i) + ":";
        
                if (trackNames.get(i).contains("(H)")) {
                    formattedTrackCodes += "HS";
                } else if (trackNames.get(i).contains("(G)")) {
                    formattedTrackCodes += "GH";
                } else {
                    formattedTrackCodes += "TB";
                }
            }
        } catch (NullPointerException e) {
            Util.printLine("ERROR: Failed to format track codes {" + e.getMessage() + "\n" +
                    "  trackCodes { " + this.trackCodes.toString() + " }\n" +
                    "  trackNames { " + this.trackNames.toString() + " }\n" +
                    "  formattedTrackCodes { " + formattedTrackCodes + " }");
        }

        return formattedTrackCodes;
    }
    
    public int getNumTracks() {
        return numTracks;
    }
    
    /**
     * Performs a lookup of the trackname from the list which was used for the WS request
     * @param trackName track name which to match
     * @return index of the track in the WS response
     */
    public int byTrackName(String trackName) {
        for(int i = 0; i < trackNames.size(); i++) {
            if(trackNames.get(i).equalsIgnoreCase(trackName)) {
                return byTrackCode(trackCodes.get(i));
            }
        }
        return -1;
    }
    
    /**
     * Performs a lookup of the trackcode from the list which was used for the WS request
     * @param trackCode bris track code which to match
     * @return index of the track in the WS response
     */
    public int byTrackCode(String trackCode) {
        for(int i = 0; i < numTracks; i++) {
            if(getBrisCode(i).equalsIgnoreCase(trackCode))
                return i;
        }
        return -1;
    }
    
    /**
     * Gets the UTC Post Time time stamp
     * @param trackIndex index of the track for which to return the post time timestamp
     * @param format "full" to return the full timestamp, "time" for time only, "date" for date only
     * @return
     */
    public String getPostTimeStampUTC(int trackIndex, String format) {
        String timestamp = "";
        if(trackIndex < numTracks) timestamp = trackObjects.getJSONObject(trackIndex).getString("postTimeStampUtc");
        switch (format.toLowerCase().trim()) {
            case "date":
                return timestamp.substring(0, timestamp.indexOf("T"));
            case "time":
                return timestamp.substring(timestamp.indexOf("T")+1, timestamp.indexOf("Z"));
            case "full":
            default:
                return timestamp;
        }
    }
    public String getPostTimeStampUTC(int trackIndex) {
        return getPostTimeStampUTC(trackIndex, "full");
    }
    public String getPostTimeStampUTC() {
        return getPostTimeStampUTC(0);
    }
    
    /**
     * Gets the Post Time time stamp
     * @param trackIndex index of the track for which to return the post time timestamp
     * @param format "full" to return the full timestamp, "time" for time only, "date" for date only
     * @return
     */
    public String getPostTimeStamp(int trackIndex, String format) {
        String timestamp = "";
        if(trackIndex < numTracks) timestamp = trackObjects.getJSONObject(trackIndex).getString("postTimeStamp");
        switch (format.toLowerCase().trim()) {
            case "date":
                return timestamp.substring(0, timestamp.indexOf("T"));
            case "time":
                return timestamp.substring(timestamp.indexOf("T")+1, timestamp.length());
            case "full":
            default:
                return timestamp;
        }
    }
    public String getPostTimeStamp(int trackIndex) {
        return getPostTimeStamp(trackIndex, "full");
    }
    public String getPostTimeStamp() {
        return getPostTimeStamp(0);
    }
    
    /**
     * Gets the track type for the given track
     * @param trackIndex
     * @return
     */
    public String getTrackType(int trackIndex) {
        if(trackIndex < numTracks) return trackObjects.getJSONObject(trackIndex).optString("trackType");
        return "IDX_ERROR";
    }
    public String getTrackType() {
        return getTrackType(0);
    }
    
    /**
     * Gets the MTP for the current race for the specified track
     * @param trackIndex
     * @return
     */
    public String getMTP(int trackIndex) {
        if(trackIndex < numTracks) return Integer.toString(trackObjects.getJSONObject(trackIndex)
                .optInt("mtp", -1));
        return "IDX_ERROR";
    }
    public String getMTP() {
        return getMTP(0);
    }
    
    public String getToteClock(int trackIndex) {
        if(trackIndex < numTracks) return trackObjects.getJSONObject(trackIndex).getString("tote_clock");
        return "IDX_ERROR";
    }
    public String getToteClock() {
        return getToteClock(0);
    }
    
    public String getRaceNum(int trackIndex) {
        if(trackIndex < numTracks) return Integer.toString(trackObjects.getJSONObject(trackIndex)
                .getInt("raceNum"));
        return "IDX_ERROR";
    }
    public String getRaceNum() {
        return getRaceNum(0);
    }
    
    public String getBrisCode(int trackIndex) {
        if(trackIndex < numTracks) return trackObjects.getJSONObject(trackIndex).getString("brisCode");
        return "IDX_ERROR";
    }
    public String getBrisCode() {
        return getBrisCode(0);
    }
    
    public List<String> getTextOdds(int trackIndex) {
        JSONArray winOddsEntries;
        List<String> textOddsList = new ArrayList<>();
        
        if(trackIndex < numTracks) {
            winOddsEntries = trackObjects.getJSONObject(trackIndex).getJSONArray("winOddsEntries");
        } else {
            return null;
        }
        
        for(int i = 0; i < winOddsEntries.length(); i++) {
            textOddsList.add(winOddsEntries.getJSONObject(i).optString("TextOdds").trim());
        }
        
        return textOddsList;
    }
    public List<String> getTextOdds() {
        return getTextOdds(0);
    }
    
    public List<String> getNumOdds(int trackIndex) {
        JSONArray winOddsEntries;
        List<String> textOddsList = new ArrayList<>();
        
        if(trackIndex < numTracks) {
            winOddsEntries = trackObjects.getJSONObject(trackIndex).getJSONArray("winOddsEntries");
        } else {
            return null;
        }
        
        for(int i = 0; i < winOddsEntries.length(); i++) {
            textOddsList.add(winOddsEntries.getJSONObject(i).optString("NumOdds"));
        }
        
        return textOddsList;
    }
    public List<String> getNumOdds() {
        return getNumOdds(0);
    }
    
    public List<String> getProgramNumbers(int trackIndex) {
        JSONArray winOddsEntries;
        List<String> textOddsList = new ArrayList<>();
        
        if(trackIndex < numTracks) {
            winOddsEntries = trackObjects.getJSONObject(trackIndex).getJSONArray("winOddsEntries");
        } else {
            return null;
        }
        
        for(int i = 0; i < winOddsEntries.length(); i++) {
            textOddsList.add(winOddsEntries.getJSONObject(i).optString("ProgramNumber"));
        }
        
        return textOddsList;
    }
    public List<String> getProgramNumbers() {
        return getProgramNumbers(0);
    }
}
