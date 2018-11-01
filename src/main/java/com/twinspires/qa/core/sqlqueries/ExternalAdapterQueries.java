package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExternalAdapterQueries extends SQLQueries {

    public boolean isProspectIdExists(String uuid) {
        Connection connection = getExternalAdapterConnection("externalsystem");
        PreparedStatement st = null;
        ResultSet rs = null;
        String query = "";
        boolean isIdExists = false;

        try {
            query = "SELECT 1 FROM system_mapping " +
                    "where external_system_id = 'prospect' and external_system_value=? ;";

            st = connection.prepareStatement(query);
            st.setString(1, uuid);
            rs = st.executeQuery();
            if (rs.next()) isIdExists = true;

        } catch (Exception ex) {
            System.out.println("Unable to get the uuid");
            ex.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        return isIdExists;
    }
}
