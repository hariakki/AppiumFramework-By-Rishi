package com.twinspires.qa.core.util;

import java.sql.*;

/**
 * Created by chad.justice on 9/27/2016.
 */
public class TrackInfoGetter {

    private String dbPath;
    private String username;
    private String password;
    private String trackName;
    private String trackId;
    private String trackType;
    private DBConnect dbConnect = new DBConnect();

    /**
     *
     * @param dbPath Database path
     * @param username Database username
     * @param password Database password
     * @param trackName Track name
     */
    public TrackInfoGetter(String dbPath, String username, String password, String trackName){
        this.dbPath = dbPath;
        this.username = username;
        this.password = password;
        this.trackName = trackName;
        this.populate();
    }

    private void populate(){

        Connection connection = dbConnect.getDBConnection(dbPath, username, password);
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            String query = "select track_id, track_type from bds_track_codes where name = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, trackName);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.trackId = resultSet.getString("track_id");
                if(resultSet.getString("track_type").equalsIgnoreCase("HS")){
                    this.trackType = "Harness";
                }
                else{
                    this.trackType = "Thoroughbred";
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {e.printStackTrace();}
            try { if (statement != null) statement.close(); } catch (SQLException e) {e.printStackTrace();}
            try { if (connection != null) connection.close(); } catch (SQLException e) {e.printStackTrace();}
        }
    }

    public String getTrackId() {
        return trackId;
    }
    public String getTrackType() {
        return trackType;
    }

}
