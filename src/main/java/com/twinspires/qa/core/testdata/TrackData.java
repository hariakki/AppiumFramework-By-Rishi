package com.twinspires.qa.core.testdata;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import com.twinspires.qa.core.webservices.WsRaceInfo;
import com.twinspires.qa.core.webservices.WsVideoSchedule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TrackData {

    static private JSONArray trackArray;
    private String trackName;
    private String brisCode;
    private String bdsCode;
    private String trackType;
    private String country;
    private String trackStatus;
    private String raceStatus;
    private Integer race;
    private Integer mtp;
    private boolean trackCanceled;

    /**
     * // TODO : trackExclusions has not been tested.  Be aware that it may require modification
     *
     * @param dataCondition   Conditions a track must meet to be selected (match DB condition strings)
     * @param trackExclusions [Optional] variable number of tracks which to not select
     */
    public TrackData(String dataCondition, String... trackExclusions) {
        TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
        clearData();

        if(dataCondition.equalsIgnoreCase("video")) {
            // video picks any track with a video feed and must compare against available tracks ws in getProdTrack()
            getProdTrack(dataCondition, trackExclusions);
        } else if (testEnv.equals(TestEnv.PROD)) {
            getProdTrack(dataCondition, trackExclusions);
        } else if (testEnv.equals(TestEnv.LOAD)){
            getTestTrack("basic wager load test",trackExclusions);
        }
        else {
            getTestTrack(dataCondition, trackExclusions);
        }
    }

    public TrackData() {
        clearData();
    }

    public TrackData(String trackName, String brisCode, String trackType){
        clearData();
        this.trackName = trackName;
        this.brisCode = brisCode;
        this.trackType = trackType;
    }

    private TrackData clearData() {
        this.trackName = "";
        this.brisCode = "";
        this.bdsCode = "";
        this.trackType = "";
        this.country = "";
        this.raceStatus = "";
        this.race = 0;
        this.mtp = 0;
        this.trackCanceled = false;
        return this;
    }

    /**
     * Get production track from the current track/race WS and select at random based on conditions
     *
     * @param dataCondition
     * @return
     */
    private TrackData getProdTrack(String dataCondition, String... trackExclusions) {
        JSONObject track = null;
        JSONArray upcomingTracks = new WsRaceInfo().getUpcomingRacesAllTracks();
        WsVideoSchedule wsVideoSchedule = null;
        boolean excluded = false;
        boolean found = false;

        // Only make Video Schedule WS call if a video data condition is requested
        if(dataCondition.contains("video")) wsVideoSchedule = new WsVideoSchedule();

        for (int i = 0; i < upcomingTracks.length(); i++) {
            // Get next track in current tracks list
            track = upcomingTracks.getJSONObject(i);

            // Ensure track is not in exclusions list
            excluded = false;
            for (int t = 0; t < trackExclusions.length; t++) {
                if (track.getString("DisplayName").contains(trackExclusions[t])) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) continue;

            // Find a track matching the necessary conditions specified
            switch (dataCondition.toLowerCase()) {
                case "video":
                    if(wsVideoSchedule.isVideoAvailByTrackCode(track.getString("BrisCode"))
                            && track.optInt("Mtp") < 30
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")) {
                        found = true;
                    }
                    break;

                case "taxable wager":
                case "special wager":
                case "pools":
                case "will pays":
                case "classic wagering":
                case "basic wager":
                    if (track.optInt("Mtp") >= 5
                            && track.optInt("Mtp") < 99
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")) {
                        found = true;
                    }
                    break;

                case "also rans":
                case "replays":
                case "results":
                    if (track.optInt("RaceNum") >= 2
                            && track.optString("Status").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")) {
                        found = true;
                    }
                    break;

                case "program stats":
                    if (track.optInt("Mtp") >= 5
                            && track.optInt("Mtp") < 99
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")
                            && track.getString("TrackType").equalsIgnoreCase("Thoroughbred")
                        // Domestic Tracks Only
                            && !track.getString("DisplayName").toUpperCase().contains("AUSTRALIA")
                            && !track.getString("DisplayName").toUpperCase().contains("IND")
                            && !track.getString("DisplayName").toUpperCase().contains("IRE")
                            && !track.getString("DisplayName").toUpperCase().contains("SAF")
                            && !track.getString("DisplayName").toUpperCase().contains("UK")
                            && !track.getString("DisplayName").toUpperCase().contains("KOR")) {
                        found = true;
                    }
                    break;
                case "thoroughbred wager info":
                case "general thoroughbred":
                    if (track.optInt("Mtp") >= 5
                            && track.optInt("Mtp") < 99
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")
                            && track.getString("TrackType").equalsIgnoreCase("Thoroughbred")) {
                        found = true;
                    }
                    break;

                case "polling harness":
                case "general harness":
                    if (track.optInt("Mtp") >= 5
                            && track.optInt("Mtp") < 99
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")
                            && track.getString("TrackType").equalsIgnoreCase("Harness")) {
                        found = true;
                    }
                    break;

                case "general greyhound":
                    if (track.optInt("Mtp") >= 5
                            && track.optInt("Mtp") < 99
                            && track.optString("RaceStatus").equalsIgnoreCase("open")
                            && !track.getBoolean("TrackCanceled")
                            && track.getString("TrackType").equalsIgnoreCase("Greyhound")) {
                        found = true;
                    }
                    break;

                default:
                    if (!track.getBoolean("TrackCanceled")) {
                        found = true;
                    }
                    break;
            }

            // Break out of search loop when track matching conditions is found
            if (found) {
                break;
            }
        }

        // When a match is found, set properties
        if (found) {
            this.setTrackName(track.optString("DisplayName"));
            this.setBrisCode(track.optString("BrisCode"));
            this.setBdsCode(track.optString("BrisCode"));
            this.setTrackType(track.optString("TrackType"));
            this.setCountry("");
            this.setRace(track.optInt("RaceNum"));
            this.setMtp(track.optInt("Mtp"));
            this.setRaceStatus(track.optString("RaceStatus"));
            this.setTrackCanceled(track.optBoolean("TrackCanceled"));
        } else {
            this.clearData();
        }
        return this;
    }

    /**
     * Get test track from the automation database and update with current track/race info from WS
     *
     * @param dataCondition
     *     NOTE:  VIDEO dataConditions select track via getProdTrack to allow safe selection of tracks with video feeds
     * @return
     */
    private TrackData getTestTrack(String dataCondition, String... trackExclusions) {
        boolean excluded;
        boolean newQuery = true;
        List<HashMap<String, String>> tracksList = new TestDataQueries().getTrack(dataCondition, getTestCycle(), false);
        HashMap<String, String> trackData = null;
        String exclusion = "";

        // Ensure track is not in exclusions list
        for (int i = 0; i < tracksList.size(); i++) {
            excluded = false;
            for (int t = 0; t < trackExclusions.length; t++) {
                exclusion = trackExclusions[t]
                        .replace("(H)", "")
                        .replace("(G)", "").trim();
                if (tracksList.get(i).get("trackName").contains(exclusion)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            } else { // found a match
                trackData = tracksList.get(i);
                this.setTrackName(trackData.get("trackName"));
                this.setBrisCode(trackData.get("brisCode"));
                this.setBdsCode(trackData.get("bdsCode"));
                this.setTrackType(trackData.get("trackType"));
                this.setCountry(trackData.get("country"));
                this.updateWithCurrent(newQuery);
                newQuery = false;
                if((!dataCondition.toLowerCase().contains("cancel")
                        && !this.isTrackCanceled()) // If track is not supposed to be canceled
                    || (dataCondition.toLowerCase().contains("cancel")
                        && this.isTrackCanceled())){ // If track is supposed to be canceled
                    break;
                } else {
                    trackData = null;
                    this.clearData();
                }
            }
        }

        // Matching data NOT found
        if (trackData == null) return null;
        return this;
    }

    /**
     * Be mindful that afternoon cycle is after 13:20
     *
     * @return M (morning) and A (afternoon)
     */
    private String getTestCycle() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss a");
        return (dateFormat.format(date).contains("AM") ? "M" : "A");
    }

    public String getTrackName() {
        return trackName;
    }

    public String getBrisCode() {
        return brisCode;
    }

    public String getBdsCode() {
        return bdsCode;
    }

    public String getTrackType() {
        return trackType;
    }

    public String getCountry() {
        return country;
    }

    public String getRaceStatus() {
        return raceStatus;
    }

    public String getRaceStr() {
        return race.toString();
    }

    public int getRaceInt() {
        return race.intValue();
    }

    public String getMtpStr() {
        return mtp.toString();
    }

    public String getDisplayedMTP(){
        String displayedMTP = "";
        if (this.mtp != null){
            displayedMTP = this.mtp.toString();
        }
        if (this.raceStatus.equalsIgnoreCase("off")){
            displayedMTP = this.raceStatus;
        }
        return displayedMTP;
    }

    public String getDisplayedTrackName(){
        if(this.trackType.equalsIgnoreCase("Harness")){
            return this.trackName + " (H)";
        }
        if(this.trackType.equalsIgnoreCase("Greyhound")){
            return this.trackName + " (G)";
        }
        return this.trackName;
    }

    public int getMtpInt() {
        return mtp.intValue();
    }

    public int getMTPforToteBoardSorting(){
        if(raceStatus.equalsIgnoreCase("off")){
            return 100;
        }
        return mtp.intValue();
    }

    public int getMTPforTodaysRacesSorting(){
        // Closed was added due to Greyhounds being dumb
        if(raceStatus.equalsIgnoreCase("off") || raceStatus.equalsIgnoreCase("closed")){
            return (-1);
        }
        if(raceStatus.equalsIgnoreCase("official")){
            return (-2);
        }
        if(raceStatus.equalsIgnoreCase("tba")){
            return 100;
        }
        if(trackCanceled){
            return 101;
        }
        return mtp.intValue();
    }

    public boolean isTrackCanceled() {
        return trackCanceled;
    }

    public TrackData setTrackName(String trackNameVal) {
        if (trackNameVal != null) {
            this.trackName = trackNameVal.trim();
        } else {
            this.trackName = "";
        }
        return this;
    }

    public TrackData setBrisCode(String brisCodeVal) {
        if (brisCodeVal != null) {
            this.brisCode = brisCodeVal.trim();
        } else {
            this.brisCode = "";
        }
        return this;
    }

    public TrackData setBdsCode(String bdsCodeVal) {
        if (bdsCodeVal != null) {
            this.bdsCode = bdsCodeVal.trim();
        } else {
            this.bdsCode = "";
        }
        return this;
    }

    public TrackData setTrackType(String trackTypeVal) {
        if (trackTypeVal != null) {
            this.trackType = trackTypeVal.trim();
        } else {
            this.trackType = "";
        }
        return this;
    }

    public TrackData setCountry(String countryVal) {
        if (countryVal != null) {
            this.country = countryVal.trim();
        } else {
            this.country = "";
        }
        return this;
    }

    public TrackData setRaceStatus(String raceStatusVal) {
        if (raceStatusVal != null) {
            this.raceStatus = raceStatusVal.trim();
        } else {
            this.raceStatus = "";
        }
        return this;
    }

    public TrackData setTrackStatus(String trackStatusVal) {
        if (trackStatusVal != null) {
            this.trackStatus = trackStatusVal.trim();
        } else {
            this.trackStatus = "";
        }
        return this;
    }

    public TrackData setRace(String race) {
        if (race != null) {
            this.race = Integer.valueOf(race.replace("Race", "").trim());
        } else {
            this.race = 0;
        }
        return this;
    }

    public TrackData setRace(int race) {
        this.race = new Integer(race);
        return this;
    }

    public TrackData setMtp(String mtp) {
        if (mtp != null) {
            this.mtp = Integer.valueOf(mtp.replace("MTP", "").trim());
        } else {
            this.mtp = 0;
        }
        return this;
    }

    public TrackData setMtp(int mtp) {
        this.mtp = new Integer(mtp);
        return this;
    }

    public TrackData setTrackCanceled(boolean isCanceled) {
        this.trackCanceled = isCanceled;
        return this;
    }

    /**
     * If current race is "off", choose next race.
     * IMPORTANT NOTE:  This method does NOT accurately update any other race values such as mtp or raceStatus
     */
    public TrackData setRaceToNextWagerable() {
        if (!this.raceStatus.equalsIgnoreCase("open")
                || this.getMtpInt() < 5 // some tests starts at 1-2 MTP and then race was off and the test would fail
                                        // on submitting bets
                || this.trackCanceled == true ) {
            this.race++;
            this.mtp = 0; // Clear previous race mtp but next race's MTP is unknown
            this.raceStatus = "Open";
        }
        return this;
    }

    /**
     * Set the race to previous race.  Does not decrease beyond race 1
     */
    public TrackData setRaceToPrevious() {
        if(this.race > 1) {
            --this.race;
        }
        return this;
    }

    /**
     * uses WsRaceInfo.getUpcomingRacesAllTracks to find the current data for this track and update this object
     */
    public TrackData updateWithCurrent() {
        return updateWithCurrent(true);
    }
    public TrackData updateWithCurrent(boolean newQuery) {
        if(newQuery) {
            this.trackArray = new WsRaceInfo().getUpcomingRacesAllTracks();
        }
        JSONObject trackObject = null;

        String adjTrackName = this.trackName
                .replace("(H)", "")
                .replace("(G)", "").trim();

        for (int i = 0; i < trackArray.length(); i++) {
            trackObject = trackArray.getJSONObject(i);
            if (trackObject.getString("DisplayName").contains(adjTrackName)) {
                setTrackName(trackObject.optString("DisplayName", this.trackName)); // Enables partial matching of DisplayName replacing partial name with full
                setBrisCode(trackObject.optString("BrisCode", this.brisCode));
                setBdsCode((bdsCode.isEmpty()) ? trackObject.optString("BrisCode", this.brisCode) : bdsCode);
                setTrackType(trackObject.optString("TrackType", this.trackType));
                setRace(Integer.toString(trackObject.optInt("RaceNum")));
                setMtp(Integer.toString(trackObject.optInt("Mtp")));
                setRaceStatus(trackObject.optString("RaceStatus"));
                setTrackStatus(trackObject.optString("Status"));
                setTrackCanceled(trackObject.optBoolean("TrackCanceled"));
                break;
            }
        }
        return this;
    }

    /**
     * TODO : May be deprecated
     * Static callabale method that determines if a provided track name should be a selectable option for the
     * current execution environment
     *
     * @param trackName the name of the track
     * @return whether the track is usable
     */
    public static boolean isSelectable(String trackName) {
        TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
        String trackNameClipped = trackName.toUpperCase();
        if (trackName.contains("(")) {
            trackNameClipped = trackNameClipped.replace("(H)", "").trim();
            trackNameClipped = trackNameClipped.replace("(G)", "").trim();
        }

        if (testEnv.getText().equalsIgnoreCase("ITE")
                || testEnv.getText().equalsIgnoreCase("STE")) {
            switch (trackNameClipped) {
                case "CAL-EXPO":
                case "EARLY LUCKITY 7":
                case "EARLY LUCKITY 8":
                case "EARLY LUCKITY 9":
                case "LATE LUCKITY 7":
                case "LATE LUCKITY 8":
                case "LATE LUCKITY 9":
                case "":
                    return false;
                default:
                    return true;
            }
        } else {
            return true;
        }
    }

    public String toString() {
        String output = "{ ";
        output += "[trackName:" + trackName + "] ";
        output += "[brisCode:" + brisCode + "] ";
        output += "[bdsCode:" + bdsCode + "] ";
        output += "[trackType:" + trackType + "] ";
        output += "[country:" + country + "] ";
        output += "[raceStatus:" + raceStatus + "] ";
        output += "[race:" + race + "] ";
        output += "[mtp:" + mtp + "] ";
        output += "[trackCanceled:" + trackCanceled + "] ";
        return output + "}";
    }

    public TrackData clone(TrackData copyFrom) {
        this.setTrackName(copyFrom.getTrackName());
        this.setBrisCode(copyFrom.getBrisCode());
        this.setBdsCode(copyFrom.getBdsCode());
        this.setTrackType(copyFrom.getTrackType());
        this.setCountry(copyFrom.getCountry());
        this.setRaceStatus(copyFrom.getRaceStatus());
        this.setRace(copyFrom.getRaceInt());
        this.setMtp(copyFrom.getMtpInt());
        this.setTrackCanceled(copyFrom.isTrackCanceled());
        return this;
    }
}