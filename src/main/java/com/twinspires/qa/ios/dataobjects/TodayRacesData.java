package com.twinspires.qa.ios.dataobjects;

import com.twinspires.qa.core.testdata.TrackData;
import com.twinspires.qa.core.webservices.WsTrackInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.*;

/**
 * Created by dalwinder.singh on 9/6/18.
 */
public class TodayRacesData {

    WsTrackInfo wsTrackInfo = new WsTrackInfo();
    JSONArray trackArray = null;
    int totalTracks = 0;

    public void loadTrackArray(){
        trackArray = wsTrackInfo.getTodaysRacesInfo("",true);
        setTotalTracks(trackArray.length());
    }

    public TrackData getCurrentRaceInfo(String trackName) {

        JSONObject trackObject = null;
        TrackData trackData = new TrackData();
        int raceNumber = 0;

        for (int i = 0; i < trackArray.length(); i++) {
            trackObject = trackArray.getJSONObject(i);
            if (trackObject.getString("name").toUpperCase().contains(trackName.toUpperCase())) {
                raceNumber = trackObject.optInt("currentRaceNumber");
                trackData.setRace(Integer.toString(raceNumber));
                JSONArray racesArray = trackObject.getJSONArray("races");
                JSONObject raceObject =  racesArray.getJSONObject(raceNumber - 1);
                if(raceObject.optString("status").equalsIgnoreCase("Off")){
                    trackData.setRaceStatus("OFF");
                }
                else if(raceObject.optString("status").equalsIgnoreCase("Open")) {
                    if(raceObject.optInt("mtp") < 99) {
                        trackData.setRaceStatus(Integer.toString(raceObject.optInt("mtp")) + " MTP");
                    } else {
                        String postTime = raceObject.optString("postTime");
                        String postTimeAdjusted = LocalTime.parse(postTime.subSequence(postTime.indexOf("T") + 1,
                                postTime.indexOf("-04:00")),
                                DateTimeFormatter.ofPattern("HH:mm:ss")).format(DateTimeFormatter.ofPattern("h:mm a"));
                        trackData.setRaceStatus(postTimeAdjusted);
                    }
                }
                else if(raceObject.optString("status").equalsIgnoreCase("Canceled")){
                    trackData.setRaceStatus("CLOSED");
                }
                break;
            }
        }
     return trackData;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public List<String> getAllTrackNames(){
        JSONObject trackObject = null;
        List<String> allTrackNames = new ArrayList<>();

        for (int i = 0; i < trackArray.length(); i++) {
            trackObject = trackArray.getJSONObject(i);
            allTrackNames.add(trackObject.getString("name").toUpperCase());
        }
        sort(allTrackNames, String.CASE_INSENSITIVE_ORDER);
        return  allTrackNames;
    }

    public int getTotalRacesForATrack(String trackName) {
        JSONObject trackObject = null;
        int totalRaces = 0;

        for (int i = 0; i < trackArray.length(); i++) {
            trackObject = trackArray.getJSONObject(i);
            if (trackObject.getString("name").toUpperCase().contains(trackName.toUpperCase())) {
                JSONArray racesArray = trackObject.getJSONArray("races");
                totalRaces = racesArray.length();
                break;
            }
        }
        return totalRaces;
    }


}
