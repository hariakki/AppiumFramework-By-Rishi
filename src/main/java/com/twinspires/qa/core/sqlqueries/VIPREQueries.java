package com.twinspires.qa.core.sqlqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class VIPREQueries extends SQLQueries {

    public void deleteBonus(String bonusId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "CALL bonus.p_delete_bonus(" + bonusId + ")";
            st = connection.prepareStatement(query);
            rs = st.executeQuery();

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        System.out.println("Bonus " + bonusId + " deleted");
    }

    public void updateBonusRedemption(String bonusId, String camId, String date, String wagerAmt, String winAmt) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "CALL p_update_bonus_redemption('hra', '2', " + camId + ", " + bonusId + ", " + wagerAmt
                    + ", " + winAmt + ", null, 1, 'WAC', 0, null, 'WIN', 'TG1', 1, '" + date + "', null, 'TS.com');";
            st = connection.prepareStatement(query);
            rs = st.executeQuery();

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
    }

    public void deletePromo(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "CALL vip.p_delete_vip_campaign(?)";
            st = connection.prepareStatement(query);
            st.setString(1, promoId);
            rs = st.executeQuery();

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        System.out.println("Promo " + promoId + " deleted");
    }

    public void deletePromoActivation(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "delete from vip.vip_campaign_assignment where id_vip_campaign =(?)";
            st = connection.prepareStatement(query);
            st.setString(1, promoId);
            rs = st.executeQuery();

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
    }

    public void removeActivePromo(String offersHistory, String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "DELETE FROM vip.vip_campaign_assignment\n" +
                    "Where id_person = ? and id_vip_campaign = ?";
            st = connection.prepareStatement(query);
            st.setString(1, offersHistory);
            st.setString(2, promoId);
            st.executeUpdate();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        } finally {
            closeConnection(rs, st, connection);
        }
    }

    public void insertPromoRedemptionCode(String promoId, String redemptionCode) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE vip.vip_campaign SET redemption_code = ? WHERE  id_vip_campaign = ?";
            st = connection.prepareStatement(query);
            st.setString(1, redemptionCode);
            st.setString(2, promoId);
            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updatePromoDisplayTimeStamp(String promoId, String timeStamp) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE `vip`.`vip_campaign` SET `display_time_stamp_start`=? WHERE  `id_vip_campaign`=?;";
            st = connection.prepareStatement(query);
            st.setString(1, timeStamp);
            st.setString(2, promoId);
            st.executeUpdate();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            closeConnection(st, connection);
        }
    }

    public String getPromoDisplayName(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String displayName = "";

        try {
            String query = "SELECT display_vip_campaign FROM vip.vip_campaign Where id_vip_campaign = ?";
            st = connection.prepareStatement(query);
            st.setString(1, promoId);

            rs = st.executeQuery();
            if (rs.next()) {
                displayName = rs.getString("display_vip_campaign");
            } else {
                displayName = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return displayName;
    }

    public String getBonusFieldValue(String bonusId, String field) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String displayName = "";

        try {
            String query = "SELECT " + field + " FROM bonus.bonus Where id_bonus = ?";
            st = connection.prepareStatement(query);
            st.setString(1, bonusId);

            rs = st.executeQuery();
            if (rs.next()) {
                displayName = rs.getString(field);
            } else {
                displayName = null;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return displayName;
    }

    //TODO: Implement later
    public String getBonusAmount(String bonusId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String displayName = "";

        try {
            String query = "SELECT 'amount_deposit' FROM bonus.bonus_assignment Where id_bonus = ?";
            st = connection.prepareStatement(query);
            st.setString(1, bonusId);

            rs = st.executeQuery();
            if (rs.next()) {
                displayName = rs.getString("amount_deposit");
            } else {
                displayName = null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            closeConnection(rs, st, connection);
        }

        return displayName;
    }



    public void insertBonusRedemptionCode(String bonusId, String redemptionCode) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE bonus.bonus SET redemption_code = ? WHERE id_bonus = ?";
            st = connection.prepareStatement(query);
            st.setString(1, redemptionCode);
            st.setString(2, bonusId);
            st.executeUpdate();

        }  catch (Exception ex) {
            System.out.println(ex);

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updateBonusDisplayTimeStamp(String bonusId, String timeStamp) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE bonus.bonus SET time_stamp =?, time_stamp_start =?, " +
                    "play_through_time_stamp_start = ? WHERE id_bonus = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, timeStamp);
            st.setString(2, timeStamp);
            st.setString(3, timeStamp);
            st.setString(4, bonusId);
            st.executeUpdate();

        }  catch (Exception ex) {
            System.out.println(ex);
        } finally {
            closeConnection(st, connection);
        }
    }

    public void updateBonusIssueMode(String bonusId, String issueMode) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE bonus.bonus SET id_bonus_issue_mode =? WHERE id_bonus = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, issueMode);
            st.setString(2, bonusId);
            st.executeUpdate();

        }  catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updatePromoEndDate(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/vip",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:00");
        Date now = new Date();
        String timeStampEnd = simpleDateFormat.format(now);
        try {
            String query = "UPDATE vip.vip_campaign SET time_stamp_end=? WHERE  id_vip_campaign = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, timeStampEnd);
            st.setString(2, promoId);
            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updateBonusExpired(String bonusId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:00");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String timeStampStart = yesterday.toString() + " 00:00:00";
        String timeStampEnd = yesterday.toString() + " 23:59:00";

        try {
            String query = "UPDATE bonus.bonus SET time_stamp_start = ?, time_stamp_end = ?, id_bonus_status = ?, " +
                    "play_through_time_stamp_end = ? WHERE id_bonus = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, timeStampStart);
            st.setString(2, timeStampEnd);
            st.setString(3, "exp");
            st.setString(4, timeStampEnd);
            st.setString(5, bonusId);

            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updateBonusOptedOut(String bonusId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE bonus.bonus_assignment SET id_bonus_assignment_status = ?, id_bonus_assignment_opt_out = ? " +
                    "WHERE id_bonus = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, "opu");
            st.setString(2, "exp");
            st.setString(3, bonusId);

            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public void updatePromoOptedOut(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus",
                sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;

        try {
            String query = "UPDATE vip.vip_campaign_assignment SET id_vip_campaign_assignment_status = ?" +
                    "WHERE id_vip_campaign = ?;";
            st = connection.prepareStatement(query);
            st.setString(1, "opu");
            st.setString(2, promoId);

            st.executeUpdate();

        } catch (Exception ex) {

        } finally {
            closeConnection(st, connection);
        }
    }

    public String getBonusUUID(String bonusId){
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String UUID = "";

        try {
            String query = "SELECT UUID FROM bonus.bonus where id_bonus = ?";
            st = connection.prepareStatement(query);
            st.setString(1, bonusId);

            rs = st.executeQuery();
            if (rs.next()) {
                UUID = rs.getString("UUID");
            } else {
                UUID = null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            closeConnection(rs, st, connection);
        }

        return UUID;
    }

    public String getPromoUUID(String promoId) {
        Connection connection = dbConnect.getDBConnection(getHost("vipre") + "/bonus", sqlDataCredentials.getUsername(), sqlDataCredentials.getPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String UUID = "";

        try {
            String query = "SELECT UUID FROM vip.vip_campaign where id_vip_campaign = ?";
            st = connection.prepareStatement(query);
            st.setString(1, promoId);

            rs = st.executeQuery();
            if (rs.next()) {
                UUID = rs.getString("UUID");
            } else {
                UUID = null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            closeConnection(rs, st, connection);
        }

        return UUID;
    }
}
