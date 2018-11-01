package com.twinspires.qa.core.webservices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WsSireStats extends AbstractWS {

    JSONObject fullResponse;
    JSONObject sireSummary;
    JSONArray offspringStats;

    public WsSireStats(String sireId) {
        getSireStats(sireId);
    }

    /**
     * "Callable constructor" to allow a new record to be retrieved, overwriting the previous values
     * @param sireId the ID of the sire who's data to retrieve
     * @return itself (WsSireStats object)
     */
    public WsSireStats getSireStats(String sireId) {
        clean(); // Clean up/initialize
        getSireInfo(sireId); // Make WS call
        buildObjects(); // Parse response
        return this;
    }

    /**
     * "Cleans" the object by setting all class variables to null
     */
    private void clean() {
        fullResponse = null;
        sireSummary = null;
        offspringStats = null;
    }

    /**
     * Performs the getsireinfo WS call
     */
    private void getSireInfo(String sireId) {
        String endpoint;
        JSONObject requestBody = new JSONObject();

        // Build endpoint
        endpoint = buildEndpoint("/webapi/Brisservices/getsireinfo");

        // Build request data
        requestBody.put("username", "my_tux");
        requestBody.put("output", "json");
        requestBody.put("brisId", sireId.replace("'", "").replace("\"", "").trim());

        // Sends the WS request
        this.sendRequest(REQ_METHOD_GET, endpoint, requestBody);
    }

    /**
     * Parses the WS response into logical JSONObject/Arrays for quick/easy data acquisition
     */
    private void buildObjects() {
        fullResponse = parseToJSONObject();
        sireSummary = fullResponse.getJSONObject("SireInfo").getJSONObject("sireSummary");
        offspringStats = fullResponse.getJSONObject("SireInfo").getJSONArray("offspringStats");
    }

    private String safeGetInt(JSONObject stats, String key) {
        return safeGetInt(stats, key, "-", false);
    }
    private String safeGetInt(JSONObject stats, String key, boolean includeCommas) {
        return safeGetInt(stats, key, "-", includeCommas);
    }
    private String safeGetInt(JSONObject stats, String key, String defaultValue, boolean includeCommas) {
        int val;
        try {
            val = stats.getInt(key);
            return (includeCommas) ? numberFormatCommas(val) : Integer.toString(val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String safeGetDouble(JSONObject stats, String key, String defaultValue, boolean roundTenths) {
        String responseVal;
        // Get WS Value, default to "-" if null
        try {
             responseVal = Double.toString(stats.getDouble(key));
             return (roundTenths) ? roundTenths(responseVal) : responseVal;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String safeGetString(JSONObject stats, String key) {
        return safeGetString(stats, key, "-", false);
    }
    private String safeGetString(JSONObject stats, String key, String defaultValue) {
        return safeGetString(stats, key, "-", false);
    }
    private String safeGetString(JSONObject stats, String key, String defaultValue, boolean includeCommas) {
        String val;
        try {
            val = stats.getString(key);
            return (includeCommas) ? numberFormatCommas(Integer.parseInt(val)) : val;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String roundTenths(String value) {
        double calculations;

        if(value.equalsIgnoreCase("-")) {
            return value;
        }

        // When not null, round percentage to 10ths
        calculations = Double.parseDouble(value);
        calculations = calculations * 10.0;
        calculations = Math.round(calculations) / 10.0;
        return Double.toString(calculations).replace(".0", "");
    }

    private String getRoundedPercent(JSONObject stats, String key) {
        String strValue;

        // Get value from response, default to "-" if null
        strValue = safeGetDouble(stats, key, "-", true);
        if(strValue.equalsIgnoreCase("-")) {
            return strValue;
        }

        return strValue.replace(".0", "") + "%";
    }

    private String numberFormatCommas(int number) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        return format.format(number);
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
                stats.add(safeGetString(categoryStats, "starts", "", true));
                stats.add(getRoundedPercent(categoryStats, "winPercent"));
                stats.add(safeGetString(categoryStats, "avg2ret", ""));
                stats.add(safeGetString(categoryStats, "wins", "", true));
                stats.add(safeGetString(categoryStats, "places", "", true));
                stats.add(safeGetString(categoryStats, "shows", "", true));
//                stats.add(safeCheck(categoryStats, "starters"));
//                stats.add(safeCheck(categoryStats, "winners"));
//                stats.add(safeCheck(categoryStats, "avgStartOdds"));
//                stats.add(safeCheck(categoryStats, "avgWinOdds"));
//                stats.add(safeCheck(categoryStats, "avgEarnPerStart"));
//                stats.add(safeCheck(categoryStats, "avgEarnPerWin"));
                stats.add("$" + safeGetInt(categoryStats, "earnings", "", true));
                return stats;
            }
        }
        return stats;
    }

    /*
     *      Sire Details
     */

    public String getBrisId() {
        return Integer.toString(sireSummary.getInt("brisId"));
    }
    public String getHorseName() {
        return sireSummary.getString("horseName");
    }
    public String getStudFee() {
        return safeGetInt(sireSummary,"studFee", false);
    }
    public String getPedigree() {
        String sire = sireSummary.getString("sireName");
        String dam = sireSummary.getString("damName");
        String damSire = sireSummary.getString("damSireName");
        return sire + " - " + dam + " by " + damSire;
    }
    public String getColorBreeder() {
        String color = sireSummary.optString("color", "");
        String sex = sireSummary.optString("sexDescription", "");
        String foalingDate = getFoalingDate("MMMMM d, yyyy");
        String breeder = sireSummary.optString("breeder", "");
        String whereBred = sireSummary.optString("whereBred", "");

        return ((color.isEmpty()) ? "" : (color + ", "))
                + ((sex.isEmpty()) ? "" : (sex + ", "))
                + foalingDate + ", "
                + ((breeder.isEmpty()) ? "" : breeder)
                + ((whereBred.isEmpty()) ? "" : (" (" + whereBred + ")"));
    }

    public String getFoalingDate() {
        return getFoalingDate("");
    }
    public String getFoalingDate(String formatPattern) {
        String strDate;
        SimpleDateFormat formatter;
        Date date;

        strDate = sireSummary.getString("foalingDate");
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

    /*
     *      Sire Stats
     */

    /**
     * Parses a Sire's record and returns the list of associated data
     * @return [0-Starts, 1-Wins, 2-Places, 3-Shows, 4-Earnings]
     */
    public List<String> getLifeRecord() {
        List<String> parsedRecord = new ArrayList<>();
        String record;
        int idxEnd;

        record = sireSummary.getString("sireRaceRecord");
        record = record.replace("(", "").trim();

        // [0] Starts
        idxEnd = record.indexOf("sts");
        parsedRecord.add(record.substring(0, idxEnd));
        record = record.substring(idxEnd+4);

        // [1] Wins
        idxEnd = record.indexOf("w");
        parsedRecord.add(record.substring(0, idxEnd));
        record = record.substring(idxEnd+2);

        // [2] Places
        idxEnd = record.indexOf("p");
        parsedRecord.add(record.substring(0, idxEnd));
        record = record.substring(idxEnd+2);

        // [3] Shows
        idxEnd = record.indexOf("s");
        parsedRecord.add(record.substring(0, idxEnd));
        record = record.substring(idxEnd+2);

        // [4] Earnings
        idxEnd = record.indexOf(")");
        parsedRecord.add(record.substring(0, idxEnd));

        return parsedRecord;
    }

    /*
     *      Sire's Offspring Summary
     */

    public String getSummaryCrops() {
        return safeGetInt(sireSummary,"summaryCrops", true);
    }
    public String getSummaryFoals() {
        return safeGetInt(sireSummary, "summaryFoals", true);
    }
    public String getSummaryStarters() {
        String num = safeGetInt(sireSummary, "summaryStarters", true);
        String per = getRoundedPercent(sireSummary, "percentStarters");
        return (num.equalsIgnoreCase("-")) ? num : (num + " (" + per + ")");
    }
    public String getSummaryWinners() {
        String num = safeGetInt(sireSummary, "summaryWinners", true);
        String per = getRoundedPercent(sireSummary, "percentWinners");
        return (num.equalsIgnoreCase("-")) ? num : (num + " (" + per + ")");
    }
    public String getSummary2yoWinners() {
        return getRoundedPercent(sireSummary, "percent2yoWinr");
    }

    public String getSummaryFirstWinners() {
        return getRoundedPercent(sireSummary, "percent1stWinr");
    }
    public String getSummaryMudWins() {
        return getRoundedPercent(sireSummary, "percentMudStrWin");
    }
    public String getSummaryTurfWins() {
        return getRoundedPercent(sireSummary, "percentTurfWin");
    }
    public String getSummaryFirstTurf() {
        return getRoundedPercent(sireSummary, "percent1stTurf");
    }
    public String getSummaryTurfWinners() {
        return safeGetInt(sireSummary, "summaryTurfWinrs", true);
    }

    public String getSummaryAWWinners() {
        return safeGetInt(sireSummary, "summaryAwWinrs", true);
    }
    public String getSummaryDirtWinners() {
        return safeGetInt(sireSummary, "summaryDirtWinrs", true);
    }
    public String getSummarySW() {
        String num = safeGetInt(sireSummary, "summaryStakesWinsCount", true);
        String per = getRoundedPercent(sireSummary, "percentStakesWinners");
        return (num.equalsIgnoreCase("-")) ? num : (num + " (" + per + ")");
    }
    public String getSummaryAvgWinDist() {
        return safeGetDouble(sireSummary, "avgWinDist", "-", true);
    }
    public String getSummarySPI() {
        return safeGetDouble(sireSummary, "spi", "-", true);
    }

    /*
     *      Offspring Stats
     */

    public List<String> getOffspringStatsTotalStarts() {
        return getOffspringStats("Total Starts");
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
        return getOffspringStats("4yo-and-up");
    }
    public List<String> getOffspringStatsFirstTimeStarters() {
        return getOffspringStats("First-time starters");
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