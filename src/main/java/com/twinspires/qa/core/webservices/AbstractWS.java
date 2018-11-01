package com.twinspires.qa.core.webservices;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.sqlqueries.SQLQueries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by kasey.sparkman on 2/01/2018.
 */
public abstract class AbstractWS {
    // Static Value Request Methods
    public static final String REQ_METHOD_GET = "GET";
    public static final String REQ_METHOD_POST = "POST";
    
    // Static Value Content Types
    public static final String CONTENT_APP_JSON = "application/json";
    public static final String CONTENT_APP_FORM_URLENCODED = "application/x-www-form-urlencoded";
    
    protected TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
    protected Affiliate affiliate = Affiliate.fromString(System.getProperty("aff", "ts"));
    protected SQLQueries sqlQueries = new SQLQueries();
    protected Map<String, List<String>> lastResponseHeader;
    protected JSONObject lastRequestBody;
    protected String lastResponseBody;
    public static int lastReponseCode;

    /**
     * Prepends the appropriate "https://[envSubDomain]." to the url based on the intended environment.
     * @param endpoint the url endpoint starting with the domain (ex. "twinspires.com/php/.../Wagers")
     * @return the full endpoint url
     */
    protected static String buildEndpoint(String endpoint) {
        String env = System.getProperty("env", "").trim().toLowerCase();
        String host = getAffiliate().toString().toLowerCase();

        if (env.equals("ite") || env.equals("ste")) {
            return "https://" + env + "." + host + ".com" + endpoint;
        } else if(env.equals("load")) {
            return "https://" + env + "test." + host + ".com" + endpoint;
        }
        else {
            return "https://www." + host + ".com" + endpoint;
        }
    }

    private static Affiliate getAffiliate(){
        return Affiliate.fromString(System.getProperty("aff", "ts"));
    }
    
    /**
     * Performs a basic GET endpoint request and returns the result as a string for parsing
     * @param endPointURL the GET endpoint url to be called
     * @param additionalHeaders [optional] additional headers that need to be included in the WS call
     */
    public static String getEndpointStringResponse(String endPointURL, HashMap<String, String> additionalHeaders) {
        URL url = null;
        HttpURLConnection httpConnection = null;
        String endPointResponse = "";
        String output = "";
        BufferedReader bufferedReader = null;
        int retryAttempts = 0;

        do{
            try {
                url = new URL(endPointURL);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setConnectTimeout(50000);
                httpConnection.setRequestProperty("Accept", "application/json");
                
                if(null != additionalHeaders) {
                    Iterator iter = additionalHeaders.entrySet().iterator();
                    while(iter.hasNext()) {
                        Map.Entry header = (Map.Entry)iter.next();
                        httpConnection.setRequestProperty(header.getKey().toString(), header.getValue().toString());
                    }
                }

                lastReponseCode = httpConnection.getResponseCode();
                bufferedReader = new BufferedReader(new InputStreamReader(
                        (httpConnection.getInputStream())));
                while ((endPointResponse = bufferedReader.readLine()) != null) {
                    output = endPointResponse.toString();
                }
                httpConnection.disconnect();
                break;
            }catch(ConnectException e){
                retryAttempts++;
            }
            catch (MalformedURLException e) {
                retryAttempts++;
            } catch (IOException e) {
                retryAttempts++;
            }
        }while(retryAttempts < 5 );
        
        return output;
    }
    public static String getEndpointStringResponse(String endPointURL) {
        return getEndpointStringResponse(endPointURL, null);
    }

    public static String postEndpointStringResponse(String endPointURL) {
        URL url;
        HttpURLConnection httpConnection;
        String endPointResponse;
        String output = "";
        BufferedReader bufferedReader;
        int retryAttempts = 0;

        do {
            try {
                url = new URL(endPointURL);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setConnectTimeout(10000);
                httpConnection.setRequestProperty("Accept", "application/json");
                lastReponseCode = httpConnection.getResponseCode();

                bufferedReader = new BufferedReader(new InputStreamReader(
                        (httpConnection.getInputStream())));
                while ((endPointResponse = bufferedReader.readLine()) != null) {
                    output = endPointResponse;
                }
                httpConnection.disconnect();
                break;
            }
            catch (IOException e) {
                retryAttempts++;
            }
        }
        while(retryAttempts < 5 );

        return output;
    }

    protected JSONObject parseToJSONObject() {
        return parseToJSONObject(this.lastResponseBody);
    }
    
    protected static JSONObject parseToJSONObject(String response) {
        JSONObject outputJSON = null;
        outputJSON = new JSONObject(response);
        return outputJSON;
    }

    protected JSONArray parseToJSONArray() {
        return parseToJSONArray(this.lastResponseBody);
    }
    
    protected static JSONArray parseToJSONArray(String response) {
        JSONArray outputJSON = null;
        outputJSON = new JSONArray(response);
        return outputJSON;
    }
    
    protected String sendRequest(String requestMethod, String endPointURL, JSONObject requestBody) {
        if(requestMethod.equalsIgnoreCase(REQ_METHOD_GET)) {
            return sendRequest(requestMethod, CONTENT_APP_FORM_URLENCODED, endPointURL, requestBody, null);
        } else if (requestMethod.equalsIgnoreCase(REQ_METHOD_POST)) {
            return sendRequest(requestMethod, CONTENT_APP_JSON, endPointURL, requestBody, null);
        } else {
            return sendRequest(requestMethod, "", endPointURL, requestBody, null);
        }
    }
    
    /**
     * Sends a Webservice request
     * @param requestMethod GET, POST, etc
     * @param contentType the Content-Type in the request header.
     *                      Most commonly "application/json" and "application/x-www-form-urlencoded"
     * @param endPointURL the endpoint to make the request against
     * @param requestBody JSONObject request body data. Converted to necessary formats automatically
     *                      (conversion limited to certain conditions, add others as necessary)
     * @return the response body that is returned from the WS call
     */
    protected String sendRequest(String requestMethod, String contentType, String endPointURL, JSONObject requestBody,
                                 String... headers){
        HttpURLConnection httpConnection;
        URL mrUrl;
        String wsRequestMethod = requestMethod.toUpperCase().trim();
        String wsContentType = contentType.toLowerCase().trim();
        String wsRequestBody = "";
        String wsResponse;

        lastResponseHeader = null;
        lastResponseBody = null;
        lastRequestBody = requestBody;

        try {
            // Formats the request body data
            if(requestBody != null) {
                if (requestMethod.equalsIgnoreCase(REQ_METHOD_GET)
                        || contentType.equalsIgnoreCase(CONTENT_APP_FORM_URLENCODED)) {
                    for (String key : requestBody.keySet()) {
                        wsRequestBody += "&" + key + "=" + requestBody.getString(key);
                    }
                    wsRequestBody = wsRequestBody.substring(1);
                } else {
                    wsRequestBody = requestBody.toString();
                }
            }

            // Setup URL
            if(wsRequestMethod.equalsIgnoreCase(REQ_METHOD_GET)) {
                mrUrl = new URL(endPointURL + "?" + wsRequestBody);
            } else {
                mrUrl = new URL(endPointURL);
            }

            // Setup Connection
            httpConnection = (HttpURLConnection) mrUrl.openConnection();
            httpConnection.setRequestMethod(wsRequestMethod);
            httpConnection.setConnectTimeout(50000);
            httpConnection.setDoOutput(true);

            // Setup any provided Headers
            if(headers!=null) {
                for(int h = 0; h < headers.length; h++) {
                    String temp[] = headers[h].split(":");
                    httpConnection.setRequestProperty(temp[0].trim(), temp[1].trim());
                }
            }

            // BONUS header only
            if(endPointURL.contains("bonus-services") || endPointURL.contains("vip-services"))
                httpConnection.setRequestProperty("boss_user","badmin");

            // GET calls only
            if(requestMethod.equalsIgnoreCase(REQ_METHOD_GET)) {
                httpConnection.setRequestProperty("Accept", CONTENT_APP_JSON);

            // All others (Confirmed working: POST, PUT)
            } else {
                httpConnection.setRequestProperty("Content-Type", wsContentType);

                // Make Request
                OutputStream os = httpConnection.getOutputStream();
                os.write(wsRequestBody.getBytes("UTF-8"));
                os.close();
            }

            // Check Response status
            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpConnection.getResponseCode());
            }

            // Get Header
            lastResponseHeader = httpConnection.getHeaderFields();

            // Get Response body
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpConnection.getInputStream())));

            while ((wsResponse = br.readLine()) != null) {
                lastResponseBody = wsResponse.toString();
            }

            httpConnection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL Exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return lastResponseBody;
    }
    
    protected String getLastResponseBody() {
        return lastResponseBody;
    }

    public JSONObject getLastRequestBody() {
        return lastRequestBody;
    }
}
