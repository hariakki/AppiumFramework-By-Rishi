package com.twinspires.qa.core.sqlqueries;

import com.twinspires.qa.core.testobjects.BetshareInfo;
import com.twinspires.qa.core.testobjects.DatastoreRegistrationInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BetShareQueries extends SQLQueries {
    public String getBetShareId(String camId) {
        Connection connection = dbConnect.getDBConnection(getHost("betshare") + "/betshare",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String betShareId = "";

        try {
            String query = "SELECT * FROM Participant WHERE Cam_Global_ID = ? " +
                    "ORDER BY Time_Stamp_Creation desc limit 1";

            st = connection.prepareStatement(query);
            st.setString(1, camId);
            rs = st.executeQuery();

            while (rs.next()) {
                betShareId = rs.getString("Bet_Share_Id");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }

        return betShareId;
    }

    public BetshareInfo getBetshareInfo(String betshareId){
        Connection connection = dbConnect.getDBConnection(getHost("betshare") + "/betshare",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        BetshareInfo betshareInfo = new BetshareInfo();

        try {
            String query = "SELECT Ticket, Track, Race, Pool_Type_ID, Runners, Amount,Cost FROM betshare.Bet_Share\n" +
                    "where Bet_Share_ID = ?";
            st = connection.prepareStatement(query);
            st.setString(1, betshareId);
            rs = st.executeQuery();

            while (rs.next()) {
                betshareInfo.setTicket(rs.getString("Ticket"));
                betshareInfo.setTrackCode(rs.getString("Track"));
                betshareInfo.setRace(rs.getString("Race"));
                betshareInfo.setPoolTypeId(rs.getString("Pool_Type_ID"));
                betshareInfo.setRunners(rs.getString("Runners"));
                betshareInfo.setAmount(rs.getString("Amount"));
                betshareInfo.setCost(rs.getString("Cost"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return betshareInfo;
    }
}
