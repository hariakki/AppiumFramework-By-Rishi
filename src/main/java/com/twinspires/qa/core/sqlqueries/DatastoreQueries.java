package com.twinspires.qa.core.sqlqueries;

import com.twinspires.qa.core.enums.ClickEventType;
import com.twinspires.qa.core.testdata.ClickEvent;
import com.twinspires.qa.core.testobjects.*;
import org.testng.Reporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Marketing click event tracking
 */
public class DatastoreQueries extends SQLQueries {

    /**
     * Sql formats a filter list of click types from objects ClickEvents, Integer, or String
     * @param preText only prepended if there are click type ids to filter by
     * @param filterClickTypeIds
     * @return formatted sql string to filter by the specified ClickTypeIDs
     */
    private String formatClickTypeIdsFilter(String preText, Object... filterClickTypeIds) {
        String filter = preText + " Click_Type_ID IN (";
        if(filterClickTypeIds.length == 0) return "";
        for(int i = 0; i < filterClickTypeIds.length; i++) {
            if(ClickEventType.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i>0) ? ", " : "") + ((ClickEventType)filterClickTypeIds[i]).getId();
            } else if(Integer.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i>0) ? ", " : "") + ((Integer)filterClickTypeIds[i]).intValue();
            } else if(String.class.isInstance(filterClickTypeIds[i])) {
                filter += ((i>0) ? ", " : "") + (String)filterClickTypeIds[i];
            } else { // Possibly throw a type exception, instead
                Reporter.log("Invalid ClickTypeId Type [" + filterClickTypeIds[i].toString() + "]: "
                        + "must be ClickEvent, Integer, or String");
            }
        }
        filter += ") ";
        return filter;
    }

    /**
     * Gets all click events for the specified Session ID and filter by any provided clickTypeIds
     * @param sessionId the session ID to filter by
     * @param filterClickTypes [OPTIONAL] Click_Type_Ids to filter by
     * @return list of click events matching the session id and click types
     */
    public List<ClickEvent> getClickEventsBySessionId(String sessionId, Object... filterClickTypes) {
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ClickEvent> clickEventsList = new ArrayList<>();
        ClickEvent clickEvent;
        String clickTypeFilter = formatClickTypeIdsFilter("AND", filterClickTypes);

        String query = "SELECT * FROM Click_Event " +
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
                clickEventsList.add(clickEvent);
            }
        } catch (Exception ex) {
            System.out.println("Unable to find the session id for this method");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return clickEventsList;
    }

    public ArrayList<String> getVideoClickEvents(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<String> clickEventIds = new ArrayList<String>();

        try {
            String query = "SELECT Click_Type_ID " +
                    "FROM Click_Event " +
                    "WHERE Session_ID = ? " +
                    //There may be a chance of non video related events being recorded this next line filters them out
                    "AND Click_Type_ID in ('35','36','37','16','17','55','56') " +
                    "AND date(Click_Event.time_Stamp) = CURDATE() " +
                    "ORDER BY Time_Stamp ASC;";

            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                clickEventIds.add(rs.getString("Click_Type_ID"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return clickEventIds;
    }

    public ArrayList<String> getVideoClickEventsWithUserID(String sessionID, String userID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<String> clickEventIds = new ArrayList<String>();

        try {
            String query = "SELECT Click_Type_ID " +
                    "FROM Click_Event " +
                    "WHERE Session_ID = ? " +
                    "AND Customer_ID = ? " +
                    //There may be a chance of non video related events being recorded this next line filters them out
                    "AND Click_Type_ID in ('35','36','37','16','17','55','56') " +
                    "AND date(Click_Event.time_Stamp) = CURDATE() " +
                    "ORDER BY Time_Stamp ASC;";

            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            st.setString(2, userID);
            rs = st.executeQuery();

            while (rs.next()) {
                clickEventIds.add(rs.getString("Click_Type_ID"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return clickEventIds;
    }

    public int getLatestNumberOfPopoutInstances(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int numberOfPopoutInstances = 0;

        try {
            String query = "SELECT Attribute_Value " +
                    "FROM Click_Event " +
                    "INNER JOIN Click_Event_Attribute ON Click_Event.Click_Event_ID=Click_Event_Attribute.Click_Event_ID " +
                    "WHERE Session_ID = ? " +
                    "AND Click_Type_ID = \"37\" " +
                    "AND date(Click_Event.time_Stamp) = CURDATE() " +
                    "ORDER BY Click_Event.Time_Stamp DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                numberOfPopoutInstances = rs.getInt("Attribute_Value");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return numberOfPopoutInstances;
    }

    public ArrayList<String> getClickEvents(String camId, String sessionId, String clickEvents) {
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs;
        ArrayList<String> clickEventIds = new ArrayList<String>();

        try {
            String query = "SELECT * FROM datastore.Click_Event " +
                    "WHERE Click_Type_ID in (" + clickEvents + ")" +
                    "&& Session_ID = ? " +
                    "order by Time_Stamp ASC;";

            st = connection.prepareStatement(query);
            st.setString(1, sessionId);
            rs = st.executeQuery();

            while (rs.next()) {
                clickEventIds.add(rs.getString("Click_Type_ID"));
            }
        }
        catch (Exception e) {
            System.out.println("Connection Failed");
            System.out.println(e.getMessage());
        }
        finally {
            closeConnection(st, connection);
        }

        return clickEventIds;
    }


    public DatastoreWagerInfo getWagerInfo(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        DatastoreWagerInfo datastoreWagerInfo = new DatastoreWagerInfo();

        try {
            String query = "SELECT Bris_Code, Race_Date, Race_Number, Pool_Type,  Total_Wager_Amount, Gateway" +
                    " FROM datastore.Wager where Session_ID = ?";
            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                datastoreWagerInfo.setBrisCode(rs.getString("Bris_Code"));
                datastoreWagerInfo.setRaceDate(rs.getString("Race_Date"));
                datastoreWagerInfo.setRaceNumber(rs.getInt("Race_Number"));
                datastoreWagerInfo.setWagerType(rs.getString("Pool_Type"));
                datastoreWagerInfo.setTotalWagerAmount(rs.getString("Total_Wager_Amount"));
                datastoreWagerInfo.setGateway(rs.getString("Gateway"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return datastoreWagerInfo;
    }

    public DatastoreDepositInfo getDepositInfo(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        DatastoreDepositInfo datastoreDepositInfo = new DatastoreDepositInfo();

        try {
            String query = "SELECT Method, Amount, Gateway " +
                    "FROM datastore.Deposit  where Session_ID = ?";
            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                datastoreDepositInfo.setDepositMethod(rs.getString("Method"));
                datastoreDepositInfo.setDepositAmount(rs.getString("Amount"));
                datastoreDepositInfo.setGateway(rs.getString("Gateway"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }
        return datastoreDepositInfo;
    }

    public DatastoreBonusInfo getBonusInfo(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        DatastoreBonusInfo datastoreBonusInfo = new DatastoreBonusInfo();

        try {
            String query = "SELECT ID_Bonus,Application_Platform, Activity_Type FROM datastore.Bonus_Event\n" +
                    "where Session_ID  = ?";
            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                datastoreBonusInfo.setIdBonus(rs.getString("ID_Bonus"));
                datastoreBonusInfo.setAppPlatform(rs.getString("Application_Platform"));
                datastoreBonusInfo.setActivityType(rs.getString("Activity_Type"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return datastoreBonusInfo;
    }

    public DatastorePromoInfo getPromoInfo(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        DatastorePromoInfo datastorePromoInfo = new DatastorePromoInfo();

        try {
            String query = "SELECT ID_VIP_Campaign,Application_Platform, Activity_Type FROM datastore.Promo_Event\n" +
                    "where Session_ID  = ?";
            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                datastorePromoInfo.setIdPromo(rs.getString("ID_VIP_Campaign"));
                datastorePromoInfo.setAppPlatform(rs.getString("Application_Platform"));
                datastorePromoInfo.setActivityType(rs.getString("Activity_Type"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return datastorePromoInfo;
    }

    public DatastoreRegistrationInfo getRegisrationInfo(String sessionID){
        Connection connection = dbConnect.getDBConnection(getHost("datastore") + "/datastore",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        DatastoreRegistrationInfo datastoreRegistrationInfo = new DatastoreRegistrationInfo();

        try {
            String query = "SELECT * FROM datastore.Registration\n" +
                    "where Session_ID = ?";
            st = connection.prepareStatement(query);
            st.setString(1, sessionID);
            rs = st.executeQuery();

            while (rs.next()) {
                datastoreRegistrationInfo.setCamId(rs.getString("CAM_ID"));
                datastoreRegistrationInfo.setEmailAddress(rs.getString("Email_Address"));
                datastoreRegistrationInfo.setFirstName(rs.getString("First_Name"));
                datastoreRegistrationInfo.setGateway(rs.getString("Gateway"));
                datastoreRegistrationInfo.setLastName(rs.getString("Last_Name"));
                datastoreRegistrationInfo.setMarketingAffiliate(rs.getString("Marketing_Affiliate"));
                datastoreRegistrationInfo.setPromotionCode(rs.getString("Promotion_Code"));
                datastoreRegistrationInfo.setStatus(rs.getString("Status"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return datastoreRegistrationInfo;
    }

}