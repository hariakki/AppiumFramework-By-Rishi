package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.util.Util;
import org.json.JSONObject;

public class WsRegistration extends WebserviceCalls {
    private JSONObject getNewCamRegRequest(Affiliate aff, String registerId){

        JSONObject regRequest = new JSONObject();
        String userName = Util.randomUsername();
        String email = Util.randomEmail();
        String affId = aff.getAffId();
        String dob = Util.randomYYYY() + "-" + Util.randomMM() + "-" + Util.randomDD();
        String ssn;

        if (affiliate.toString().equals("TWINSPIRES")) {
             ssn = Util.randomSSN3();
        } else {
            ssn = Util.randomSSN1() + Util.randomSSN2() + Util.randomSSN3();
        }

        regRequest.put("username", "iphone");
        regRequest.put("password", "ru13juhyo");
        regRequest.put("ip", "10.20.2.248");
        regRequest.put("affid", affId);
        regRequest.put("affiliateId", affId);
        regRequest.put("output", "json");
        regRequest.put("email", email);
        regRequest.put("firstname", "Test");
        regRequest.put("lastname", "Bris");
        regRequest.put("addr1", "123 Some Street");
        regRequest.put("zip", "01776");
        regRequest.put("city", "Lexington");
        regRequest.put("needsFormerAddress", "no");
        regRequest.put("dayphone", "5023537586");
        regRequest.put("dob", dob);
        regRequest.put("ssn", ssn);
        regRequest.put("cam_username", userName);
        regRequest.put("cam_password", "password1");
        regRequest.put("register_id", registerId);
        regRequest.put("platform", "Mobile");
        regRequest.put("State", "KY");

        return regRequest;
    }

    public String getNewCamUserName(Affiliate aff, TestEnv env)  {

        String endPoint = "";
        String host = aff.toString().toLowerCase();

        JSONObject request = getNewCamRegRequest(aff, getRegisterId(aff, env));

        if(env.equals(TestEnv.ITE))
            endPoint = "https://mi." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/register";
        else if (env.equals(TestEnv.STE))
            endPoint = "https://ms." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/register";
        else if (env.equals(TestEnv.LOAD))
            endPoint = "https://mt." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/register";

        sendRequest("POST", "application/json", endPoint, request);

        return request.get("cam_username").toString();
    }

    private String getRegisterId(Affiliate aff, TestEnv env) {

        String endPoint = "";
        String affId = aff.getAffId();
        String host = aff.toString().toLowerCase();
        JSONObject request = new JSONObject();
        request.put("username", "iphone");
        request.put("password", "ru13juhyo");
        request.put("ip", "10.20.2.248");
        request.put("affid", affId);
        request.put("affiliateId", affId);
        request.put("output", "json");
        request.put("email", "timspiresqa123@" + host + ".com");

        if(env.equals(TestEnv.ITE))
            endPoint = "https://mi." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/upsert";
        else if (env.equals(TestEnv.STE))
            endPoint = "https://ms." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/upsert";
        else if (env.equals(TestEnv.LOAD))
            endPoint = "https://mt." + host + ".com/php/fw/php_BRIS_BatchAPI/2.3/Registration/upsert";

        sendRequest("POST", "application/json", endPoint, request);

        return (String) parseToJSONObject(lastResponseBody).getJSONObject("Response").get("register_id");
    }
}