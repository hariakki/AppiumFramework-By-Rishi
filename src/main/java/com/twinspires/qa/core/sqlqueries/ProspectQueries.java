package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProspectQueries extends SQLQueries {

    //TODO: Need to refactor/refine this because it only works when traffic is slow (or no traffic at all) -- it is a short cut.
    public String getPlatform() {
        
        Connection connection = getProspectConnection("prospect");
        PreparedStatement st = null;
        ResultSet rs = null;
        String platform = "";
        
        try {

            String query = "SELECT platform FROM prospect " + 
                    "ORDER BY created_date DESC " + 
                    "LIMIT 1;";

            st = connection.prepareStatement(query);

            rs = st.executeQuery();
            while (rs.next()) {
                platform = rs.getString("platform");
            }
        } catch (Exception ex) {
            System.out.println("Unable to get the platform");
            ex.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return platform;
    }

    public String getUUID(String email) {

        Connection connection = getProspectConnection("prospect");
        PreparedStatement st = null;
        ResultSet rs = null;
        String uuid= "";

        try {

            String query = "SELECT uuid FROM prospect where email = ?; ";
            st = connection.prepareStatement(query);
            st.setString(1, email);
            rs = st.executeQuery();
            while (rs.next()) {
                uuid = rs.getString("uuid");
            }
        } catch (Exception ex) {
            System.out.println("Unable to get the uuid");
            ex.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return uuid;
    }


}
