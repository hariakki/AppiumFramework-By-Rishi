package com.twinspires.qa.core.util;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Created by dalwinder.singh on 8/30/16.
 */
public class DBConnect {

    public Connection getDBConnection(String connectionURL, String username, String password){
        Connection connection = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("No Oracle JDBC driver found");
            e.printStackTrace();
            return null;

        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"+connectionURL,username,password);
            if (connection == null) {
                Util.printLine("Connection Failed");
            }
            return connection;

        } catch (SQLException e) {

            Util.printLine("Connection Failed");
            e.printStackTrace();
            return null;

        }
    }

    public PreparedStatement buildQuery(Connection connection, String query, Object... parameters) {
        PreparedStatement prepStatement = null;

        try {
            prepStatement = connection.prepareStatement(query);
        } catch (Exception e) {
            System.out.println("Error building mysql query:  " + e.getMessage());
        }

        for(int i = 0; i < parameters.length; i++) {
            try{
                if(String.class.isInstance(parameters[i])) {
                    prepStatement.setString((i+1),      (String) parameters[i]);
                } else if(Character.class.isInstance(parameters[i])) {
                    prepStatement.setString((i+1),      ((Character) parameters[i]).toString());
                } else if(parameters[i] instanceof BigDecimal) {
                    prepStatement.setBigDecimal((i+1), (BigDecimal)parameters[i]);
                } else if(parameters[i] instanceof Boolean) {
                    prepStatement.setBoolean((i+1),    (Boolean)parameters[i]);
                } else if(parameters[i] instanceof Date) {
                    prepStatement.setDate((i+1),       (Date)parameters[i]);
                } else if(parameters[i] instanceof Double) {
                    prepStatement.setDouble((i+1),     (Double)parameters[i]);
                } else if(parameters[i] instanceof Integer) {
                    prepStatement.setInt((i+1),        (Integer)parameters[i]);
                } else if(parameters[i] instanceof Float) {
                    prepStatement.setFloat((i+1),      (Float)parameters[i]);
                } else if(parameters[i] instanceof Long) {
                    prepStatement.setLong((i+1),       (Long)parameters[i]);
                } else if(parameters[i] instanceof Time) {
                    prepStatement.setTime((i+1),       (Time)parameters[i]);
                } else if(parameters[i] instanceof Timestamp) {
                    prepStatement.setTimestamp((i+1),  (Timestamp)parameters[i]);
                }
            } catch (Exception e) {
                System.out.println("Error building mysql query with parameters:  " + e.getMessage());
            }
        }
        return prepStatement;
    }

    public void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
