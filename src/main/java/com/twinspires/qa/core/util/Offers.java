package com.twinspires.qa.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.SQLQueries;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import com.twinspires.qa.core.sqlqueries.VIPREQueries;
import com.twinspires.qa.core.webservices.WebserviceCalls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Offers {

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    SQLQueries sqlQueries = new SQLQueries();
    TestDataQueries testDataQueries = new TestDataQueries();
    VIPREQueries vipreQueries = new VIPREQueries();
    public String bonusId;
    public String promoId;
    public String redemptionCode;
    public String bonusName;
    public String promoName = "AutoTestPromo" + sdf.format(cal.getTime());
    public String depositAmount;
    public String depositType = "oad";
    public String resource;
    public String parameters;
    public String urlUuidCode;
    public String offerEndDate;
    public Long dateExpMillis = getTodaysDateExpMillis();
    public String rank = "0";
    private String testEnv;
    private String adapterEndpoint;
    private String vipEndpoint;
    private String bonusEndpoint;

    public Offers() {
    }

    public Offers(TestEnv testEnv) {
        redemptionCode = getRedemptionCode();
        this.testEnv = testEnv.toString();
    }

    private void deleteBonus(){
        vipreQueries.deleteBonus(bonusId);
    }

    private void deletePromo(){
        vipreQueries.deletePromo(promoId);
    }

    public void deleteOffer(){
        if(bonusId != null )
            deleteBonus();
        if(promoId != null)
            deletePromo();
    }

    public String getRedemptionCode(){
        Random rand = new Random();
        int ran = rand.nextInt((1995555 - 1935) + 1) + 1935;
        return "red" + ran;
    }

    public long getTodaysDateMillis() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String strDate = sdfDate.format(new Date());
        Date date = null;
        try {
            date =sdfDate.parse(strDate);
        } catch (ParseException e) {e.printStackTrace();}
        long millis = date.getTime();
        return millis;
    }

    public long getTodaysDateExpMillis() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd 23:59:00");
        SimpleDateFormat sdfDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdfDate.format(new Date());
        Date date = null;
        try {
            date =sdfDate2.parse(strDate);
        } catch (ParseException e) {e.printStackTrace();}
        long millis = date.getTime();
        return millis;
    }

    public long getTimeStampMillis() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdfDate.format(new Date());
        Date date = null;
        try {
            date =sdfDate.parse(strDate);
        } catch (ParseException e) {e.printStackTrace();}
        long millis = date.getTime();
        return millis;
    }

    private String activatePromo(String promoId, String redemptionCode){
        JSONObject request = new JSONObject();
        request.put("idVipCampaign", promoId);
        request.put("code", redemptionCode);
        JSONObject response = WebserviceCalls.postEndpointJSONResponse(adapterEndpoint + "adapter/boss/vip/campaign/activate", request);
    //    urlUuidCode = response.get("urlUuidCode").toString();

        try {
            return response.getJSONArray("messages").get(0).toString();
        } catch (JSONException e) {
            return response.getJSONArray("errors").get(0).toString();
        }
    }

    private String activateBonus(String bonusId, String redemptionCode) {
        JSONObject request = new JSONObject();
        request.put("idBonus", bonusId);
        request.put("code", redemptionCode);
        JSONObject response = WebserviceCalls.postEndpointJSONResponse(adapterEndpoint + "adapter/boss/bonus/activate", request);
     //   urlUuidCode = response.get("bonusOfferCodeAndUuidUrl").toString();

        try {
            return response.getJSONArray("messages").get(0).toString();
        } catch (JSONException e) {
            return response.getJSONArray("errors").get(0).toString();
        }
    }

    public void createPromo(){
        vipEndpoint = testDataQueries.getServiceEndpoint(testEnv,"vip");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupPromo = getSetupPromoJson();
        JSONObject response = WebserviceCalls.postEndpointJSONResponse(vipEndpoint + "vip-services/vip/campaign/save", setupPromo);
        promoId = response.get("idVipCampaign").toString();
        promoName = response.get("displayVipCampaign").toString();
        offerEndDate = response.get("timeStampEndMillis").toString();
        System.out.println("PromoId: " + promoId);
        System.out.println(activatePromo(promoId,redemptionCode));
    }

    public void createCashDrop(){
        bonusName = getBonusName("AutoTestCD");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusCreditSchedule", "adv");
        setupBonus.put("idPlaythroughType", "ipa");
        setupBonus.put("bonusFixedAmount", 35);
        setupBonus.put("bonusCreditScheduleText", "credit bonus balance in advance, subject to clawback");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createSignUpCashDrop(){
        bonusName = getBonusName("AutoTestSUCD");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusTemplate", "suc");
        setupBonus.put("idBonusType", "fix");
        setupBonus.put("idBonusCreditSchedule", "adv");
        setupBonus.put("idPlaythroughType", "ipa");
        setupBonus.put("bonusFixedAmount", 35);
        setupBonus.put("bonusCreditScheduleText", "credit bonus balance in advance, subject to clawback");
        setupBonus.put("registrationBonus", true);
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(true);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createDepositNow() {
        bonusName = getBonusName("AutoTestDN");
        depositAmount = "10"; //In some case, this is the min and max allowed on p10
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusTrigger", "dep");
        setupBonus.put("idBonusIssueMode", "voi");
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("bonusPercentage", 40);
        setupBonus.put("idBonusTemplate", "doi");
        setupBonus.put("idBonusType", "per");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        setupRedemption.put("minimumAmount", depositAmount);
        setupRedemption.put("idBonusTrigger", "dep");
        setupRedemption.put("idBonusAssignmentMode", depositType);
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createDepositNowWithPlaythrough() {
        bonusName = getBonusName("AutoTestDNPT");
        depositAmount = "15";
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusTrigger", "dep");
        setupBonus.put("idBonusIssueMode", "voi");
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("bonusPercentage", 40);
        setupBonus.put("idBonusTemplate", "doi");
        setupBonus.put("idBonusType", "per");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        setupRedemption.put("minimumAmount", depositAmount);
        setupRedemption.put("idBonusTrigger", "dep");
        setupRedemption.put("idBonusAssignmentMode", depositType);
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getPlaythroughJson();
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createCashdropWithPlaythrough() {
        bonusName = getBonusName("AutoTestCDPT");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusCreditSchedule", "adv");
        setupBonus.put("idPlaythroughType", "ipa");
        setupBonus.put("bonusFixedAmount", 35);
        setupBonus.put("bonusCreditScheduleText", "credit bonus balance in advance, subject to clawback");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getPlaythroughJson();
        setupPlaythrough.put("idBonusCreditSchedule", "adv");
        setupPlaythrough.put("autoOptOutOnWithdraw", true);
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }


    public void createNonDeposit(){
        bonusName = getBonusName("AutoTestND");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("bonusFixedAmount", "20");
        setupBonus.put("idBonusTemplate", "dno");
        setupBonus.put("idBonusType", "fix");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        offerEndDate = setupResponse.get("timeStampEnd").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        setupEligibility.put("idBonusIssueMode", "voi");
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        setupPlaythrough.put("idBonusCreditSchedule", "voi");
        setupPlaythrough.put("autoOptOutOnWithdraw", false);
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createNonDepositWithPlaythrough(){
        bonusName = getBonusName("AutoTestND");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("idPlaythroughType", "ipa");
        setupBonus.put("bonusFixedAmount", "20");
        setupBonus.put("idBonusTemplate", "dno");
        setupBonus.put("idBonusType", "fix");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        setupEligibility.put("idBonusIssueMode", "voi");
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getPlaythroughJson();
        setupPlaythrough.put("idBonusCreditSchedule", "voi");
        setupPlaythrough.put("autoOptOutOnWithdraw", false);
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }


    public void createDepositCashDrop(){
        bonusName = getBonusName("AutoTestDCD");
        depositAmount = "15";
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusTrigger", "dep");
        setupBonus.put("idBonusCreditSchedule", "adv");
        setupBonus.put("bonusPercentage", "50");
        setupBonus.put("idBonusTemplate", "dcd");
        setupBonus.put("allowOptIn", true);
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(false);
        setupEligibility.put("idBonusTrigger", "dep");
        setupEligibility.put("idBonusIssueMode", "voi");
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        setupRedemption.put("idBonusTrigger", "dep");
        setupRedemption.put("minimumAmount", depositAmount);
        setupRedemption.put("maximumPayout", "50");
        setupRedemption.put("idBonusAssignmentMode", depositType);
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        setupPlaythrough.put("idBonusAssignmentMode", depositType);
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));
    }

    public void createSignUpNonDeposit(){
        bonusName = getBonusName("AutoTestSUND");
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("bonusFixedAmount", "20");
        setupBonus.put("idBonusTemplate", "sun");
        setupBonus.put("idBonusType", "fix");
        setupBonus.put("registrationBonus", true);
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(true);
        setupEligibility.put("idBonusIssueMode", "voi");
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        setupPlaythrough.put("idBonusCreditSchedule", "voi");
        setupPlaythrough.put("autoOptOutOnWithdraw", false);
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));

    }

    public void createSignUpDeposit(){
        bonusName = getBonusName("AutoTestSUD");
        depositAmount = "15";
        bonusEndpoint = testDataQueries.getServiceEndpoint(testEnv,"bonus");
        adapterEndpoint = testDataQueries.getServiceEndpoint(testEnv,"adapter");
        JSONObject setupBonus = getBonusSetupJson();
        setupBonus.put("idBonusTrigger", "dep");
        setupBonus.put("idBonusIssueMode", "voi");
        setupBonus.put("clawbackFundsOnExpiration", false);
        setupBonus.put("bonusPercentage", 40);
        setupBonus.put("idBonusTemplate", "sud");
        setupBonus.put("idBonusType", "per");
        JSONObject setupResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupBonus);
        bonusId = setupResponse.get("idBonus").toString();
        System.out.println("BonusId: " + bonusId);
        JSONObject setupEligibility = getPlayerEligibilityJson(true);
        JSONObject eligibilityResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupEligibility);
        JSONObject setupRedemption = getBonusAssignmentJson();
        setupRedemption.put("minimumAmount", depositAmount);
        setupRedemption.put("idBonusTrigger", "dep");
        setupRedemption.put("idBonusAssignmentMode", depositType);
        JSONObject redemptionResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupRedemption);
        JSONObject setupPlaythrough = getNoPlaythroughJson();
        JSONObject playthroughResponse = WebserviceCalls.postEndpointJSONResponse(bonusEndpoint + "bonus-services/bonus/save", setupPlaythrough);
        System.out.println(activateBonus(bonusId, redemptionCode));

    }

    private JSONObject getSetupPromoJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vipCampaignExpires", true);
        jsonObject.put("vipCampaign", promoName);
        jsonObject.put("displayVipCampaign", promoName);
        jsonObject.put("redemptionCode", redemptionCode);
        jsonObject.put("description", "<p>Automation Test</p>");
        jsonObject.put("agreement", "<p>Automation Test</p>");
        jsonObject.put("idSkin", 2);
        jsonObject.put("displayTimeStampStart", getTodaysDateMillis());
        jsonObject.put("timeStampStart", getTodaysDateMillis());
        jsonObject.put("timeStampEnd", dateExpMillis);
        jsonObject.put("glCode", "5930.56330");
        jsonObject.put("glCodeDescription", "Twinspires.com Co              - ADW Marketing - Promotions-general");
        jsonObject.put("idDayOfMonth", "voi");
        jsonObject.put("recurEveryFrequency", 1);
        jsonObject.put("step", 1);
        jsonObject.put("idSite", "hra");
        return jsonObject;
    }

    private JSONObject getBonusSetupJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stackIndex", rank);
        jsonObject.put("idSite", "hra");
        jsonObject.put("idSkin", "2");
        jsonObject.put("shortDescription", bonusName);
        jsonObject.put("bonus", bonusName);
        jsonObject.put("description", "<p>Automation Test</p>");
        jsonObject.put("idBonusTrigger", "cod");
        jsonObject.put("idBonusIssueMode", "cod");
        jsonObject.put("timeStampStart", getTodaysDateMillis());
        jsonObject.put("timeStampEnd", dateExpMillis);
        jsonObject.put("playThroughWagerDaily", false);
        jsonObject.put("idBonusStatus", "hel");
        jsonObject.put("autoOptOutOnWithdraw", true);
        jsonObject.put("maximumNumberAssignment", 1);
        jsonObject.put("maximumNumberRedemption", 1);
        jsonObject.put("clawbackFundsOnExpiration", true);
        jsonObject.put("agreement", "<p>Automation Test</p>");
        jsonObject.put("skinName", "twinspires");
        jsonObject.put("bonusExpires", true);
        jsonObject.put("bonusAssignmentModeText", "void");
        jsonObject.put("bonusIssueModeText", "bonus code");
        jsonObject.put("playthroughTypeText", "void");
        jsonObject.put("playThroughTimeStampStart", getTodaysDateMillis());
        jsonObject.put("playThroughTimeStampEnd", dateExpMillis);
        jsonObject.put("timeStamp", getTimeStampMillis());
        jsonObject.put("idBonusTemplate", "cdr");
        jsonObject.put("horseRacingPlaythroughInclusion", false);
        jsonObject.put("validateBonus", false);
        jsonObject.put("allowOptIn", false);
        jsonObject.put("glCode", "5930.56330");
        jsonObject.put("glCodeDescription", "Twinspires.com Co              - ADW Marketing - Promotions-general");
        jsonObject.put("imageRetrievalUrl", "https://"+ testEnv.toLowerCase() + ".twinspires.com/assets/offers/");
        jsonObject.put("timeZoneSetting", "PST");
        jsonObject.put("hidden", false);
        jsonObject.put("displayNew", true);
        jsonObject.put("registrationBonus", false);
        jsonObject.put("step", 1);
        return jsonObject;
    }

    private JSONObject getPlayerEligibilityJson(boolean isRegistrationBonus){
        JSONArray bonusEligibilityParameters = new JSONArray();
        if(isRegistrationBonus){
            JSONObject parameters = new JSONObject();
            parameters.put("idBonusEligibilityCategory", 4);
            parameters.put("valueStart", "New User");
            parameters.put("valueEnd", "New User");
            parameters.put("inclusion",true);
            bonusEligibilityParameters.put(parameters);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idBonus", bonusId);
        jsonObject.put("idSkin", "2");
        jsonObject.put("idSite", "hra");
        jsonObject.put("idBonusTrigger", "cod");
        jsonObject.put("idBonusIssueMode", "cod");
        jsonObject.put("bonusEligibilityParameters", bonusEligibilityParameters);
        jsonObject.put("step", 2);
        return jsonObject;
    }

    private JSONObject getBonusAssignmentJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idBonus", bonusId);
        jsonObject.put("idSkin", "2");
        jsonObject.put("idSite", "hra");
        jsonObject.put("idBonusTrigger", "cod");
        jsonObject.put("idBonusIssueMode", "cod");
        jsonObject.put("redemptionCode", redemptionCode);
        jsonObject.put("maximumNumberAssignment", 10);
        jsonObject.put("maximumNumberRedemption", 10);
        jsonObject.put("step", 3);
        return jsonObject;
    }

    private  JSONObject getNoPlaythroughJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idBonus", bonusId);
        jsonObject.put("idSkin", "2");
        jsonObject.put("idSite", "hra");
        jsonObject.put("idBonusStatus", "hel");
        jsonObject.put("idBonusAssignmentMode", "voi");
        jsonObject.put("idBonusCreditSchedule", "adv");
        jsonObject.put("idPlaythroughType", "ipa");
        jsonObject.put("autoOptOutOnWithdraw", true);
        jsonObject.put("step", 4);
        return jsonObject;
    }

    private JSONObject getPlaythroughJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idBonus", bonusId);
        jsonObject.put("idSkin", "2");
        jsonObject.put("idSite", "hra");
        jsonObject.put("idBonusStatus", "hel");
        jsonObject.put("idBonusAssignmentMode", "voi");
        jsonObject.put("idBonusCreditSchedule", "asg");
        jsonObject.put("idPlaythroughType", "wac");
        jsonObject.put("autoOptOutOnWithdraw", false);
        jsonObject.put("playThroughTimeStampStart", getTodaysDateMillis());
        jsonObject.put("playThroughTimeStampEnd", dateExpMillis);
        jsonObject.put("playthroughCalculationType", "fix");
        jsonObject.put("playthroughFixedValue", "10");
        jsonObject.put("step", 4);
        return jsonObject;
    }

    private String getBonusName(String name) {
        if (bonusName == null)
            return name + sdf.format(cal.getTime());
        else return bonusName;
    }
}