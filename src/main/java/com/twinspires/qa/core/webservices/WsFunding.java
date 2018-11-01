package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.sqlqueries.CAMQueries;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WsFunding extends WebserviceCalls {

    CAMQueries camQueries = new CAMQueries();

    /**
     * This calls a web service call to make a deposit based on the value passed in
     *
     * @param username
     * @param password
     * @param accountNum
     * @param depositType {"CHECK", "CASH", "MONEY_ORDER", "WIRE_TRANSFER"}
     * @param depositAmt
     */
    public void postDeposit(String username, String password, int accountNum,
                            String depositType, BigDecimal depositAmt) {

        try {
            String url = buildEndpoint("/php/fw/php_BRIS_BatchAPI/2.3/Miscfunding/deposit");
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            JSONObject requestBody = new JSONObject();

            //add request header
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            requestBody = getDepositRequestBody(username, password, accountNum,
                    depositType, depositAmt);

            // Send post request
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(requestBody.toString());
            osw.flush();
            osw.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' : " + url);
            System.out.println("Response Code : " + responseCode);
        } catch (Exception e) {
            System.out.println("Web Service Call Failed");
            e.printStackTrace();
        }
    }

    /**
     * This creates a JSON Object to pass into the POST /deposit call
     *
     * @param username
     * @param password
     * @param accountNum
     * @return requestBody
     * @throws Exception
     */
    public JSONObject getDepositRequestBody(String username, String password, int accountNum,
                                            String depositType, BigDecimal depositAmt) throws Exception {

        JSONObject requestBody = new JSONObject();
        String affId = affiliate.getAffId();

        //TODO Need to update to work with other affiliates
        requestBody.put("username", "myotb");
        requestBody.put("password", "velitzen");
        requestBody.put("output", "json");
        requestBody.put("affid", affId);
        requestBody.put("handler", "qa.css");
        requestBody.put("account", accountNum);
        requestBody.put("fundCode", depositType);
        requestBody.put("amount", depositAmt);
        requestBody.put("extraFuncCode", "MISC_DEPOSITS");
        requestBody.put("comments", "Testing for Selenium");
        requestBody.put("authKey", getAuthKey(username, password));

        return requestBody;
    }

    public BigDecimal getAccountBalance(String accountNum, String username, String password) throws Exception {
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/adw/legacy/account/balance");
        BigDecimal accountBalance;
        JSONObject requestBody = new JSONObject();

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "172.16.34.124");
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("account", accountNum);
        requestBody.put("pin", password);
        requestBody.put("authKey", postJwtAuthKey(username, password));

        sendRequest("POST", "application/x-www-form-urlencoded", url, requestBody);

        try {
            accountBalance = parseToJSONObject(lastResponseBody).getJSONObject("AccountBalance").getBigDecimal("Balance");
        } catch (Exception e) {
            accountBalance = null;
        }

        return accountBalance;
    }

    public void postEZMoneyDeposit(String amount, String routingNumber, String bankAccountNumber,
                                   String username, String jwtId) {

        try {
            String url = buildEndpoint("/webapi/Ezmoney/persistAccount");
            URL obj = new URL(url);
            JSONObject requestBody = new JSONObject();
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add request header
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("authorization", jwtId);

            requestBody.put("username", "my_classic");
            requestBody.put("password", "StGKwasb");
            requestBody.put("ip", "18.222.81.111");
            requestBody.put("affid", "2800");
            requestBody.put("affiliateId", "2800");
            requestBody.put("output", "json");
            requestBody.put("bankaccounttype", "checking");
            requestBody.put("amount", amount);
            requestBody.put("bankroutingnumber", routingNumber);
            requestBody.put("bankaccountnumber", bankAccountNumber);
            requestBody.put("account", camQueries.getAccountNumber(username));

            // Send post request
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(requestBody.toString());
            osw.flush();
            osw.close();

            System.out.println("\nSending 'POST' : " + url);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            System.out.println("Response: "+response.toString());

        } catch (Exception e) {
            System.out.println("Web Service Call Failed");
            e.printStackTrace();
        }
    }

    public Map<String, String> getEzMoneyInitialDepositStatus(String username, String jwtId) {
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/webapi/Ezmoney/accountInfo");
        Map<String,String> accountInfo = new HashMap<>();

        try {
            URL obj = new URL(url);
            JSONObject requestBody = new JSONObject();
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add request header
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("authorization", jwtId);

            requestBody.put("username", "my_classic");
            requestBody.put("password", "StGKwasb");
            requestBody.put("ip", "216.26.183.5");
            requestBody.put("affid", affId);
            requestBody.put("affiliateId", affId);
            requestBody.put("output", "json");
            requestBody.put("account", camQueries.getAccountNumber(username));
            requestBody.put("requestType", "2");

            // Send post request
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(requestBody.toString());
            osw.flush();
            osw.close();

            System.out.println("\nSending 'POST' : " + url);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            accountInfo.put("isInitialDeposit", parseToJSONObject(response.toString()).getJSONObject("Response").
                    getJSONObject("basicInfo").get("bankAccountExist").toString());
            accountInfo.put("bankAccountNumber", parseToJSONObject(response.toString()).getJSONObject("Response").
                    getJSONObject("bankInfo").getString("bankAccountNumber"));
        } catch (Exception e) {
            System.out.println("Web Service Call Failed");
            e.printStackTrace();
        }
        return accountInfo;
    }
}
