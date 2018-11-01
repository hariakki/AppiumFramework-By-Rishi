package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CAMQueries extends SQLQueries {

    public int getAccountNumber(String username) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;

        try {
            // To guarantee we get for TS account, not the last of while loop. The original query might return multi.
            String query = "select ces.access_user_id from cam_customer_external_system ces " +
                    "join cam_customer_channel ccc " +
                    "on ces.Customer_ID = ccc.Customer_ID " +
                    "where ccc.cam_UserName = ? and ces.External_System_ID = 3;";

            st = connection.prepareStatement(query);
            st.setString(1, username);

            rs = st.executeQuery();
            while (rs.next()) {
                accountNumber = rs.getInt("access_user_id");
            }
        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return accountNumber;
    }

    public String getCamGlobalId(String username) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String camId = "";
        String query = "";

        try {

            if(testEnv.getText().equalsIgnoreCase("Prod") || testEnv.getText().equalsIgnoreCase("Load")){
                query = "select cam_global_id from cam.v_cam_customer vcc join cam.cam_customer_channel " +
                        "ccc on vcc.customer_id = ccc.customer_id where ccc.CAM_UserName= ? ;";
            } else {
                query = "select cam_global_id from cam.cam_customer cc join cam.cam_customer_channel \n" +
                        "ccc on cc.customer_id = ccc.customer_id where ccc.CAM_UserName= ? ;";
            }

            st = connection.prepareStatement(query);
            st.setString(1, username);

            rs = st.executeQuery();
            if (rs.next()) {
                camId = rs.getString("cam_global_id");
            } else {
                camId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return camId;
    }

    public String getAffiliateId( String affiliate) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String affiliateId = "";

        try {
            String query = "SELECT Affiliate_ID FROM cam.cam_affiliate Where Name = ?";
            st = connection.prepareStatement(query);
            st.setString(1, affiliate);

            rs = st.executeQuery();
            if (rs.next()) {
                affiliateId = rs.getString("Affiliate_ID");
            } else {
                affiliateId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return affiliateId;
    }

    public String getURID(String camID) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String urid = "";

        try {
            String query = "SELECT * FROM cam.cam_urid WHERE Cam_ID = ? ORDER BY expiretime DESC LIMIT 1";
            st = connection.prepareStatement(query);
            st.setString(1, camID);

            rs = st.executeQuery();
            if (rs.next()) {
                urid = rs.getString("URID");
            } else {
                urid = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return urid;
    }

    public String getUUID(String customerId, String email) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String uuid = "";

        try {
            String query = "SELECT * FROM cam.cam_customer_email WHERE Customer_ID = ? && Email_Value = ? && " +
                    "Email_Type_ID = '5' ORDER BY UUID_Time_Stamp DESC LIMIT 1";
            st = connection.prepareStatement(query);
            st.setString(1, customerId);
            st.setString(2, email);

            rs = st.executeQuery();
            if (rs.next()) {
                uuid = rs.getString("UUID");
            } else {
                uuid = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return uuid;
    }

    public Map<String, String> getCustomerEmailInfo(String customerId, String email) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        Map<String, String> emailInfo = new HashMap<>();

        try {
            String query = "SELECT * FROM cam.cam_customer_email WHERE Customer_ID = ? && Email_Value = ?";
            st = connection.prepareStatement(query);
            st.setString(1, customerId);
            st.setString(2, email);
            rs = st.executeQuery();

            if (rs.next()) {
                emailInfo.put("isValid", rs.getString("Is_Valid"));
                emailInfo.put("isPrimary", rs.getString("Is_Primary"));
            } else {
                emailInfo = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return emailInfo;
    }

    public HashMap<String, String> getExternalSystems(String username) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> externalSystems = new HashMap<String, String>();
        try {
            String query = "Select * from (SELECT access_user_id,Customer_ID,External_System_ID FROM cam.cam_customer_external_system\n" +
                    "Where Customer_ID = (SELECT Customer_Id FROM cam.cam_customer_channel\n" +
                    "Where CAM_UserName = ?)) as t\n" +
                    "Join \n" +
                    "(SELECT External_System_ID,External_System_Name FROM cam.cam_external_system) as v \n" +
                    "on t.External_System_ID = v.External_System_ID";
            st = connection.prepareStatement(query);
            st.setString(1, username);

            rs = st.executeQuery();
            while (rs.next()) {
                externalSystems.put(rs.getString("External_System_Name"), rs.getString("access_user_id"));
            }
        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }

        return externalSystems;
    }

    public HashMap<String, String> getPlatformValue(String username) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> platformValue = new HashMap<String, String>();

        try {
             String query = "SELECT cti.* FROM cam_customer_channel cc, cam_customer_tracking_info cti "
                            + "WHERE cc.CAM_UserName = ? and cti.customer_id = cc.customer_id;";

            st = connection.prepareStatement(query);
            st.setString(1, username);
            rs = st.executeQuery();

            while (rs.next()) {
                platformValue.put(rs.getString("Attribute_Id"), rs.getString("Attribute_Value"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return platformValue;
    }

    public String getUserMailingAddress(String username) {
        Connection connection = dbConnect.getDBConnection(getHost("cam cloud"),
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String mailingAddress = "";

        try {
            String query = "select A.Address1, A.City_Value, B.State_Label, A.Zip_Code\n" +
                    "from cam.cam_customer_address A, cam.cam_state B \n" +
                    "where A.Customer_ID = (select Customer_ID from " +
                    "cam.cam_customer_channel where cam_username = ?)\n" +
                    "and A.State_ID = B.State_ID;";

            st = connection.prepareStatement(query);
            st.setString(1, username);
            rs = st.executeQuery();

            while (rs.next()) {
                mailingAddress = rs.getString("Address1") +"|"+rs.getString("City_Value")
                        +", " +rs.getString("State_Label")+" "+rs.getString("Zip_Code");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return mailingAddress;
    }

    public String getUserName(String camId) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String userName = "";

        try {

            String query = "SELECT CAM_UserName FROM cam.cam_customer_channel\n" +
                    "Where Customer_ID = (SELECT Customer_ID FROM cam.cam_customer\n" +
                    "Where Cam_Global_ID = ?)";

            st = connection.prepareStatement(query);
            st.setString(1, camId);

            rs = st.executeQuery();
            if (rs.next()) {
                userName = rs.getString("CAM_UserName");
            } else {
                userName = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return userName;
    }

    public String getCustomerIDPayPalFTDEmail() {
        Connection connection = dbConnect.getDBConnection(getHost("cam cloud")+"/cam",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String customerID = null;

        try {
            String query = "SELECT * FROM cam.cam_customer_paypal WHERE Email_Value = 'PayPalFTD@twinspires.com'";

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                customerID = rs.getString("Customer_ID");
            }

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        } finally {
            closeConnection(rs, st, connection);
        }

        return customerID;
    }

    public void deletePayPalEmail(String customerID) {
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "DELETE FROM cam.cam_customer_paypal WHERE Customer_ID = " + customerID;

            st = connection.prepareStatement(query);
            st.executeUpdate();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        } finally {
            closeConnection(rs, st, connection);
        }
    }

    public String getCamCustomerIdByUsername(String username){
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String camId = "";
        try {

            String query = "select Customer_ID from cam.cam_customer_channel ccc where ccc.CAM_UserName = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, username);

            rs = st.executeQuery();
            if (rs.next()) {
                camId = rs.getString("Customer_ID");
            } else {
                camId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return camId;
    }

    public String getCamIdByUsername(String username){
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String camId = "";
        try {

            String query = "SELECT Cam_Global_ID FROM cam.cam_customer \n" +
                    "Where Customer_ID = (Select Customer_ID From cam.cam_customer_channel\n" +
                    "where CAM_UserName = ?)";

            st = connection.prepareStatement(query);
            st.setString(1, username);

            rs = st.executeQuery();
            if (rs.next()) {
                camId = rs.getString("Cam_Global_ID");
            } else {
                camId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return camId;
    }

    public String getCamCustomerNameByCamID(String camID){
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String firstName = "";
        try {

            String query = "select First_Name from cam.cam_customer cc where cc.Cam_Global_ID = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, camID);

            rs = st.executeQuery();
            if (rs.next()) {
                firstName = rs.getString("First_Name");
            } else {
                firstName = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return firstName;
    }

    public String getCamCustomerLastNameByCamID(String camID){
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String lastName = "";
        try {

            String query = "select Last_Name from cam.cam_customer cc where cc.Cam_Global_ID = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, camID);

            rs = st.executeQuery();
            if (rs.next()) {
                lastName = rs.getString("Last_Name");
            } else {
                lastName = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return lastName;
    }

    public String getCamCustomerIdBySSN(String ssn){
        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        String camId = "";
        try {

            String query = "select Customer_ID from cam.cam_customer cc where social_security_number_md5  = md5( ? )";

            st = connection.prepareStatement(query);
            st.setString(1, ssn);

            rs = st.executeQuery();
            if (rs.next()) {
                camId = rs.getString("Customer_ID");
            } else {
                camId = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return camId;
    }

    public int[] deleteCustomerFromCAMTables(String customerId){
        int[] results = null;
        if(customerId == "" || customerId == null){
            return results;
        }

        Connection connection = geCAMConnection("cam");
        Statement st = null;
        ResultSet rs = null;

        try{
            // DO NOT REMOVE THE WHERE CLAUSE UNLESS YOU ARE WANTING TO DELETE ALL CAM CUSTOMER DATA
            // Due to foreign key constraints, deletes have to be made in a specific order.  If key constaints
            // change then the order of deletion will need to be changed as well.
            String query = "DELETE FROM %s WHERE Customer_ID = '"+ customerId + "';";

            st = connection.createStatement();

            st.addBatch(String.format(query,"cam_customer_address"));
            st.addBatch(String.format(query,"cam_customer_phone"));
            st.addBatch(String.format(query,"cam_customer_email"));
            st.addBatch(String.format(query,"cam_tournament_enrollment"));
            st.addBatch(String.format(query,"cam_customer_external_system"));
            st.addBatch(String.format(query,"cam_customer_tracking_info"));
            st.addBatch(String.format(query,"cam_customer_bank_account"));
            st.addBatch(String.format(query,"cam_customer_breach_info"));
            st.addBatch(String.format(query,"cam_customer_communication_channel"));
            st.addBatch(String.format(query,"cam_customer_credit_card"));
            st.addBatch(String.format(query,"cam_customer_credit_card_token"));
            st.addBatch(String.format(query,"cam_customer_custom_attribute"));
            st.addBatch(String.format(query,"cam_customer_identification"));
            st.addBatch(String.format(query,"cam_customer_im"));
            st.addBatch(String.format(query,"cam_customer_note"));
            st.addBatch(String.format(query,"cam_customer_paypal"));
            st.addBatch(String.format(query,"cam_customer_persona"));
            st.addBatch(String.format(query,"cam_customer_persona_channel"));
            st.addBatch(String.format(query,"cam_customer_player_group"));
            st.addBatch(String.format(query,"cam_customer_security_question"));
            if(testEnv.getText().equalsIgnoreCase("ste")){
                st.addBatch(String.format(query,"cam_customer_auth")); // Table doesn't exist in ITE...
            }
            st.addBatch(String.format(query,"cam_customer_channel"));
            st.addBatch(String.format(query,"cam_customer"));

            results = st.executeBatch();

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return results;
    }

    public void deleteFavoriteTracks(String userName){
        String customerId = getCamCustomerIdByUsername(userName);

        Connection connection = geCAMConnection("cam");
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            String query = "DELETE FROM cam_customer_tracking_info " +
                    "WHERE Customer_ID = '"+ customerId +"' " +
                    "AND Attribute_ID = '11';";

            st = connection.prepareStatement(query);
            st.execute();
        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
    }
}