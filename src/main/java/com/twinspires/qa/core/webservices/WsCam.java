package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.TestDataQueries;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dalwinder.singh on 4/6/18.
 */
public class WsCam extends WebserviceCalls {

    private String testEnv;
    private String camEndpoint;
    private String resource;
    private String parameters;
    TestDataQueries testDataQueries = new TestDataQueries();

    public WsCam(TestEnv testEnv) {
        this.testEnv = testEnv.toString();
    }

    /**
     * This methods disable a particular customer by setting customer status to Management Disabled
     * @param camId - account to disable
     * @throws IOException
     */
    public void disableTestAccount(String camId) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        String url = camEndpoint + "cam/service/Customer/UpdateChannel";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String affId = affiliate.getAffId().toString();
        String channelID = affiliate.getChannelId().toString();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"affiliateId\": " + affId + ",\"customerChannel\": {\"channelID\": " + channelID + ",\"statusID\": 3, \"comment1\":" +
                " \"Automation Disabled\"},\"camID\":"+camId+"}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + postJsonData);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
    }

    /**
     * This methods enable a particular customer by setting customer status to
     * @param camId - account to enable
     * @throws IOException
     */
    public void enableTestAccount(String camId) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        String url = camEndpoint + "cam/service/Customer/UpdateChannel";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String affId = affiliate.getAffId().toString();
        String channelID = affiliate.getChannelId().toString();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"affiliateId\": " + affId + ",\"customerChannel\": {\"channelID\": " + channelID + ",\"statusID\": 1, \"comment1\":" +
                " \"Automation Enabled\"},\"camID\":"+camId+"}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + postJsonData);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
    }


    public JSONObject getCustomer(String camId) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        resource = "/cam/service/Info/Customer";
        parameters = "?camID="+camId+"";

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(camEndpoint + resource + parameters);
        return response;
    }

    public boolean isAccountDisabled(String camId) throws IOException {
        JSONObject customer = getCustomer(camId);
        String status = (String) customer.getJSONObject("customerChannel").get("statusLabel");
        return status.equals("LOCKED");
    }

    public JSONObject getCustomerAccountInfo(String camId) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        resource = "cam/mvc/customer/";
        parameters = camId + "/get-my-account";

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(camEndpoint + resource + parameters);

        return response;
    }

    public JSONObject getCustomerTourStatus(String camId) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        resource = "cam/service/Info/Customer";
        parameters = "?camID=" + camId + "";

        JSONObject response = WebserviceCalls.getEndpointJSONResponse(camEndpoint + resource + parameters);
        JSONObject customerTrackingInfo = response.getJSONObject("customerTrackingInfo");

        return customerTrackingInfo;
    }

    public void updateCustomerPin(String camId, String accountNumber, String currentPin, String updatePin) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        String url = camEndpoint + "cam/mvc/customer/update-my-account";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String affId = affiliate.getAffId().toString();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"camId\":\"" + camId + "\",\"currentPin\":\"" + currentPin + "\",\"hostName\":\"hostName\",\"accountNumber\":\"" +
                accountNumber + "\",\"affId\":\"" + affId + "\",\"pin\":\"" + updatePin + "\"}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + postJsonData);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
    }

    public void updateCustomerPassword(String camId, String accountNumber, String currentPassword, String updatePassword) throws IOException {
        camEndpoint = testDataQueries.getServiceEndpoint(testEnv,"cam");
        String url = camEndpoint + "cam/mvc/customer/update-my-account";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String affId = affiliate.getAffId().toString();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        String postJsonData = "{\"camId\":\"" + camId + "\",\"currentPassword\":\"" + currentPassword + "\",\"hostName\":\"hostName\",\"accountNumber\":\"" +
                accountNumber + "\",\"affId\":\"" + affId + "\",\"password\":\"" + updatePassword + "\"}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + postJsonData);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
    }
}