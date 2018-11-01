package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ADWQueries extends SQLQueries {

    BDSQueries bdsQueries = new BDSQueries();
    CAMQueries camQueries = new CAMQueries();

    public String getTrackType(String trackCode) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/track",
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String trackType = "";

        try {
            String query = "SELECT type FROM track WHERE bris_code = '" + trackCode + "' LIMIT 1;";

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            rs.next();
            trackType = rs.getString("type");

        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return trackType;
    }

    public ArrayList<String> getProbablesPools(String trackCode, String raceNumber) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/tote_display",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<String> pools = new ArrayList<String>();

        try {
            String query = "SELECT DISTINCT(p.desc) FROM exotics AS e, pool_types AS p " +
                    "WHERE e.track = ? " +
                    "AND e.race = ? " +
                    "AND p.type = e.type;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                pools.add(rs.getString("desc"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get probables pools");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return pools;
    }

    public HashMap<String, String> getProbablesBaseAmounts(String trackCode, String raceNumber) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/tote_display",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> baseAmounts = new HashMap<String, String>();

        try {
            String query = "SELECT DISTINCT(types.desc), totals.base_amount " +
                    "FROM pool_totals AS totals, pool_types AS types, exotics AS e " +
                    "WHERE totals.track = ? " +
                    "AND totals.race = ? " +
                    "AND totals.pool_type = e.type " +
                    "AND e.type = types.type;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                baseAmounts.put(rs.getString("desc"), rs.getString("base_amount"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get base amounts");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return baseAmounts;
    }

    public HashMap<String, String> getProbablesPoolTotals(String trackCode, String raceNumber) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/tote_display",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> amounts = new HashMap<String, String>();

        try {
            String query = "SELECT DISTINCT(types.desc), totals.amount, totals.amount " +
                    "FROM pool_totals AS totals, pool_types AS types, exotics AS e " +
                    "WHERE totals.track = ? " +
                    "AND totals.race = ? " +
                    "AND totals.pool_type = e.type " +
                    "AND e.type = types.type;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                amounts.put(rs.getString("desc"), rs.getString("amount"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get pool amounts");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return amounts;
    }

    public String getPromoCode(String username) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/winticket", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String promoCode = "";

        try {
            String query = "SELECT promo_code FROM winticket.register \n"
                    + "WHERE username = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, username);
            rs = st.executeQuery();

            while (rs.next()) {
                promoCode = rs.getString("promo_code");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return promoCode;
    }

    /**
     * Queries a Wager Code to Wager Name lookup hash map
     * @return Hash map <Key: wager_code | Value: wager_name>
     */
    public HashMap<String, String> getWagerTypeLookup() {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/wager", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> lookeupTable = new HashMap<String, String>();

        try {
            String query = "SELECT wager_code, wager_name FROM wager.wager_types;";

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                lookeupTable.put(rs.getString("wager_code").trim()
                        , rs.getString("wager_name").trim());
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return lookeupTable;
    }

    public String getWagerConfirmationCode(String account,String programName, String betAmount) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String confirmationCode = "";
        if(!betAmount.contains("."))
            betAmount += ".00";

        try {
            String query = "SELECT * FROM batch.history Where account = ? and  "
                    + "program_name = ? and bet_amount = ? order by res_stamp desc limit 1";

            st = connection.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, programName);
            st.setString(3, betAmount);
            rs = st.executeQuery();

            while (rs.next()) {
                confirmationCode = rs.getString("response");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return confirmationCode;
    }

    public Timestamp getWagerDateTime(String account, String programName, String betAmount) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        Timestamp dateTime = null;
        String adjBetAmount;
        adjBetAmount = (!betAmount.contains(".")) ? betAmount + ".00" : betAmount;

        try {
            String query = "SELECT * FROM batch.history Where account = ? and  "
                    + "program_name = ? and bet_amount = ? order by res_stamp desc limit 1";

            st = connection.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, programName);
            st.setString(3, adjBetAmount);
            rs = st.executeQuery();


            while (rs.next()) {
                dateTime = rs.getTimestamp("queue_stamp");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return dateTime;
    }

    public HashMap<String, String> getWagerDetails(String username) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;
        HashMap<String, String> wagerDetails = new HashMap<String, String>();

        accountNumber = camQueries.getAccountNumber(username);

        try {
            String query = "SELECT prgName, raceNum, betType, runList, ticketPrice, serialNum " +
                    "FROM wager " +
                    "WHERE account = ? " +
                    "AND stamp >= DATE(NOW()) - INTERVAL 7 DAY " +
                    "ORDER BY stamp DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            rs = st.executeQuery();

            while (rs.next()) {
                wagerDetails.put("track", rs.getString("prgName"));
                wagerDetails.put("race", rs.getString("raceNum"));
                wagerDetails.put("wagerCode", rs.getString("betType"));
                wagerDetails.put("runnersList", rs.getString("runList"));
                wagerDetails.put("betAmount", rs.getString("ticketPrice"));
                wagerDetails.put("transactionId", rs.getString("serialNum"));
                wagerDetails.put("account", String.valueOf(accountNumber));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return wagerDetails;
    }

    public HashMap<String, String> getGeolocationDetails(String username, String programName, String race_num) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;
        HashMap<String, String> wagerDetails = new HashMap<String, String>();

        accountNumber = camQueries.getAccountNumber(username);

        try {
            String query = "SELECT latitude, longitude " +
                    "FROM history " +
                    "WHERE account = ? " +
                    "AND program_name = ? " +
                    "AND race_num = ? " +
                    "AND queue_stamp >= Now() - Interval 1 DAY " +
                    "ORDER BY queue_stamp DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            st.setString(2, programName);
            st.setString(3, race_num);
            rs = st.executeQuery();

            while (rs.next()) {
                wagerDetails.put("latitude", rs.getString("latitude"));
                wagerDetails.put("longitude", rs.getString("longitude"));
                wagerDetails.put("account", String.valueOf(accountNumber));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return wagerDetails;
    }

    public HashMap<String, String> getWagerDetails(String username, String date) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;
        HashMap<String, String> wagerDetails = new HashMap<String, String>();

        accountNumber = camQueries.getAccountNumber(username);

        try {
            String query = "SELECT prgName, raceNum, betType, runList, ticketPrice, serialNum " +
                    "FROM wager " +
                    "WHERE account = ? " +
                    "AND betDate =  ? " +
                    "ORDER BY stamp DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            st.setString(2, date);
            rs = st.executeQuery();

            while (rs.next()) {
                wagerDetails.put("track", rs.getString("prgName"));
                wagerDetails.put("race", rs.getString("raceNum"));
                wagerDetails.put("wagerCode", rs.getString("betType"));
                wagerDetails.put("runnersList", rs.getString("runList"));
                wagerDetails.put("betAmount", rs.getString("ticketPrice"));
                wagerDetails.put("transactionId", rs.getString("serialNum"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return wagerDetails;
    }
    
    /**
     * Gets only today's completed wagers count
     * @param username
     * @return
     */
    public int getCompletedWagersCountToday(String username) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;
        int wagersCount = 0;

        accountNumber = camQueries.getAccountNumber(username);

        try {
            String query = "SELECT count(prgName) " +
                    "FROM wager WHERE account = ? " +
                    "AND stamp >= DATE(NOW());";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            rs = st.executeQuery();

            while (rs.next()) {
                wagersCount = rs.getInt("count(prgName)");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return wagersCount;
    }
    
    /**
     * Gets completed wager count for the date range (not current date)
     * @param username the username of the
     * @param startDate start date for range
     * @param daysFromStart days offset from start date (ex. -7 for the week up to startDate)
     * @return Count of completed wagers
     */
    public int getCompletedWagersCountHistory(String username, Date startDate, int daysFromStart) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/winticket",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        final String CUR_DATE = "DATE(NOW())";
        String paramDateStart;
        String paramDateEnd;
        String queryString;
        int accountNumber = 0;
        int count = 0;
        
        // Get formatted dates for the stored procedure
        if(daysFromStart > 0) {
            paramDateStart = (startDate!= null) ? formatDate(startDate) : CUR_DATE;
            paramDateStart = "'" + paramDateStart + "'";
            paramDateEnd = paramDateStart + " + INTERVAL " + Math.abs(daysFromStart) + " DAY";
        } else if(daysFromStart < 0) {
            paramDateEnd = (startDate!= null) ? formatDate(startDate) : CUR_DATE;
            paramDateEnd = "'" + paramDateEnd + "'";
            paramDateStart = paramDateEnd + " - INTERVAL " + Math.abs(daysFromStart) + " DAY";
        } else {
            paramDateStart = (startDate!= null) ? formatDate(startDate) : CUR_DATE;
            paramDateEnd = paramDateStart;
        }
        
        // Get the account number of the provided username
        accountNumber = camQueries.getAccountNumber(username);
    
        try {
            queryString = "CALL p_select_activity_plus("
                    + accountNumber + ", "  // int   `int_AcctNumber`
                    + paramDateStart + ", " // date  `d_ActivityDate_Start`
                    + paramDateEnd + ", "   // date  `d_ActivityDate_End`
                    + CUR_DATE + ", "       // date  `d_today`
                    + "FALSE, "             // bool  `b_frozen`
                    + "'Bet', "             // vchr4 `str_search_type`
                    + "NULL, "              // int   `int_limit`
                    + "0, "                 // int   `int_offset`
                    + "@count);";           // int   output

            System.out.println(queryString);
            
            st = connection.prepareStatement(queryString);
            rs = st.executeQuery();
        
            while (rs.next()) {
                count++;
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }
        
        return count;
    }

    public String getRandomWagerDate(String username, int dayOfMonth) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        int accountNumber = 0;
        String date = "";

        accountNumber = camQueries.getAccountNumber(username);
        dayOfMonth--;

        try {
            String query = "SELECT DISTINCT(betDate) " +
                    "FROM batch.wager " +
                    "WHERE account = ? " +
                    "AND stamp > DATE(NOW()) - INTERVAL ? DAY " +
                    "AND stamp < DATE(NOW()) " +
                    "ORDER BY rand() " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            st.setInt(2, dayOfMonth);
            rs = st.executeQuery();

            while (rs.next()) {
                date = rs.getString("betDate");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return date;
    }

    public String getWagerName(String wagerCode) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/batch",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String wager = "";

        try {
            String query = "SELECT pool_description FROM pool_code_map " +
                    "WHERE twinspires_pool_code = ? " +
                    "AND code_system = 'dfl';";

            st = connection.prepareStatement(query);
            st.setString(1, wagerCode);
            System.out.println(st.toString());
            rs = st.executeQuery();

            while (rs.next()) {
                wager = rs.getString("pool_description");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }
        
        if(wager.contains("Place") || wager.contains("Show"))
            wager = wager.replace("-", "/");
        if(wager.contains("Super Hi-5"))
            wager = "Super High Five";
        
        return wager;
    }

    public HashMap<String, String> getWagerFromActivity(String username, String date) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/winticket",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> wagerDetails = new HashMap<String, String>();
        String temp1 = "";
        String temp2 = "";
        int accountNumber = 0;

        accountNumber = camQueries.getAccountNumber(username);

        try {
            String query = "SELECT * FROM winticket.activity " +
                    "WHERE acctnumber = ? " +
                    "AND activityDate = ? " +
                    "AND txtype != 'Cancel' " +
                    "ORDER BY activityTime DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            st.setString(2, date);
            rs = st.executeQuery();

            while (rs.next()) {
                temp1 = rs.getString("ProgramName"); // Get track code if name lookup is unsuccessful
                temp2 = bdsQueries.getTrackName(temp1);
                wagerDetails.put("trackName", (temp2 == null) ? temp1 : temp2);
                wagerDetails.put("race", rs.getString("Race"));
                wagerDetails.put("betAmount", rs.getString("Amount"));
                wagerDetails.put("returnAmount", "-"+rs.getString("CreditAmt"));
                wagerDetails.put("betType", getWagerName(rs.getString("poolType")));
                wagerDetails.put("runnersList", rs.getString("Runners").replace("+", ","));
                wagerDetails.put("activityDate", rs.getString("ActivityDate"));
                wagerDetails.put("transactionId", rs.getString("SerialNum"));

                if(rs.getString("TxType").equals("Cancelled_Bet")) {
                    wagerDetails.put("txType", "Cancelled");
                    wagerDetails.put("returnAmount", "0.00");
                } else if(rs.getString("TxType").equals("Bet")) {
                    if(rs.getString("RefundAmt").equals("0.00")) {
                        wagerDetails.put("txType", "Complete");
                        wagerDetails.put("returnAmount", "-"+rs.getString("DebitAmt"));
                    } else {
                        wagerDetails.put("txType", "Refunded");
                        wagerDetails.put("returnAmount", rs.getString("RefundAmt"));
                    }
                } else {
                    wagerDetails.put("txType", rs.getString("TxType"));
                }
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }

        return wagerDetails;
    }

    public ArrayList<String> getADWHorseNames(String trackCode, String raceNumber) {
        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/programs",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String raceDate = simpleDateFormat.format(date);
        ArrayList<String> names = new ArrayList<String>();

        try {
            String query = "SELECT F12 FROM `thoroughbred` " +
                    "WHERE f1 = ? " +
                    "AND f2 = ? " +
                    "AND f3 = ? " +
                    "ORDER BY F14+0, LENGTH(F14), right(F14, 1);";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                //column F12 is the horses name in this table
                names.add(rs.getString("F12"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get horse names");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return names;
    }

    public String getBrisCode(String trackName) {
        Connection connection = dbConnect.getDBConnection(getHost("adw")+ "/track",
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String brisCode = "";

        try {
            String query = "select bris_code from track.track where display_name='"+trackName+"';";
            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            rs.next();
            brisCode = rs.getString("bris_code");

        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return brisCode;
    }

    public HashMap<String, String> getRandomJockeyName(String trackCode, String raceDate, String race) {
        Connection connection = dbConnect.getDBConnection(getHost("adw")+ "/programs",
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> jockeyName = new HashMap<String, String>();

        try {
            String query = "SELECT f60, f61, f59 " + 
                    "FROM thoroughbred " + 
                    "WHERE f1 = ? AND f2 = ? AND f3 = ? " +
                    "ORDER BY RAND() " +
                    "LIMIT 1;";
            
            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);
            
            rs = st.executeQuery();
            rs.next();
            
            jockeyName.put("firstName", rs.getString("f60"));
            jockeyName.put("middleName", rs.getString("f61"));
            jockeyName.put("lastName", rs.getString("f59"));
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return jockeyName;
    }
    
    public HashMap<String, String> getRandomTrainerName(String trackCode, String raceDate, String race) {
        Connection connection = dbConnect.getDBConnection(getHost("adw")+ "/programs",
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> trainerName = new HashMap<String, String>();

        try {
            String query = "SELECT f53, f54, f52 " + 
                    "FROM thoroughbred " + 
                    "WHERE f1 = ? " + 
                    "AND f2 = ? " + 
                    "AND f3 = ?;";
            
            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);
            
            rs = st.executeQuery();
            rs.next();
            
            trainerName.put("firstName", rs.getString("f53"));
            trainerName.put("middleName", rs.getString("f54"));
            trainerName.put("lastName", rs.getString("f52"));
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return trainerName;
    }
    
    public String getWagerTypesForTrack(String brisCode, String race) {
        Connection connection = dbConnect.getDBConnection(getHost("adw")+ "/batch",
                sqlDataCredentials.getAdwUsername(), sqlDataCredentials.getAdwPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String wagerTypes = "";
        
        try {
            String query = "SELECT bet_types FROM races " + 
                    "WHERE program_name = ? " + 
                    "AND race = ?;";
            
            st = connection.prepareStatement(query);
            st.setString(1, brisCode);
            st.setString(2, race);
            
            rs = st.executeQuery();
            while(rs.next()) {
                wagerTypes = rs.getString("bet_types");
            }
        } catch (Exception ex) {
            System.out.println("Unable to get bet types");
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return wagerTypes;
    }

    public String getWagerCode(String wagerName) {

        Connection connection = dbConnect.getDBConnection(getHost("adw") + "/wager",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String wagerCode = "";

        try {
            String query = "select wager_code from wager_types where wager_name = ? Limit 1;";

            st = connection.prepareStatement(query);
            st.setString(1, wagerName);
            rs = st.executeQuery();

            while (rs.next()) {
                wagerCode = rs.getString("wager_code");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(st, connection);
        }

        return wagerCode;
    }
}