package com.twinspires.qa.core.sqlqueries;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.webservices.WebserviceCalls;
import com.twinspires.qa.core.webservices.WsRegistration;
import org.json.JSONObject;
import org.testng.Reporter;

import com.twinspires.qa.core.enums.ClickEventType;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.testdata.ClickEvent;
import com.twinspires.qa.core.testobjects.DeviceInfo;
import com.twinspires.qa.core.util.Util;

/**
 * Created by dalwinder.singh on 2/14/17.
 */
public class TestDataQueries extends SQLQueries {
    WsRegistration wsRegistration = new WsRegistration();
    WebserviceCalls webserviceCalls = new WebserviceCalls();
    CAMQueries camQueries = new CAMQueries();


    protected TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
//    TestDataCredentials testDataCredentials = new TestDataCredentials(testEnv);

    /*
     * ************************  Private Methods  ************************ *
     */

    /**
     * Sql formats a filter list of click types from objects ClickEvents, Integer, or String
     *
     * @param preText            only prepended if there are click type ids to filter by
     * @param filterClickTypeIds
     * @return formatted sql string to filter by the specified ClickTypeIDs
     */
    private String formatClickTypeIdsFilter(String preText, Object... filterClickTypeIds) {
        String filter = preText + " Click_Type_ID IN (";
        if (filterClickTypeIds.length == 0) return "";
        for (int i = 0; i < filterClickTypeIds.length; i++) {
            if (ClickEventType.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i > 0) ? ", " : "") + ((ClickEventType) filterClickTypeIds[i]).getId();
            } else if (Integer.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i > 0) ? ", " : "") + ((Integer) filterClickTypeIds[i]).intValue();
            } else if (String.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i > 0) ? ", " : "") + (String) filterClickTypeIds[i];
            } else { // Possibly throw a type exception, instead
                Reporter.log("Invalid ClickTypeId Type [" + filterClickTypeIds[i].toString() + "]: "
                        + "must be ClickEvent, Integer, or String");
            }
        }
        filter += ") ";
        return filter;
    }

    /*
     * ************************  Public Methods  ************************ *
     */

    /**
     * Performs a DB check for any known defects against the current test method name
     *
     * @param TCMethodName The name of the test script method
     * @return String listing the defects for the TC in the format "DEFECT: [DE####]"
     */
    public String checkForDefects(String TCMethodName) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(),
                testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String defects = "";
        String query = "SELECT defect_id "
                + "FROM test.defects "
                + "WHERE tc_method_name = ? ";

        try {
            st = connection.prepareStatement(query);
            st.setString(1, TCMethodName);

            rs = st.executeQuery();
            while (rs.next()) {
                defects += "DEFECT: " + rs.getString("defect_id") + System.lineSeparator();
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);

        }
        return defects;
    }

    public String getUsername(String testEnv, String affiliate, String dataCondition) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String username = "";

        try {

            String query = "select username from usernames where affiliate_name = ? and test_environment = ? and " +
                    "data_condition = ? ";

            st = connection.prepareStatement(query);
            st.setString(1, affiliate);
            st.setString(2, testEnv);
            st.setString(3, dataCondition);

            rs = st.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
            else {
                username = null;
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);

        }
        return username;
    }

    public String getUsernameAfterCreation(String testEnv, String affiliate, String dataCondition) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        //Create a new user in cam and fund it with $1000
        String username = wsRegistration.getNewCamUserName(Affiliate.valueOf(affiliate), TestEnv.valueOf(testEnv));
        webserviceCalls.postAdjustment(camQueries.getAccountNumber(username),
                camQueries.getCamGlobalId(username), "POS_ADJ", BigDecimal.valueOf(1000));

        writeUsernameToDB(username,testEnv,affiliate,dataCondition);
        return username;
    }

    public void writeUsernameToDB(String username, String testEnv, String affiliate, String dataCondition) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;

        try {

            String query = "INSERT INTO usernames (affiliate_name, data_condition, username, test_environment) VALUES (?, ?, ?, ?)";

            st = connection.prepareStatement(query);
            st.setString(1, affiliate.toLowerCase());
            st.setString(2, dataCondition);
            st.setString(3, username);
            st.setString(4, testEnv.toLowerCase());
            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);

        }
    }

    public String getUrl(String testEnv, String affiliate, String channel) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String url = "";

        try {
            String query = "select url from urls where affiliate_name = ? and test_environment = ? " +
                    "and channel = ?";

            st = connection.prepareStatement(query);
            st.setString(1, affiliate);
            st.setString(2, testEnv);
            st.setString(3, channel);

            rs = st.executeQuery();
            if (rs.next()) {
                url = rs.getString("url");
            } else {
                url = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return url;
    }

    public String getServiceEndpoint(String testEnv, String name) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String endpoint = "";

        try {
            String query = "SELECT endpoint FROM test.services where environment = ? and name = ?";

            st = connection.prepareStatement(query);
            st.setString(1, testEnv);
            st.setString(2, name);

            rs = st.executeQuery();
            if (rs.next()) {
                endpoint = rs.getString("endpoint");
            } else {
                endpoint = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return endpoint;
    }

    public String getPassword(String testEnv, String affiliate, String dataCondition) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String password = "";

        try {

            String query = "select password from usernames where affiliate_name = ? and test_environment = ? and " +
                    "data_condition = ? ";

            st = connection.prepareStatement(query);
            st.setString(1, affiliate);
            st.setString(2, testEnv);
            st.setString(3, dataCondition);

            rs = st.executeQuery();
            if (rs.next()) {
                password = rs.getString("password");
            } else {
                password = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);

        }
        return Util.decrypt("QXV0bzEyMyFBdXRvMTIzIQ==", "QXV0b21hdGlvblZlY3Rvcg==", password);
    }

    public void setBonusPlaythroughId(String testEnv, String affiliate, String dataCondition, String bonusId) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;

        try {

            String query = "UPDATE test.playthrough SET bonusId = ? " +
                    "WHERE data_condition = ? and test_environment = ? and affiliate_name = ?";
            st = connection.prepareStatement(query);
            st.setString(1, bonusId);
            st.setString(2, dataCondition);
            st.setString(3, testEnv);
            st.setString(4, affiliate);
            st.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection(st, connection);

        }
    }

    public String getBonusPlaythroughId(String testEnv, String affiliate, String dataCondition) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String bonusId = "";

        try {
            String query = "SELECT bonusId FROM test.playthrough " +
                    "Where data_condition = ? and test_environment = ? and affiliate_name = ?";

            st = connection.prepareStatement(query);
            st.setString(1, dataCondition);
            st.setString(2, testEnv);
            st.setString(3, affiliate);

            rs = st.executeQuery();
            if (rs.next()) {
                bonusId = rs.getString("bonusId");
            } else {
                bonusId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return bonusId;
    }

    public String getBrisTrackCodeByTrackName(String trackName) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String trackCode = "";

        try {
            String query = "SELECT bris_code FROM tracks WHERE track_name = ? limit 1; ";

            st = connection.prepareStatement(query);
            st.setString(1, trackName);

            rs = st.executeQuery();
            while (rs.next()) {
                trackCode = rs.getString("bris_code");
            }
        } catch (Exception ex) {
            System.out.println("Unable to find a track based on that data condition");
        } finally {
            closeConnection(rs, st, connection);
        }

        return trackCode;
    }

    /**
     * Returns a test track from the Automation DB meeting the specified conditions
     *
     * @param dataCondition data condition required
     * @param testCycle     M-morning cycle, A-afternoon cycle
     * @return track matching the conditions
     */
    public HashMap<String, String> getTrack(String dataCondition, String testCycle) {
        return getTrack(dataCondition, testCycle, true).get(0);
    }

    /**
     * Returns all test tracks from the Automation DB meeting the specified conditions
     *
     * @param dataCondition data condition required
     * @param testCycle     M-morning cycle, A-afternoon cycle
     * @param limitOne      whether to limit the query to return one or all matching results
     * @return tracks matching the conditions
     */
    public List<HashMap<String, String>> getTrack(String dataCondition, String testCycle, boolean limitOne) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        List<HashMap<String, String>> tracksList = new ArrayList<>();
        HashMap<String, String> track;

        try {
            String query = "SELECT * FROM tracks " +
                    "WHERE data_condition = ? AND test_cycle = ? " +
                    "ORDER BY RAND()";
            query += (limitOne) ? " LIMIT 1;" : ";";

            st = connection.prepareStatement(query);
            st.setString(1, dataCondition);
            st.setString(2, testCycle);

            rs = st.executeQuery();
            while (rs.next()) {
                track = new HashMap<String, String>();
                track.put("trackName", rs.getString("track_name"));
                track.put("brisCode", rs.getString("bris_code"));
                track.put("bdsCode", rs.getString("bds_code"));
                track.put("trackType", rs.getString("track_type"));
                track.put("country", rs.getString("country"));
                tracksList.add(track);
            }
        } catch (Exception ex) {
            System.out.println("Unable to find a track based on that data condition");
        } finally {
            closeConnection(rs, st, connection);
        }

        return tracksList;
    }

    /*
     * ************************  TABLE: Click_Event_Test  ************************ *
     */

    /**
     * Gets the most recent session ID based on the script method name to filter registered test click events
     *
     * @param methodName the name of the script that generated the click event
     * @return the most recent Session ID
     */
    public String getClickEventSessionIdByMethod(String methodName) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String sessionId = "";

        String query = "SELECT Session_ID FROM Click_Event_Test " +
                "WHERE Method_Name = ? " +
                "ORDER BY Time_Stamp DESC " +
                "LIMIT 1;";
        st = buildQuery(connection, query, methodName);

        try {
            rs = st.executeQuery();
            while (rs.next()) {
                sessionId = rs.getString("Session_ID");
            }
        } catch (Exception ex) {
            System.out.println("Unable to find the session id for this method");
        } finally {
            closeConnection(rs, st, connection);
        }
        return sessionId;
    }

    /**
     * Register an expected click event with the test database
     *
     * @param clickTypeId expected click type id
     * @param sessionId   active session ID for the user associated with the click event
     * @param methodName  name of the test script creating the click event to specific test. Optional by providing "" only.
     * @param assertDesc  assertion text to describe the intended functionality being recorded
     * @param customerId  [OPTIONAL] the customer ID
     */
    public void addClickEvent(Integer clickTypeId, String sessionId, String methodName, String assertDesc, String customerId) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        List<String> varList = new ArrayList<>();

        // Set up variables list for prepared statement injection
        varList.add(clickTypeId.toString());
        varList.add(sessionId);
        if (!methodName.isEmpty()) varList.add(methodName);
        if (!assertDesc.isEmpty()) varList.add(assertDesc);
        if (!customerId.isEmpty()) varList.add(customerId);

        try {
            String query = "INSERT INTO test.Click_Event_Test ("
                    + "Click_Type_ID"
                    + ", Session_ID"
                    + ((methodName.isEmpty()) ? "" : ", Method_Name")        // OPTIONAL
                    + ((assertDesc.isEmpty()) ? "" : ", Assert_Description") // OPTIONAL
                    + ((customerId.isEmpty()) ? "" : ", Customer_ID")        // OPTIONAL
                    + ") VALUES ("
                    + "?"
                    + ", ?"
                    + ((methodName.isEmpty()) ? "" : (", ?"))
                    + ((assertDesc.isEmpty()) ? "" : (", ?"))
                    + ((customerId.isEmpty()) ? "" : (", ?"))
                    + ");";
            st = connection.prepareStatement(query);
            for (int i = 0; i < varList.size(); i++) {
                st.setString(i + 1, varList.get(i));
            }

            st.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error adding click event to test database");
            System.out.println(":: " + e.getMessage());
        } finally {
            closeConnection(st, connection);
        }
        sleepTime(500);
    }

    public void addClickEvent(Integer clickTypeId, String sessionId, String methodName, String assertDesc) {
        addClickEvent(clickTypeId, sessionId, methodName, assertDesc, "");
    }

    public void addClickEvent(ClickEventType clickType, String sessionId, String methodName, String assertDesc, String customerId) {
        addClickEvent(clickType.getId(), sessionId, methodName, assertDesc, customerId);
    }

    public void addClickEvent(ClickEventType clickType, String sessionId, String methodName, String assertDesc) {
        addClickEvent(clickType.getId(), sessionId, methodName, assertDesc, "");
    }

    /**
     * Gets all click events for the specified Session ID and filter by any provided clickTypeIds
     *
     * @param sessionId        the session ID to filter by
     * @param filterClickTypes [OPTIONAL] Click_Type_Ids to filter by
     * @return list of click events matching the session id and click types
     */
    public List<ClickEvent> getClickEventsBySessionId(String sessionId, Object... filterClickTypes) {
        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ClickEvent> clickEventsList = new ArrayList<>();
        ClickEvent clickEvent;
        String clickTypeFilter = formatClickTypeIdsFilter("AND", filterClickTypes);

        String query = "SELECT * FROM Click_Event_Test " +
                "WHERE Session_ID = ? " +
                clickTypeFilter +
                "ORDER BY Time_Stamp ASC;";
        st = buildQuery(connection, query, sessionId);

        try {
            rs = st.executeQuery();
            while (rs.next()) {
                clickEvent = new ClickEvent();
                clickEvent.setClickEventIDs(rs.getInt("Click_Event_ID"));
                clickEvent.setTimeStamp(rs.getString("Time_Stamp"));
                clickEvent.setClickTypeID(rs.getInt("Click_Type_ID"));
                clickEvent.setCustomerID(rs.getString("Customer_ID"));
                clickEvent.setSessionID(rs.getString("Session_ID"));
                clickEvent.setAssertDescription(rs.getString("Assert_Description"));
                clickEvent.setMethodName(rs.getString("Method_Name"));
                clickEventsList.add(clickEvent);
            }
        } catch (Exception ex) {
            System.out.println("Unable to find the session id for this method");
        } finally {
            closeConnection(rs, st, connection);
        }

        return clickEventsList;
    }


    public ArrayList<DeviceInfo> getSupportedDevices(String platformName) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(),
                testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String query = "select device_name,device_version,platform_name from supported_devices where platform_name=?";
        ArrayList<DeviceInfo> devices = new ArrayList<>();

        try {
            st = connection.prepareStatement(query);
            st.setString(1, platformName);
            rs = st.executeQuery();
            while (rs.next()) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceName(rs.getString("device_name"));
                deviceInfo.setDeviceVersion(rs.getString("device_version"));
                deviceInfo.setPlatformName(rs.getString("platform_name"));
                devices.add(deviceInfo);
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return devices;
    }

    public String getTestObjectApiKey(String platformName, String environment) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test", testDataCredentials.getUsername(),
                testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String query = "select api_key from testobject_api_keys where platform_name = ? and test_environment = ?";
        String apiKey = "";
        try {
            st = connection.prepareStatement(query);
            st.setString(1, platformName);
            st.setString(2, environment);
            rs = st.executeQuery();
            while (rs.next()) {
                apiKey = rs.getString("api_key");
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return apiKey;
    }
}