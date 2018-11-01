package com.twinspires.qa.core.webservices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WsDamStats extends AbstractWS {
    JSONObject fullResponse;
    JSONObject damSummary;
    JSONObject damRecord;
    JSONObject offspringSummary;
    JSONObject offspringRecord;
    JSONArray offspringStats;

    public WsDamStats(String damId) {
        getDamStats(damId);
    }

    /**
     * "Callable constructor" to allow a new record to be retrieved, overwriting the previous values
     * @param damId the ID of the dam who's data to retrieve
     * @return itself (WsDamStats object)
     */
    public WsDamStats getDamStats(String damId) {
        clean(); // Clean up/initialize
        getDamInfo(damId); // Make WS call
        buildObjects(); // Parse response
        return this;
    }

    /**
     * "Cleans" the object by setting all class variables to null
     */
    private void clean() {
        fullResponse = null;
        damSummary = null;
        damRecord = null;
        offspringSummary = null;
        offspringRecord = null;
        offspringStats = null;
    }

    /**
     * Performs the getdaminfo WS call
     */
    private void getDamInfo(String damId) {
        String endpoint;
        JSONObject requestBody = new JSONObject();

        // Build endpoint
        endpoint = buildEndpoint("/webapi/Brisservices/getdaminfo");

        // Build request data
        requestBody.put("username", "my_tux");
        requestBody.put("output", "json");
        requestBody.put("brisId", damId.replace("'", "").replace("\"", "").trim());

        // Sends the WS request
        this.sendRequest(REQ_METHOD_GET, endpoint, requestBody);
    }

    /**
     * Parses the WS response into logical JSONObject/Arrays for quick/easy data acquisition
     */
    private void buildObjects() {
        fullResponse = parseToJSONObject();
        offspringStats = fullResponse.getJSONObject("DamInfo").getJSONArray("bdsDamOffspringStats");
        damSummary = fullResponse.getJSONObject("DamInfo").getJSONObject("bdsDamSummary");
        damRecord = damSummary.getJSONObject("bdsDamRecord");
        offspringSummary = damSummary.getJSONObject("bdsDamOffspringSummary");
        offspringRecord = damSummary.getJSONObject("bdsOffspringRecord");
    }

    /**
     * Parses a Dam/Offspring record and returns the list of associated data
     * @param record
     * @return [0-Starts, 1-Wins, 2-Places, 3-Shows, 4-Earnings]
     */
    private List<String> parseRecord(String record) {
        List<String> parsedRecord = new ArrayList<>();
        String subRecord = "";
        int idxEnd;

        subRecord = record.replace("(", "").trim();

        // [0] Starts
        idxEnd = subRecord.indexOf("sts");
        parsedRecord.add(subRecord.substring(0, idxEnd));
        subRecord = subRecord.substring(idxEnd+4);

        // [1] Wins
        idxEnd = subRecord.indexOf("w");
            parsedRecord.add(subRecord.substring(0, idxEnd));
        subRecord = subRecord.substring(idxEnd+2);

        // [2] Places
        idxEnd = subRecord.indexOf("p");
            parsedRecord.add(subRecord.substring(0, idxEnd));
        subRecord = subRecord.substring(idxEnd+2);

        // [3] Shows
        idxEnd = subRecord.indexOf("s");
            parsedRecord.add(subRecord.substring(0, idxEnd));
        subRecord = subRecord.substring(idxEnd+2);

        // [4] Earnings
        idxEnd = subRecord.indexOf(")");
        parsedRecord.add(subRecord.substring(0, idxEnd));

        return parsedRecord;
    }

    /**
     * Captures data from the bdsDamOffspringStats json array and parses it into a String list matching
     * the order
     * @param categoryName the stats category
     * @return list of data matching the order which is expected on the front-end Dam details page
     */
    private List<String> getOffspringStats(String categoryName) {
        JSONObject categoryStats;
        List<String> stats = new ArrayList<>();
        String temp;
        for(int i = 0; i < offspringStats.length(); i++) {
            if(offspringStats.getJSONObject(i).getString("categoryName").equalsIgnoreCase(categoryName)) {
                categoryStats = offspringStats.getJSONObject(i);
                stats.add(safeCheck(categoryStats, "starts"));
                stats.add(round(safeCheck(categoryStats, "winPercent")));
                stats.add(safeCheck(categoryStats, "avg2ret"));
                stats.add(safeCheck(categoryStats, "wins"));
                stats.add(safeCheck(categoryStats, "places"));
                stats.add(safeCheck(categoryStats, "shows"));
                stats.add(safeCheck(categoryStats, "starters"));
                stats.add(safeCheck(categoryStats, "winners"));
//                stats.add(safeCheck(categoryStats, "avgStartOdds"));
//                stats.add(safeCheck(categoryStats, "avgWinOdds"));
//                stats.add(safeCheck(categoryStats, "avgEarnPerStart"));
//                stats.add(safeCheck(categoryStats, "avgEarnPerWin"));
//                stats.add(categoryStats.getInt("earnings"));
                return stats;
            }
        }
        return stats;
    }

    /**
     * safe get string from json response.  if null, returns ""
     * @param stats
     * @param key
     * @return the String value of the JSONObject's key
     */
    private String safeCheck(JSONObject stats, String key) {
        try {
            return stats.getString(key);
        } catch (Exception e) {
            // TODO : Workaround for DE11029
//            return "-";
            return "";
        }
    }

    private String round(String num) {
        String strValue;
        double doub;
        if(num.isEmpty()) return num;
        doub = Double.parseDouble(num) * 10.0;
        doub = Math.round(doub) / 10.0;
        strValue = Double.toString(doub);
        strValue = strValue.replace(".0", "") + "%";
        return strValue;
    }

    public String getRequestUTS() {
        return fullResponse.getString("UTS");
    }

    // Dam Summary
    public String getBrisId() {
        return damSummary.getString("brisId");
    }
    public String getHorseName() {
        return damSummary.getString("horseName");
    }
    public String getSireName() {
        return damSummary.getString("sireName");
    }
    public String getColor() {
        return damSummary.getString("color");
    }
    public String getSex() {
        return damSummary.getString("sexDescription");
    }
    public String getFoalingDate() {
        return getFoalingDate("");
    }
    public String getFoalingDate(String formatPattern) {
        String strDate;
        SimpleDateFormat formatter;
        Date date;

        strDate = damSummary.getString("foalingDate");
        if(!formatPattern.isEmpty()) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
                formatter = new SimpleDateFormat(formatPattern);
                return formatter.format(date);
            } catch (Exception e) {
                System.out.println("Error formatting 'Dam Foaling Date':" + e.getMessage());
            }
        }
        return strDate;
    }
    public String getBreeder() {
        return damSummary.getString("breeder");
    }
    public String getWhereBred() {
        return damSummary.getString("whereBred");
    }

    // Dam Record
    public String getRacePerformance() {
        return damRecord.getString("racePerformance");
    }
    public List<String> getCareer() {
        return parseRecord(damRecord.getString("careerRecord"));
    }
    public List<String> get2yo() {
        return parseRecord(damRecord.getString("twoYoRecord"));
    }
    public List<String> get3yo() {
        return parseRecord(damRecord.getString("threeYoRecord"));
    }
    public List<String> getSprints() {
        return parseRecord(damRecord.getString("sprints"));
    }
    public List<String> getRoutes() {
        return parseRecord(damRecord.getString("routes"));
    }
    public List<String> getTurf() {
        return parseRecord(damRecord.getString("turf"));
    }
    public List<String> getAllWeather() {
        return parseRecord(damRecord.getString("allWeather"));
    }
    public List<String> getWetTacks() {
        return parseRecord(damRecord.getString("wetTracks"));
    }
    public String getFirstStart() {
        String modFirstStart;
        int idxNewLine;

        modFirstStart = damRecord.optString("firstStart", "");
        if(modFirstStart.isEmpty()) return modFirstStart;

        idxNewLine = modFirstStart.indexOf("(");
        return modFirstStart.substring(0, idxNewLine) + "\n" + modFirstStart.substring(idxNewLine);
    }

    // Offspring Summary
    public String getOffspringSummaryFoals() {
        return Integer.toString(offspringSummary.getInt("foals"));
    }
    public String getOffspringSummaryWinnersStarters() {
        String output = "";
        String[] stats = offspringSummary.getString("starterWinnerStakeWinner").split("\\|");
        // [0-starter | 1-Winner | 2-Stakes Winner]
        output = stats[1] + "/" + stats[0];
        output = (output.equalsIgnoreCase("0/0")) ? "-" : output;
        return output;
    }
    public String getOffspringSummaryStakesWinners() {
        // [0-starter | 1-Winner | 2-Stakes Winner]
        return offspringSummary.getString("starterWinnerStakeWinner").split("\\|")[2];
    }
    public String getOffspringSummary2yoWinners() {
        return offspringSummary.getInt("twoYearWinner") + "%";
    }
    public String getOffspringSummaryTurfWinnersStarters() {
        String output = "";
        output = offspringSummary.getInt("turfWinners") + "/" + offspringSummary.getInt("turfStarters");
        output = (output.equalsIgnoreCase("0/0")) ? "-" : output;
        return output;
    }
    public String getOffspringSummaryDirtWinnersStarters() {
        String output = "";
        output = offspringSummary.getInt("dirtWinners") + "/" + offspringSummary.getInt("dirtStarters");
        output = (output.equalsIgnoreCase("0/0")) ? "-" : output;
        return output;
    }
    public String getOffspringSummaryAWWinnersStarters() {
        String output = "";
        output = offspringSummary.getInt("awWinners") + "/" + offspringSummary.getInt("awStarters");
        output = (output.equalsIgnoreCase("0/0")) ? "-" : output;
        return output;
    }
    public String getOffspringSummaryAWD() {
        return Double.toString(offspringSummary.getDouble("awd"));
    }
    public String getOffspringSummaryDPI() {
        return Double.toString(offspringSummary.getDouble("dpi"));
    }

    // Offspring Record
    public List<String> getOffspringRecordCareerPerformance() {
        return parseRecord(offspringRecord.getString("careerPerformance"));
    }
    public List<String> getOffspringRecord2yo() {
        return parseRecord(offspringRecord.getString("twoYoRecord"));
    }
    public List<String> getOffspringRecord3yo() {
        return parseRecord(offspringRecord.getString("threeYoRecord"));
    }
    public List<String> getOffspringRecordSprints() {
        return parseRecord(offspringRecord.getString("sprints"));
    }
    public List<String> getOffspringRecordRoutes() {
        return parseRecord(offspringRecord.getString("routes"));
    }
    public List<String> getOffspringRecordTurf() {
        return parseRecord(offspringRecord.getString("turf"));
    }
    public List<String> getOffspringRecordallWeather() {
        return parseRecord(offspringRecord.getString("allWeather"));
    }
    public List<String> getOffspringRecordWetTracks() {
        return parseRecord(offspringRecord.getString("wetTracks"));
    }

    // Offspring Stats
    public List<String> getOffspringStatsTotalStarts() {
        return getOffspringStats("Total starts");
    }
    public List<String> getOffspringStatsDirt() {
        return getOffspringStats("Dirt");
    }
    public List<String> getOffspringStatsTurf() {
        return getOffspringStats("Turf");
    }
    public List<String> getOffspringStatsAllWeather() {
        return getOffspringStats("All-Weather");
    }
    public List<String> getOffspringStatsDirtSprints() {
        return getOffspringStats("Dirt Sprints");
    }
    public List<String> getOffspringStatsDirtRoutes() {
        return getOffspringStats("Dirt Routes");
    }
    public List<String> getOffspringStatsTurfSprints() {
        return getOffspringStats("Turf Sprints");
    }
    public List<String> getOffspringStatsTurfRoutes() {
        return getOffspringStats("Turf Routes");
    }
    public List<String> getOffspringStatsAWSprints() {
        return getOffspringStats("AW Sprints");
    }
    public List<String> getOffspringStatsAWRoutes() {
        return getOffspringStats("AW Routes");
    }
    public List<String> getOffspringStatsWetTracks() {
        return getOffspringStats("Wet Tracks");
    }
    public List<String> getOffspringStatsFilliesMares() {
        return getOffspringStats("Fillies/Mares");
    }
    public List<String> getOffspringStatsColtsGeldings() {
        return getOffspringStats("Colts/Geldings");
    }
    public List<String> getOffspringStats2yo() {
        return getOffspringStats("2-year-olds");
    }
    public List<String> getOffspringStats3yo() {
        return getOffspringStats("3-year-olds");
    }
    public List<String> getOffspringStats4yoUp() {
        return getOffspringStats("4 yo-and-up");
    }
    public List<String> getOffspringStatsFirstTimeStarters() {
        return getOffspringStats("First-time Starters");
    }
    public List<String> getOffspringStatsGradedStakes() {
        return getOffspringStats("Graded Stakes");
    }
    public List<String> getOffspringStatsAllowanceRaces() {
        return getOffspringStats("Allowance Races");
    }
    public List<String> getOffspringStatsClaimingRaces() {
        return getOffspringStats("Claiming Races");
    }
    public List<String> getOffspringStatsMaidenRaces() {
        return getOffspringStats("Maiden Races");
    }
}
