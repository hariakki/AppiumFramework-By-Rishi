package com.twinspires.qa.core.sqlqueries;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.testdata.SQLDataCredentials;
import com.twinspires.qa.core.testdata.TestDataCredentials;
import com.twinspires.qa.core.util.DBConnect;

public class SQLQueries {
    DBConnect dbConnect = new DBConnect();
    protected TestEnv testEnv = TestEnv.fromString(System.getProperty("env", "ite"));
    SQLDataCredentials sqlDataCredentials = new SQLDataCredentials(testEnv);
    TestDataCredentials testDataCredentials = new TestDataCredentials(testEnv);

    protected PreparedStatement buildQuery(Connection connection, String query, Object... parameters) {
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
                } else if(parameters[i] instanceof java.sql.Date) {
                    prepStatement.setDate((i+1),       (java.sql.Date)parameters[i]);
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

    public void closeConnection(ResultSet rs, PreparedStatement st, Connection connection) {
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

    public void closeConnection(ResultSet rs, Statement st, Connection connection) {
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

    public void closeConnection(PreparedStatement st, Connection connection) {
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

    public void closeConnection(ResultSet rs, Connection connection) {
        try {
            if (rs != null) {
                rs.close();
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

    public String getHost(String hostName) {
        String host = getHost(testEnv.toString(), hostName);
        return host;
    }

    public String getHost(String testEnv, String hostName) {

        Connection connection = dbConnect.getDBConnection("10.20.13.69:3306/test",
                testDataCredentials.getUsername(), testDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String host_address = "";

        try {

            String query = "select host_address from hosts where test_environment = ? and host_name = ?";

            st = connection.prepareStatement(query);
            st.setString(1, testEnv);
            st.setString(2, hostName);

            rs = st.executeQuery();
            if (rs.next()) {
                host_address = rs.getString("host_address");
            } else {
                host_address = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return host_address;
    }
    
    public String formatDate(Date preFormattedDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(preFormattedDate);
    }
    
    public Date parseDate(String preFormattedDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(preFormattedDate);
        } catch (Exception e) {
            return null;
        }
    }

    public void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public String getTodaysDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String raceDate = simpleDateFormat.format(date);
        
        return raceDate;
    }

    protected Connection getBDSConnection(){
        // DB Connection to bris_migration_v5 (BDS), which is the only DB.
        return dbConnect.getDBConnection(getHost("bds") + "/bris_migration_v5",
                sqlDataCredentials.getBDSUsername(), sqlDataCredentials.getBDSPassword());
    }

    protected Connection getADWConnection(String dbName){
        // DB Connection to ADW databases
        return dbConnect.getDBConnection(getHost("adw") + "/" + dbName,
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
    }

    protected Connection geCAMConnection(String dbName){
        // DB Connection to CAM databases
        return dbConnect.getDBConnection(getHost("cam cloud") +  "/" + dbName,
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
    }

    protected Connection getProspectConnection(String dbName){
        //DB Connection to prospect
        return dbConnect.getDBConnection(getHost("prospect") + "/" + dbName,
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
    }

    protected Connection getExternalAdapterConnection(String dbName){
        //DB Connection to ExternalAdapter
        return dbConnect.getDBConnection(getHost("externaladapter") + "/" + dbName,
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
    }

    protected Connection getEmarsysConnection(String dbName){
        //DB Connection to Emarsys
        return dbConnect.getDBConnection(getHost("emarsys") + "/" + dbName,
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
    }
}
