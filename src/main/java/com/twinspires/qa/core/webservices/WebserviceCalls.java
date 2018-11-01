package com.twinspires.qa.core.webservices;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

public class WebserviceCalls extends AbstractWS{
    public static String authKey;

	/**
	 * This calls a web service call to make a pos/neg based on the value passed in
	 *
	 * @param accountNum
	 * @param camId
	 * @param adjustmentType
	 * @param depositAmt
	 */
	public void postAdjustment(int accountNum, String camId, String adjustmentType, BigDecimal depositAmt) {
		try {
			//TODO need to get ip address dynamically from somewhere; Move WS URL to db
			String env = System.getProperty("env", "");
			String url = "";
			if (env.trim().equalsIgnoreCase("ITE")) {
				url = "https://funding-integration.twinspires.chdn.com/funding/adjust/adjustTransaction";
			} else if (env.trim().equalsIgnoreCase("STE")) {
				url = "https://funding-staging.twinspires.chdn.com/funding/adjust/adjustTransaction";
			} else if(env.trim().equalsIgnoreCase("LOAD")) {
				url = "https://funding-loadtest.twinspires.chdn.com/funding/adjust/adjustTransaction";
			}

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			JSONObject requestBody = new JSONObject();
			
			//add request header
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			
			requestBody = getAdjustmentRequestBody(accountNum, camId, adjustmentType, depositAmt);
	
			// Send post request
			OutputStream os = con.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			osw.write(requestBody.toString());
			osw.flush();
			osw.close();
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' : " + url);
			System.out.println("Response Code : " + responseCode);
			System.out.println(adjustmentType + " of " + depositAmt + " has been made to user(camid): " + camId + " on account: " + accountNum);
		} catch (Exception e) {
			System.out.println("Web Service Call Failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * This creates a JSON Object to pass into the POST /adjustTransaction call
	 *
	 * @param accountNum
	 * @param camId
	 * @param adjustmentType
	 * @param depositAmt
	 * @return requestBody
	 */
	private JSONObject getAdjustmentRequestBody(int accountNum, String camId, String adjustmentType, BigDecimal depositAmt) {
		JSONObject requestBody = new JSONObject();

		requestBody.put("affiliateId", "2800");
		requestBody.put("amount", depositAmt);
		requestBody.put("customerId", camId);
		requestBody.put("accountNumber", accountNum);
		requestBody.put("handler", "qa.css");
		requestBody.put("transactionType", adjustmentType);
		requestBody.put("reasonCode", "OTHER");
		requestBody.put("comment", "Testing for Selenium");
		requestBody.put("failOnToteDown", true);

		return requestBody;
	}

	/**
	 * TODO : DEPRECATING All instances of AuthKey are being replaced by the jwtId
	 * This get the authorization key for an account that is
	 * logged in so it can be passed to other web services
	 *
	 * @param username
	 * @param password
	 * @return String authKey
	 * @throws Exception
	 */
	public String getAuthKey(String username, String password) throws Exception {
		String affId = affiliate.getAffId();
		String key = "An error occurred acquiring the user [" + username + "] authKey";
		String url = buildEndpoint("/webapi/Key/Auth");
		URL myurl = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setDoOutput(true);

		//TODO Needs update to work for other accounts
		//TODO Needs updated to work for different IPs. This will probably only work on ITE
		String requestBody = "username=iphone";
		requestBody += "&password=ru13juhyo";
		requestBody += "&affid=" + affId;
		requestBody += "&affiliateId=" + affId;
		requestBody += "&ip=172.17.34.160";
		requestBody += "&tmsid=3275d8d92b319e1e190877a3b361804d";
		requestBody += "&account=" + username;
		requestBody += "&pin=" + password;
		requestBody += "&output=json";

		DataOutputStream output = new DataOutputStream(con.getOutputStream());
		output.writeBytes(requestBody);

		BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
		StringBuilder sb = new StringBuilder();
		String responseBody;

		while ((responseBody = br.readLine()) != null) {
			sb.append(responseBody);
		}

		JSONObject json =  new JSONObject(sb.toString());
		JSONObject authorizationKey =  json.getJSONObject("AuthorizationKey");
		key = authorizationKey.getString("Key");

		return key;
	}

	/**
	 * This get the authorization key for an account that is
	 * logged in so it can be passed to other web services
	 *
	 * @param username
	 * @param password
	 * @return String jwtAuthKey
	 * @throws Exception
	 */
	public String postJwtAuthKey(String username, String password) {
	    String affId = affiliate.getAffId();
        String url = buildEndpoint("/webapi/Key/JwtID");
        String jwtAuthKey = "";
		String response = "";
		JSONObject requestBody = new JSONObject();

        requestBody.put("username", "myotb");
        requestBody.put("password", "veltizen");
        requestBody.put("camUsername", username);
        requestBody.put("camPassword", password);
        requestBody.put("affid", affId);
        requestBody.put("output", "json");
        requestBody.put("ip", "127.0.0.1");

        sendRequest("POST", "application/json", url, requestBody);

        if (parseToJSONObject(this.lastResponseBody).getJSONObject("loginResult").getString("message").contains("SUCCESS")) {
            // Get header data
            jwtAuthKey = this.lastResponseHeader.get("Authorization").get(0).replace("Bearer", "").trim();
        } else {
            Util.printLine(parseToJSONObject(this.lastResponseBody).getJSONObject("loginResult").getString("message"));
        }

		return jwtAuthKey;
	}

	//Generic method to parse JSON response of an end point.
	public static JSONObject getEndpointJSONResponse(String endPointURL) {
		URL url = null;
		HttpURLConnection httpConnection = null;
		String endPointResponse = "";
		JSONObject outputJSON = null;
		BufferedReader bufferedReader = null;
		int retryAttempts = 0;
		do {
			try {
				url = new URL(endPointURL);
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestMethod("GET");
				httpConnection.setConnectTimeout(50000);

				httpConnection.setRequestProperty("Accept", "application/json");
				if(authKey !=null)
					httpConnection.setRequestProperty("Authorization",authKey);
				bufferedReader = new BufferedReader(new InputStreamReader(
						(httpConnection.getInputStream())));
				while ((endPointResponse = bufferedReader.readLine()) != null) {
					outputJSON = new JSONObject(endPointResponse.toString());
				}
                httpConnection.disconnect();
                break;
			} catch (IOException e) {
				retryAttempts++;
			} catch (JSONException e) {
                outputJSON = new JSONObject("{\"Races\": " + endPointResponse.toString() + "}");
                break;
			}

		} while (retryAttempts < 5 );

		return outputJSON;
	}

	public static JSONObject postEndpointJSONResponse(String endPointURL,JSONObject request){
		URL url;
		HttpURLConnection httpConnection;
		String endPointResponse = "";
		JSONObject outputJSON = null;
		try {
		    // Setup POST connection
			url = new URL(endPointURL);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setConnectTimeout(50000);
			httpConnection.setDoOutput(true);
			httpConnection.setRequestProperty("Content-Type", "application/json");
			if(endPointURL.contains("bonus-services") || endPointURL.contains("vip-services"))
				httpConnection.setRequestProperty("boss_user","badmin");
			if(authKey !=null)
				httpConnection.setRequestProperty("Authorization",authKey);
			// Make Request
			OutputStream os = httpConnection.getOutputStream();
			os.write(request.toString().getBytes("UTF-8"));
			os.close();

			// Check Response status
			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ httpConnection.getResponseCode());
			}

			// Get Response body
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
			while ((endPointResponse = br.readLine()) != null) {
				outputJSON = new JSONObject(endPointResponse.toString());
			}

			httpConnection.disconnect();
			return outputJSON;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch(JSONException e) {
			System.out.println(endPointResponse);
			return new JSONObject("{ \"object\":" + endPointResponse + "}");
		}
	}

	public void setVelocityLimit(String accountNumber, String fundType, String setLimit, String amount, TestEnv testEnv) throws IOException {
		String USER_AGENT = "Mozilla/5.0";
		String url = "https://" + testEnv + ".twinspires.com/extra/v2/php/limitGroupHandler.php";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Cookie", "USER=607A243F8F9588EA4AA751B6604A0A08AD2D7A8BD50E595904CF9DF88450CF1546D2C15926400C96DD0EE3FAF68500DDA088E30F19;");
		String urlParameters = "accountNumber=" + accountNumber + "&affiliateId=2800&fundType=" + fundType +
                "&" + setLimit + "=" + amount + "&service=updateFunds";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}

		in.close();
	}
}