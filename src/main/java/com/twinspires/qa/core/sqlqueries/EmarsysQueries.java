package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class EmarsysQueries extends SQLQueries {
    
    public HashMap<String, String> getDepositBonusDetails(String bonusId) {
        Connection connection = getEmarsysConnection("compose_"+ testEnv.toString().toLowerCase());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> depositBonusDetails = new HashMap<String, String>();

        try {
            String query = "SELECT id_person, id_assignment_status, id_assignment_opt_out, "
                    + "amount_deposit, id_bonus_template "
                    + "FROM bonus_assignment " 
                    + "WHERE id_bonus = ?";

            st = connection.prepareStatement(query);
            st.setString(1, bonusId);
            
            rs = st.executeQuery();
            while (rs.next()) {
                depositBonusDetails.put("camId", rs.getString("id_person"));
                depositBonusDetails.put("bonusStatus", rs.getString("id_assignment_status"));
                depositBonusDetails.put("optOutStatus", rs.getString("id_assignment_opt_out"));
                depositBonusDetails.put("depositAmount", rs.getString("amount_deposit"));
                depositBonusDetails.put("bonusTemplate", rs.getString("id_bonus_template"));
            }
        } catch(Exception ex) {
            System.out.println(ex.getStackTrace());
            System.out.println("Unable to get Bonus Data for bonus: " + bonusId);
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return depositBonusDetails;
    }
    
    public HashMap<String, String> getCashDropDetails(String bonusId) {
        Connection connection = getEmarsysConnection("compose_"+ testEnv.toString().toLowerCase());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> cashDropDetails = new HashMap<String, String>();

        try {
            String query = "SELECT id_person, id_assignment_status, id_assignment_opt_out, "
                    + "amount_assigned, id_bonus_template "
                    + "FROM bonus_assignment " 
                    + "WHERE id_bonus = ?";

            st = connection.prepareStatement(query);
            st.setString(1, bonusId);
            
            rs = st.executeQuery();
            while (rs.next()) {
                cashDropDetails.put("camId", rs.getString("id_person"));
                cashDropDetails.put("bonusStatus", rs.getString("id_assignment_status"));
                cashDropDetails.put("optOutStatus", rs.getString("id_assignment_opt_out"));
                cashDropDetails.put("assignedAmount", rs.getString("amount_assigned"));
                cashDropDetails.put("bonusTemplate", rs.getString("id_bonus_template"));
            }
        } catch(Exception ex) {
            System.out.println(ex.getStackTrace());
            System.out.println("Unable to get Bonus Data for bonus: " + bonusId);
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return cashDropDetails;
    }
    
    public HashMap<String, String> getExpiredBonusDetails(String bonusId) {
        Connection connection = getEmarsysConnection("compose_"+ testEnv.toString().toLowerCase());
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> expiredBonusDetails = new HashMap<String, String>();

        try {
            String query = "SELECT id_person, id_assignment_status, id_assignment_opt_out, "
                    + "id_bonus_template "
                    + "FROM bonus_assignment " 
                    + "WHERE id_bonus = ?";

            st = connection.prepareStatement(query);
            st.setString(1, bonusId);
            
            rs = st.executeQuery();
            while (rs.next()) {
                expiredBonusDetails.put("camId", rs.getString("id_person"));
                expiredBonusDetails.put("bonusStatus", rs.getString("id_assignment_status"));
                expiredBonusDetails.put("optOutStatus", rs.getString("id_assignment_opt_out"));
                expiredBonusDetails.put("bonusTemplate", rs.getString("id_bonus_template"));
            }
        } catch(Exception ex) {
            System.out.println(ex.getStackTrace());
            System.out.println("Unable to get Bonus Data for bonus: " + bonusId);
        } finally {
            closeConnection(rs, st, connection);
        }
        
        return expiredBonusDetails;
    }

}
