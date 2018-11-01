package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.webservices.WsTrackInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TracksData {
    static JSONArray todaysRacesResponse;
    List<TrackData> tracks;

    // Use if not logged in as tux shows all tracks in anon mode
    public TracksData(){
        this.todaysRacesResponse = new WsTrackInfo().getTodaysRacesInfo("",true);
        initTracks();
    }

    // Use if in logged in as some tracks are state restricted (ex. Greyhound tracks)
    public TracksData(String state){
        this.todaysRacesResponse = new WsTrackInfo().getTodaysRacesInfo(state,true);
        initTracks();
    }

    // Construct a list of TrackDataObjects
    public TracksData(TrackData... trackData){
        this.tracks = new ArrayList<>();
        for(int i = 0; i < trackData.length; i++){
            this.tracks.add(trackData[i]);
        }
    }

    private void initTracks(){
        JSONObject trackObject = null;
        this.tracks = new ArrayList<>();

        for(int i = 0; i < todaysRacesResponse.length(); i++){
            trackObject = todaysRacesResponse.getJSONObject(i);
            tracks.add(new TrackData(trackObject.getString("name"),
                                            trackObject.getString("brisCode"),
                                            trackObject.getString("type")));
        }
    }

    public TracksData updateWithCurrent(){
        tracks.get(0).updateWithCurrent(true);
        for (TrackData track: tracks) {
            track.updateWithCurrent(false);
        }
        return this;
    }

    public List<TrackData> getTracks(){return tracks;}

    public TrackData getTrack(int index){return tracks.get(index);}

    public TracksData sortTracksByMTPStatus(){
        tracks.sort(Comparator.comparing(TrackData::getMTPforTodaysRacesSorting)
                .thenComparing(TrackData::getDisplayedTrackName));
        return this;
    }

    public TracksData sortTracksByMTPForToteBoard(){
        tracks.sort(Comparator.comparing(TrackData::getMTPforToteBoardSorting)
                .thenComparing(TrackData::getDisplayedTrackName));
        return this;
    }

    public void addTrack(TrackData... trackData){
        for (TrackData track: trackData) {
            tracks.add(track);
        }
    }

    public void addTrack(String... trackNames){
        for(String name:trackNames){
            tracks.add(new TrackData().setTrackName(name));
        }
    }

    public void removeTrack(String... trackNames){
        for(String name:trackNames){
            tracks.remove(indexOfTrackName(name));
        }
    }

    private int indexOfTrackName(String trackName){
        int index = 0;
        for(TrackData trackData: tracks){
            if(trackData.getDisplayedTrackName().contains(trackName)){
                return index;
            }
            index++;
        }
        return -1; // If not found return -1 to trip out of bounds exception
    }
}
