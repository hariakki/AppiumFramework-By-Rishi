package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FundingQueries extends SQLQueries {

    public void updatePendingEzmoneyTransaction(String accountNumber) {
        Connection connection = dbConnect.getDBConnection(getHost("funding") + "/funding",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE funding.FundsAudit SET Status = 'Completed'where AccountNumber = ? and " +
                    "(Status = 'Processing' or Status = 'Pending')";
            st = connection.prepareStatement(query);
            st.setString(1, accountNumber);
            st.executeUpdate();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            closeConnection(st, connection);
        }
    }

    public HashMap<String, String> getFundingTxDetails(int accountNumber, String date) {
        Connection connection = dbConnect.getDBConnection(getHost("funding") + "/funding",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> fundingDetails = new HashMap<String, String>();

        try {
            String query = "SELECT * FROM FundsAudit " +
                    "WHERE AccountNumber = ? " +
                    "AND TransactionDateTime LIKE ? " +
                    "ORDER BY TransactionDateTime DESC;";

            st = connection.prepareStatement(query);
            st.setInt(1, accountNumber);
            st.setString(2, date + "%");
            rs = st.executeQuery();

            while (rs.next()) {
                fundingDetails.put("fundType", rs.getString("FundType"));
                fundingDetails.put("txType", rs.getString("TransactionType"));
                fundingDetails.put("amount", rs.getString("Amount"));
                fundingDetails.put("fundTypeKey", rs.getString("FundTypeKey"));
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }

        return fundingDetails;
    }

    public String getEZMoneyLast4(String fundTypeKey) {
        Connection connection = dbConnect.getDBConnection(getHost("funding") + "/funding",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String lastFour = "";

        try {
            String query = "SELECT * FROM ezmoney_audit " +
                    "WHERE order_id = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, fundTypeKey);
            rs = st.executeQuery();

            while (rs.next()) {
                lastFour = rs.getString("bank_account");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }

        return lastFour;
    }

    public String getCreditCardLast4(String fundTypeKey) {
        Connection connection = dbConnect.getDBConnection(getHost("funding") + "/funding",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String lastFour = "";

        try {
            String query = "SELECT * FROM CreditCardAudit " +
                    "WHERE OrderId = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, fundTypeKey);
            rs = st.executeQuery();

            while (rs.next()) {
                lastFour = rs.getString("CardNum");
            }
        } catch (Exception ex) {
            System.out.println("Connection Failed");
        } finally {
            closeConnection(rs, st, connection);
        }

        return lastFour;
    }

    public List<String> getFundingMethodOrder() {
        Connection connection = dbConnect.getDBConnection(getHost("funding") + "/funding", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        List<String> raceResult = new ArrayList<>();

        try {
            String query = "SELECT FundCode FROM funding.FundOptions Where DisplayOrder > 0 order by DisplayOrder Asc";

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                raceResult.add(rs.getString("FundCode"));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            closeConnection(rs, st, connection);
        }

        return raceResult;
    }

    public String getPayPalFee() {
        Connection connection = dbConnect.getDBConnection(getHost("funding") +
                "/funding", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String payPalFee = null;

        try {
            String query = "Select * from funding.FundOptionsInfo where infoType = 2 " +
                    "and FundID = 18 and AccountNumber = 0 and affiliateID = 2800";

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                payPalFee = rs.getString("InfoValue");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            closeConnection(rs, st, connection);
        }

        return payPalFee;
    }
}
