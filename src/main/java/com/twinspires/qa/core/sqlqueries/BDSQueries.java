package com.twinspires.qa.core.sqlqueries;

import com.twinspires.qa.core.enums.ProgramRating;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.testobjects.CategoryStats;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BDSQueries extends SQLQueries {
    /* This will return the list of columns requested by program number (asc)
     *
     * Note 1: program_number in db is char(3) which can be string as '1A ', ' 1A', '  1', '1  ', ' 1 '...
     *         That is why new_program was created to trim possible spaces for comparison and in case program_number
     *         need to be preserved to be as it is in the list requested.
     *
     * Note 2: Send in columnList in a comma delimited fashion like 'horse_name, yob, sex, color, ..., breeder' for example.
     */
    private String selectProgramColumns(String columnList) {
        return "SELECT " + columnList + ", trim(program_number) as new_program " +
                "FROM program_entry_race r inner join program_entry_start s on (s.program_entry_race_id = r.entry_race_id) " +
                "WHERE r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? " +
                "ORDER BY new_program+0, length(new_program), right(new_program, 1);";
    }

    public List<String> getTrackCodes(List<String> trackNames) {
        List<String> trackCodes = new ArrayList<>();
        String tracks = "";
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String trackName = "";

        for (int i = 0; i < trackNames.size(); i++) {
            trackName = (trackNames.get(i)).replace("(H)", "").trim();

            if (trackName.equals("Hong Kong Late")) {
                trackName = "'Hong Kong - Late'";
            } else if (trackName.equals("UK Wolverhampton")) {
                trackName = "'Wolverhampton'";
            } else if (trackName.contains("(G)") || trackName.contains("G.H.")) {
                //Greyhound track, only take first 13 characters
                trackName = "'" + trackName.substring(0, 13) + "'";
            } else {
                trackName = "'" + trackName + "'";
            }
             tracks = tracks  + trackName + (i == trackNames.size() - 1 ? "" : ",");
        }

        try {
            String query = "SELECT track_id FROM bds_track_codes WHERE name in (" + tracks + ");";
            st = connection.prepareStatement(query);

            rs = st.executeQuery();
            while (rs.next()) {
                trackCodes.add(rs.getString("track_id"));
            }

        } catch (Exception ex) {
            System.out.println("DEBUG:: Error: ");
            System.out.println(ex.getMessage());
            System.out.println("-------------------------");
            System.out.println(ex.getStackTrace().toString());
        } finally {
            closeConnection(rs, st, connection);
        }

        return trackCodes;
    }

    public String getTrackCode(String trackName) {
        return getTrackCodes(Collections.singletonList(trackName)).get(0);
    }

    /**
     * This method takes the track name as shown on the mobile wagering page
     * and returns the display name as shown on the Account History page
     *
     * @param wagerTrackName
     * @return displayTrackName
     */
    public String getTrackDisplayName(String wagerTrackName, String env) {

        String displayTrackName = "";
        String brisCode = "";
        Connection connection = null;
        Connection connection2 = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        if (env.equals(TestEnv.ITE) || env.equals(TestEnv.STE)) {
            try {
                connection = dbConnect.getDBConnection("10.20.5.17" + "/bris_migration_v5",
                        sqlDataCredentials.getBDSUsername(), sqlDataCredentials.getBDSPassword());
                //uses a stored procedure that only exists on ITE but the data is the same across ITE and STE.
                //Gets display track name from BDS
                cs = connection.prepareCall("{call tmp_select_track_name(?)}");
                cs.setString(1, wagerTrackName);
                cs.execute();

                rs = cs.getResultSet();
                while (rs.next()) {
                    displayTrackName = rs.getString(1);
                }

            } catch (SQLException e) {
                System.out.println("Unable to call stored procedure");
                e.printStackTrace();
            } finally {
                closeConnection(rs, connection);
            }
        } else { //env == prod
            try {
                connection = dbConnect.getDBConnection("10.20.5.1" + "/bris_migration_v5",
                        "kdeaton", "kD3@t0n");
                connection2 = dbConnect.getDBConnection("172.20.19.203" + "/track",
                        "qa_kdeaton", "bQGYpKgqiPkagxRxkmWFF3zB");

                //Gets bris_code from ADW
                String query = "SELECT bris_code FROM track.track WHERE display_name = ?;";
                st = connection2.prepareStatement(query);
                st.setString(1, wagerTrackName);

                rs = st.executeQuery();
                if (rs.next()) {
                    brisCode = rs.getString("bris_code");
                }
                st.close();

                //Gets display track name from BDS
                query = "SELECT bts.name FROM bds_track_codes bts " +
                        "inner join bds_track_code_aliases bta " +
                        "on (bta.bds_track_code_id = bts.id and bta.source = 'bris') " +
                        "WHERE bta.source_track_code = ? " +
                        "limit 1;";

                st = connection.prepareStatement(query);
                st.setString(1, brisCode);

                rs = st.executeQuery();
                if (rs.next()) {
                    displayTrackName = rs.getString("name");
                }
            } catch (SQLException e) {
                System.out.println("Unable to get display track name");
                e.printStackTrace();
            } finally {
                closeConnection(rs, st, connection);
            }
        }

        if (displayTrackName.isEmpty()) {
            displayTrackName = wagerTrackName;
        }
        return displayTrackName;
    }

    public boolean isValidTrack(String trackName) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean trackExists = false;

        try {

            String query = "select 1 from bds_track_codes where name = ?";

            st = connection.prepareStatement(query);
            st.setString(1, trackName);

            rs = st.executeQuery();
            if (rs.next()) {
                trackExists = true;
            } else {
                trackExists = false;
            }

        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return trackExists;
    }

    public ArrayList<String> getRaceResult(String trackCode, String raceDate, int raceNumber) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<String> raceResult = new ArrayList<String>();
        ArrayList<String> scratchedHorses = new ArrayList<String>();
        try {

            String query = "select official_position, horse_name from instant_chart_start " +
                    "where track_id = ? and race_date= ? and race_number= ? " +
                    "order by official_position asc;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setInt(3, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {

                if (rs.getInt("official_position") == 0) {
                    scratchedHorses.add(rs.getString("horse_name"));
                } else {
                    raceResult.add(rs.getString("horse_name"));
                }
            }

            if (scratchedHorses.size() != 0) {
                Collections.sort(scratchedHorses);
                raceResult.addAll(scratchedHorses);
            }
        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }
        return raceResult;
    }

    public ArrayList<String> getBDSHorseNames(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<String> names = new ArrayList<String>();

        try {
            String query = selectProgramColumns("horse_name");

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();

            while (rs.next()) {
                names.add(rs.getString("horse_name"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get horse names");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return names;
    }

    public HashMap<String, List<String>> getHorseColorsNSex(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        HashMap<String, List<String>> sexColor = new HashMap<>();
        sexColor.put("color", new ArrayList<>());
        sexColor.put("sex", new ArrayList<>());
        sexColor.put("sexCode", new ArrayList<>());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "SELECT s.sex as sex_code, v.description as sex, " +
                    "ifnull(c.description, '') as color, trim(program_number) as new_program " +
                    "FROM program_entry_race r inner join program_entry_start s on (s.program_entry_race_id = r.entry_race_id) " +
                    "left join colorcodes c on (c.eqbcode = s.color AND c.breed = 'TB') " +
                    "left join column_value v on (v.table_name = 'horse' and v.column_name = 'sex' and v.value = s.sex) " +
                    "WHERE r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? " +
                    "ORDER BY new_program+0, length(new_program), right(new_program, 1);";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();

            while (rs.next()) {
                sexColor.get("color").add(rs.getString("color"));
                sexColor.get("sex").add(rs.getString("sex"));
                sexColor.get("sexCode").add(rs.getString("sex_code"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get horse colors OR sex");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        return sexColor;
    }

    public int getNumHorses(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        int count = 0;

        try {
            String query = "SELECT count(1) as HrsCnt " +
                    "FROM program_entry_race r inner join program_entry_start s on (s.program_entry_race_id = r.entry_race_id) " +
                    "WHERE r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? ";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();

            while (rs.next()) {
                count = rs.getInt("HrsCnt");
            }
        } catch (Exception e) {
            System.out.println("Unable to get the number or horses");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return count;
    }

    //TODO: Replace getMorningLineOdds() getProfitLineOdds() with this one after OddsTest Settled.
    public HashMap<String, List<String>> getOdds(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        HashMap<String, List<String>> odds = new HashMap<>();
        odds.put("profitLineOdds", new ArrayList<>());
        odds.put("morningLineOdds", new ArrayList<>());

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = selectProgramColumns("ifnull(pl_fair_value, '-') as plOdds, " +
                    "replace(morning_line_odds, '/1', '') as mlOdds");

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                odds.get("profitLineOdds").add(rs.getString("plOdds"));
                odds.get("morningLineOdds").add(rs.getString("mlOdds"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get Odds");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        return odds;
    }


    public HashMap<String, List<String>> getAlsoRans(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        HashMap<String, List<String>> alsoRans = new HashMap<>();
        alsoRans.put("programNumber", new ArrayList<>());
        alsoRans.put("horseName", new ArrayList<>());
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "SELECT horse_name, if(official_position = 0, 99, official_position) as official_position, " +
                    " trim(program_number) as new_program " +
                    "FROM instant_chart_start " +
                    "WHERE track_id = ? and race_date = current_date() and race_number = ? " +
                    "and (official_position > 3 OR official_position = 0)  " +
                    "ORDER BY official_position, new_program+0, length(new_program), right(new_program, 1);";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                alsoRans.get("programNumber").add(rs.getString("new_program"));
                alsoRans.get("horseName").add(rs.getString("horse_name"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get Also Rans");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        return alsoRans;
    }

    public HashMap<ProgramRating, List<String>> getRatingDetails(String trackCode, int raceNumber) {

        HashMap<ProgramRating, List<String>> ratingList = new HashMap<>();

        ratingList.put(ProgramRating.DAYS_OFF, new ArrayList<>());
        ratingList.put(ProgramRating.AVERAGE_CLASS, new ArrayList<>());
        ratingList.put(ProgramRating.LAST_CLASS, new ArrayList<>());
        ratingList.put(ProgramRating.PRIME_POWER, new ArrayList<>());
        ratingList.put(ProgramRating.RUN_STYLE, new ArrayList<>());
        ratingList.put(ProgramRating.EARLY_PACE_1, new ArrayList<>());
        ratingList.put(ProgramRating.EARLY_PACE_2, new ArrayList<>());
        ratingList.put(ProgramRating.LATE_PACE, new ArrayList<>());
        ratingList.put(ProgramRating.AVERAGE_SPEED, new ArrayList<>());
        ratingList.put(ProgramRating.AVERAGE_DISTANCE, new ArrayList<>());
        ratingList.put(ProgramRating.BEST_SPEED, new ArrayList<>());

        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        String query = selectProgramColumns("days_off," +
                "ifnull(round(average_class), 0) as average_class," +
                "ifnull(round(last_class), 0) as last_class," +
                "ifnull(round(prime_power), 0) as prime_power," +
                "prior_run_style, speed_points, average_pace_e1, average_pace_e2, average_pace_lp, " +
                "ifnull(average_speed_last3, 0) as average_speed_last3, " +
                "ifnull(average_speed, 0) as average_speed, " +
                "ifnull(best_speed_distance, 0) as best_speed_distance");

        try {
            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setInt(2, raceNumber);
            rs = st.executeQuery();

            while (rs.next()) {
                ratingList.get(ProgramRating.DAYS_OFF).add(rs.getString("days_off"));
                ratingList.get(ProgramRating.RUN_STYLE).add(rs.getString("prior_run_style") + ' ' + rs.getString("speed_points"));

                if (rs.getString("new_program").equalsIgnoreCase("SCR")) {
                    ratingList.get(ProgramRating.AVERAGE_CLASS).add("-");
                    ratingList.get(ProgramRating.LAST_CLASS).add("-");
                    ratingList.get(ProgramRating.AVERAGE_SPEED).add("-");
                    ratingList.get(ProgramRating.BEST_SPEED).add("-");
                    ratingList.get(ProgramRating.EARLY_PACE_1).add("-");
                    ratingList.get(ProgramRating.EARLY_PACE_2).add("-");
                    ratingList.get(ProgramRating.LATE_PACE).add("-");
                    ratingList.get(ProgramRating.PRIME_POWER).add("-");
                    ratingList.get(ProgramRating.AVERAGE_DISTANCE).add("-");
                } else {
                    ratingList.get(ProgramRating.AVERAGE_CLASS).add(rs.getString("average_class"));
                    ratingList.get(ProgramRating.LAST_CLASS).add(rs.getString("last_class"));
                    ratingList.get(ProgramRating.AVERAGE_SPEED).add(rs.getString("average_speed_last3"));
                    ratingList.get(ProgramRating.BEST_SPEED).add(rs.getString("best_speed_distance"));
                    ratingList.get(ProgramRating.EARLY_PACE_1).add(rs.getString("average_pace_e1"));
                    ratingList.get(ProgramRating.EARLY_PACE_2).add(rs.getString("average_pace_e2"));
                    ratingList.get(ProgramRating.LATE_PACE).add(rs.getString("average_pace_lp"));
                    ratingList.get(ProgramRating.PRIME_POWER).add(rs.getString("prime_power"));
                    ratingList.get(ProgramRating.AVERAGE_DISTANCE).add(rs.getString("average_speed"));
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to get Ratings");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return ratingList;

    }

    public HashMap<String, String> getHorseStats(String horseName, String trackCode, String race) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> horseStats = new HashMap<String, String>();
        String raceDate = getTodaysDate();

        try {
            String query = "SELECT horse_name, sire, dam_name, ds_name, " +
                    "c.description as horse_color, sex, horse_yob, " +
                    "breeder_name, wherebred, jockey_first_name, jockey_middle_name, jockey_last_name, " +
                    "trainer_first_name, trainer_middle_name, trainer_last_name, horse_lt_start_count, " +
                    "horse_lt_win_count, horse_lt_places_count, horse_lt_shows_count, horse_lt_earnings, " +
                    "horse_cyr_start_count, horse_cyr_win_count, horse_cyr_places_count, " +
                    "horse_cyr_shows_count, horse_cyr_earnings, horse_pyr_start_count, " +
                    "horse_pyr_win_count, horse_pyr_places_count, horse_pyr_shows_count, " +
                    "horse_pyr_earnings, horse_lt_track_start_count, horse_lt_track_win_count, " +
                    "horse_lt_track_places_count, horse_lt_track_shows_count, horse_lt_track_earnings, " +
                    "horse_lt_fastdirt_start_count, horse_lt_fastdirt_wins_count, " +
                    "horse_lt_fastdirt_places_count, horse_lt_fastdirt_shows_count, " +
                    "horse_lt_fastdirt_earnings, horse_lt_track_QH_start_count, horse_lt_track_QH_win_count," +
                    "horse_lt_track_QH_places_count, horse_lt_track_QH_shows_count, " +
                    "horse_lt_track_QH_earnings, horse_lt_allweather_start_count, " +
                    "horse_lt_allweather_wins_count, horse_lt_allweather_places_count, " +
                    "horse_lt_allweather_shows_count, horse_lt_allweather_earnings, " +
                    "horse_lt_mudsloppy_start_count, horse_lt_mudsloppy_win_count, " +
                    "horse_lt_mudsloppy_places_count, horse_lt_mudsloppy_shows_count, " +
                    "horse_lt_mudsloppy_earnings " +
                    "FROM hcp_entry_start s left outer join colorcodes c on (c.eqbcode = s.horse_color and c.breed = 'tb') " +
                    "WHERE track_id = ? and race_date = ? and race_number = ? and horse_name = ?;";

            st = connection.prepareStatement(query);

            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);
            st.setString(4, horseName);

            rs = st.executeQuery();
            while (rs.next()) {
                horseStats.put("name", rs.getString("horse_name"));
                horseStats.put("sire", rs.getString("sire"));
                horseStats.put("dam", rs.getString("dam_name"));
                horseStats.put("damsSire", rs.getString("ds_name"));
                horseStats.put("color", rs.getString("horse_color"));
                horseStats.put("sex", rs.getString("sex"));
                horseStats.put("birthYear", rs.getString("horse_yob"));
                horseStats.put("breeder", rs.getString("breeder_name") + " (" +
                        rs.getString("wherebred") + ")");
                if (rs.getString("jockey_middle_name").isEmpty()) {
                    horseStats.put("jockey", rs.getString("jockey_first_name") + " " +
                            rs.getString("jockey_last_name"));
                } else {
                    horseStats.put("jockey", rs.getString("jockey_first_name") + " " +
                            rs.getString("jockey_middle_name") + " " + rs.getString("jockey_last_name"));
                }
                if (rs.getString("trainer_middle_name").isEmpty()) {
                    horseStats.put("trainer", rs.getString("trainer_first_name") + " " +
                            rs.getString("trainer_last_name"));
                } else {
                    horseStats.put("trainer", rs.getString("trainer_first_name") + " " +
                            rs.getString("trainer_middle_name") + " " + rs.getString("trainer_last_name"));
                }
                horseStats.put("lifetimeStarts", rs.getString("horse_lt_start_count"));
                horseStats.put("lifetimeWins", rs.getString("horse_lt_win_count"));
                horseStats.put("lifetimePlaces", rs.getString("horse_lt_places_count"));
                horseStats.put("lifetimeShows", rs.getString("horse_lt_shows_count"));
                horseStats.put("lifetimeEarnings", rs.getString("horse_lt_earnings"));
                horseStats.put("currentYearStarts", rs.getString("horse_cyr_start_count"));
                horseStats.put("currentYearWins", rs.getString("horse_cyr_win_count"));
                horseStats.put("currentYearPlaces", rs.getString("horse_cyr_places_count"));
                horseStats.put("currentYearShows", rs.getString("horse_cyr_shows_count"));
                horseStats.put("currentYearEarnings", rs.getString("horse_cyr_earnings"));
                horseStats.put("previousYearStarts", rs.getString("horse_pyr_start_count"));
                horseStats.put("previousYearWins", rs.getString("horse_pyr_win_count"));
                horseStats.put("previousYearPlaces", rs.getString("horse_pyr_places_count"));
                horseStats.put("previousYearShows", rs.getString("horse_pyr_shows_count"));
                horseStats.put("previousYearEarnings", rs.getString("horse_pyr_earnings"));
                horseStats.put("lifetimeStartsTrack", rs.getString("horse_lt_track_start_count"));
                horseStats.put("lifetimeWinsTrack", rs.getString("horse_lt_track_win_count"));
                horseStats.put("lifetimePlacesTrack", rs.getString("horse_lt_track_places_count"));
                horseStats.put("lifetimeShowsTrack", rs.getString("horse_lt_track_shows_count"));
                horseStats.put("lifetimeEarningsTrack", rs.getString("horse_lt_track_earnings"));
                horseStats.put("lifetimeStartsDirt", rs.getString("horse_lt_fastdirt_start_count"));
                horseStats.put("lifetimeWinsDirt", rs.getString("horse_lt_fastdirt_wins_count"));
                horseStats.put("lifetimePlacesDirt", rs.getString("horse_lt_fastdirt_places_count"));
                horseStats.put("lifetimeShowsDirt", rs.getString("horse_lt_fastdirt_shows_count"));
                horseStats.put("lifetimeEarningsDirt", rs.getString("horse_lt_fastdirt_earnings"));
                horseStats.put("lifetimeStartTurf", rs.getString("horse_lt_track_QH_start_count"));
                horseStats.put("lifetimeWinsTurf", rs.getString("horse_lt_track_QH_win_count"));
                horseStats.put("lifetimePlacesTurf", rs.getString("horse_lt_track_QH_places_count"));
                horseStats.put("lifetimeShowsTurf", rs.getString("horse_lt_track_QH_shows_count"));
                horseStats.put("lifetimeEarningsTurf", rs.getString("horse_lt_track_QH_earnings"));
                horseStats.put("lifetimeStartsAllWeather", rs.getString("horse_lt_allweather_start_count"));
                horseStats.put("lifetimeWinsAllWeather", rs.getString("horse_lt_allweather_wins_count"));
                horseStats.put("lifetimePlacesAllWeather", rs.getString("horse_lt_allweather_places_count"));
                horseStats.put("lifetimeShowsAllWeather", rs.getString("horse_lt_allweather_shows_count"));
                horseStats.put("lifetimeEarningsAllWeather", rs.getString("horse_lt_allweather_earnings"));
                horseStats.put("lifetimeStartsOffDirt", rs.getString("horse_lt_mudsloppy_start_count"));
                horseStats.put("lifetimeWinsOffDirt", rs.getString("horse_lt_mudsloppy_win_count"));
                horseStats.put("lifetimePlacesOffDirt", rs.getString("horse_lt_mudsloppy_places_count"));
                horseStats.put("lifetimeShowsOffDirt", rs.getString("horse_lt_mudsloppy_shows_count"));
                horseStats.put("lifetimeEarningsOffDirt", rs.getString("horse_lt_mudsloppy_earnings"));
                horseStats.put("programBreeder", rs.getString("breeder_name"));
                horseStats.put("programWhereBred", rs.getString("wherebred"));
            }

        } catch (Exception e) {
            System.out.println("Unable to get Horse Stats");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return horseStats;
    }

    public HashMap<String, String> getHorsePastRace(String horseName) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> pastRace = new HashMap<String, String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yy");
        Date date = new Date();

        try {
            String query = "SELECT race_date, track_id, race_number, official_position, short_comment " +
                    "FROM  horse, start " +
                    "WHERE horse.horse_name = ?  and start.bris_id = horse.bris_id " +
                    "ORDER BY race_date DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setString(1, horseName);

            rs = st.executeQuery();
            while (rs.next()) {
                date = sdf.parse(rs.getString("race_date"));
                pastRace.put("date", sdf2.format(date));
                if (rs.getString("race_number").length() > 1) {
                    pastRace.put("track", rs.getString("track_id") + " - " +
                            rs.getString("race_number"));
                } else {
                    pastRace.put("track", rs.getString("track_id") + " - 0" +
                            rs.getString("race_number"));
                }
                if (rs.getString("official_position").equals("0")) {
                    pastRace.put("finish", "SCR");
                } else {
                    pastRace.put("finish", rs.getString("official_position"));
                }
                pastRace.put("comment", rs.getString("short_comment").replace(",", ", "));
            }

        } catch (Exception e) {
            System.out.println("Unable to get Most Recent Race");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return pastRace;
    }

    public HashMap<String, String> getHorseWorkout(String horseName, String trackCode, String race) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> workout = new HashMap<String, String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yy");
        Date date = new Date();

        try {
            String query = "SELECT date, track_id, distance_id, course_type, " +
                    "track_condition, timing, ranking, number_in_ranking_group " +
                    "FROM horse, workout " +
                    "WHERE horse.horse_name = ? and workout.bris_id = horse.bris_id " +
                    "ORDER BY DATE DESC " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setString(1, horseName);

            rs = st.executeQuery();
            while (rs.next()) {
                date = sdf.parse(rs.getString("date"));
                workout.put("date", sdf2.format(date));
                workout.put("track", rs.getString("track_id"));
                workout.put("distance", rs.getString("distance_id"));
                workout.put("surface", rs.getString("course_type"));
                workout.put("condition", rs.getString("track_condition"));
                workout.put("time", rs.getString("timing"));
                workout.put("rank", rs.getString("ranking") + " of " +
                        rs.getString("number_in_ranking_group"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get Most Recent Workout");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return workout;
    }

    public String getBrisId(String horseName) {
        Connection connection = getBDSConnection();

        PreparedStatement st = null;
        ResultSet rs = null;
        String brisId = "";

        try {
            String query = "SELECT bris_id FROM horse WHERE horse_name  = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, horseName);

            rs = st.executeQuery();
            while (rs.next()) {
                brisId = rs.getString("bris_id");
            }
        } catch (Exception e) {
            System.out.println("Unable to get Bris ID");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return brisId;
    }

    public HashMap<String, String> getRandomSireNDam(String track, String race) {
        HashMap<String, String> sireNdam = new HashMap<>();
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "SELECT s.sire_name, s.sire_id, s.dam_name, s.dam_id " +
                    "FROM program_entry_race r inner join program_entry_start s on (s.program_entry_race_id = r.entry_race_id) " +
                    "WHERE r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? " +
                    "ORDER BY rand() " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setString(1, track);
            st.setString(2, race);

            rs = st.executeQuery();
            if (rs.next()) {
                sireNdam.put("sireName", rs.getString("sire_name"));
                sireNdam.put("damName", rs.getString("dam_name"));
                sireNdam.put("sireId", rs.getString("sire_id"));
                sireNdam.put("damId", rs.getString("dam_id"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get Sire");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return sireNdam;
    }

    public String getTrackName(String trackCode) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String trackName = "";

        try {

            if (trackCode.equalsIgnoreCase("LTHX")) {
                trackCode = "LTH";
            } else if (trackCode.equalsIgnoreCase("ETHX")) {
                trackCode = "ETH";
            }

            String query = "select name from bds_track_codes where track_id = ?";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);

            rs = st.executeQuery();
            if (rs.next()) {
                trackName = rs.getString("name");
            } else {
                trackName = null;
            }
        } catch (Exception ex) {

        } finally {
            closeConnection(rs, st, connection);
        }

        return trackName;
    }

    //TODO: Rework when time allows (if the proc changes)
    public List<HashMap<String, Object>> getJockeyDetails(int jockeyId) {
        Connection connection = getBDSConnection();
        CallableStatement cs = null;
        ResultSet rs = null;
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();

        try {
            String proc = "CALL `p_select_jockey_stats`(?)";
            cs = connection.prepareCall(proc);
            cs.setInt(1, jockeyId);
            cs.execute();

            //stored proc returns two result sets-
            //calling getResultSet(collection), then moreResults(closesResults)-
            // then resultSet(collection2) again returns the second page
            rs = cs.getResultSet();
            cs.getMoreResults();
            rs = cs.getResultSet();

            ResultSetMetaData meta = null;
            meta = rs.getMetaData();

            int colCount = meta.getColumnCount();
            List<String> cols = new ArrayList<String>();
            for (int index = 1; index <= colCount; index++) {
                cols.add(meta.getColumnName(index));
            }

            //removing id column
            cols.remove(0);
            cols.set(0, "category");

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (String colName : cols) {
                    Object val = rs.getObject(colName);
                    row.put(colName, val);
                }

                rows.add(row);
            }
        } catch (Exception e) {
            System.out.println("Unable to get Jockey Details");
            e.printStackTrace();
        } finally {
            closeConnection(rs, cs, connection);
        }

        return rows;
    }

    //TODO: This will replace other Proc to get Trainer/Jocky later
    public HashMap<String, String> getJockeyNTrainerByHorse(String trackCode, String raceNumber, String horseName) {
        Connection connection = getBDSConnection();
        HashMap<String, String> jockey = new HashMap<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "select jockey_first_name, jockey_middle_name, jockey_last_name, jockey_id, " +
                    "trainer_first_name, trainer_middle_name, trainer_last_name, trainer_id " +
                    "from program_entry_race r, program_entry_start s " +
                    "where r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? " +
                    "and s.program_entry_race_id = r.entry_race_id and s.horse_name = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);
            st.setString(3, horseName);

            rs = st.executeQuery();
            while (rs.next()) {
                jockey.put("jockeyFirstName", rs.getString("jockey_first_name"));
                jockey.put("jockeyMiddleName", rs.getString("jockey_middle_name"));
                jockey.put("jockeyLastName", rs.getString("jockey_last_name"));
                jockey.put("jockeyId", rs.getString("jockey_id"));

                jockey.put("trainerFirstName", rs.getString("trainer_first_name"));
                jockey.put("trainerMiddleName", rs.getString("trainer_middle_name"));
                jockey.put("trainerLastName", rs.getString("trainer_last_name"));
                jockey.put("trainerId", rs.getString("trainer_id"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get Jockey/Trainer");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return jockey;
    }

    //TODO: Delete OR replace later
    public HashMap<String, String> getRandomJockeyNTrainer(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        HashMap<String, String> jockey = new HashMap<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            String query = "select jockey_first_name, jockey_middle_name, jockey_last_name, jockey_id, " +
                    "trainer_first_name, trainer_middle_name, trainer_last_name, trainer_id " +
                    "from program_entry_race r, program_entry_start s " +
                    "where r.bds_track_id = ? and r.race_date = current_date() and r.race_number = ? " +
                    "and s.program_entry_race_id = r.entry_race_id and s.program_number != 'SCR' " +
                    "order by rand() limit 1";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                jockey.put("jockeyFirstName", rs.getString("jockey_first_name"));
                jockey.put("jockeyMiddleName", rs.getString("jockey_middle_name"));
                jockey.put("jockeyLastName", rs.getString("jockey_last_name"));
                jockey.put("jockeyId", rs.getString("jockey_id"));

                jockey.put("trainerFirstName", rs.getString("trainer_first_name"));
                jockey.put("trainerMiddleName", rs.getString("trainer_middle_name"));
                jockey.put("trainerLastName", rs.getString("trainer_last_name"));
                jockey.put("trainerId", rs.getString("trainer_id"));
            }
        } catch (Exception e) {
            System.out.println("Unable to get Jockey/Trainer");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return jockey;
    }

    //TODO: Rework when time allows (if the proc changes)
    public List<HashMap<String, Object>> getTrainerDetails(int trainerId, String trackId, String raceDate) {
        Connection connection = getBDSConnection();
        CallableStatement cs = null;
        ResultSet rs = null;
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();

        try {
            String proc = "CALL `p_select_trainer_stats`(?, ?, ?);";
            cs = connection.prepareCall(proc);
            cs.setInt(1, trainerId);
            cs.setString(2, trackId);
            cs.setString(3, raceDate);
            cs.execute();

            //stored proc returns two result sets-
            //calling getResultSet(collection), then moreResults(closesResults)-
            // then resultSet(collection2) again returns the second page
            rs = cs.getResultSet();
            cs.getMoreResults();
            rs = cs.getResultSet();

            ResultSetMetaData meta = null;
            meta = rs.getMetaData();

            int colCount = meta.getColumnCount();
            List<String> cols = new ArrayList<String>();
            for (int index = 1; index <= colCount; index++)
                cols.add(meta.getColumnName(index));

            //removing id column
            cols.remove(0);

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (String colName : cols) {
                    Object val = rs.getObject(colName);
                    row.put(colName, val);
                }

                rows.add(row);
            }
        } catch (Exception e) {
            System.out.println(e + " Unable to get Trainer Details...");
        } finally {
            closeConnection(rs, cs, connection);
        }

        return rows;
    }

   public List<CategoryStats> getJockeyTrainerComboStats(int jockeyId, int trainerId, String trackId, String raceDate) {
        Connection connection = getBDSConnection();

        CallableStatement cs = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<CategoryStats> rows = new ArrayList<CategoryStats>();

        try {
            // The procedure will create temp tables and return 3 datasets and the data we need
            // is in the 2nd datasets (or table tt_trainer_jockey_combo_summary_full)
            String proc = "CALL `p_select_jockey_trainer_stats`(?, ?, ?, ?);";

            // To avoid confusion, in the same session, select the data needed from the table
            // tt_trainer_jockey_combo_summary_full for categories by the order of
            // ('Total Starts', 'Last 14 days', 'Last 30 days', 'Last 90 days', 'Last 365 days', 'Current Meet')
            String query = "SELECT category_label.`order`, category_label.`label` AS `category`, " +
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.`starts`), 0) AS `starts`, " +
                    "IFNULL(MAX(ROUND(tt_trainer_jockey_combo_summary_full.`win_per`, 0)), 0) AS `win_per`, " +
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.`roi`), 0) AS `roi`, " +
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.wins),0) as wins, " +
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.places),0) as places, " +
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.shows),0) as shows, "+
                    "IFNULL(MAX(tt_trainer_jockey_combo_summary_full.earnings), 0) AS earnings " +
                    "FROM category_label LEFT JOIN tt_trainer_jockey_combo_summary_full " +
                    "ON category_label.category = tt_trainer_jockey_combo_summary_full.category " +
                    "AND category_label.num_days = tt_trainer_jockey_combo_summary_full.num_days " +
                    "WHERE category_label.type = 'trainer-jockey' " +
                    "AND category_label.`label` in ('Total Starts', 'Last 14 days', 'Last 30 days', 'Last 90 days', 'Last 365 days', 'Current Meet')" +
                    "GROUP BY category_label.`order` " +
                    "ORDER BY category_label.`order`;";

            cs = connection.prepareCall(proc);
            cs.setInt(1, jockeyId);
            cs.setInt(2, trainerId);
            cs.setString(3, trackId);
            cs.setString(4, raceDate);
            cs.execute();

            st = connection.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                CategoryStats categoryStats = new CategoryStats();
                categoryStats.setCategory(rs.getString("category").toLowerCase());
                categoryStats.setStarts(rs.getString("starts"));
                categoryStats.setWinPercent(rs.getString("win_per"));
                categoryStats.setRoi(rs.getString("roi"));
                categoryStats.setWins(rs.getString("wins"));
                categoryStats.setPlaces(rs.getString("places"));
                categoryStats.setShows(rs.getString("shows"));
                categoryStats.setEarnings(rs.getString("earnings"));
                rows.add(categoryStats);
            }
        } catch (Exception e) {
            System.out.println(e + " Unable to get Jockey + Trainer Details...");
        } finally {
            closeConnection(rs, cs, connection);
        }

        return rows;
    }

    //TODO: Consolidate
    public String getRandomGreyhoundDogName(String trackCode, String race) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String name = "";
        String raceDate = getTodaysDate();

        try {
            String query = "SELECT GreyhoundName FROM `staging_greyhound_entry_start` " +
                    "WHERE trackcode = ? " +
                    "AND carddate = ? " +
                    "AND racenumber = ? " +
                    "ORDER BY Rand() " +
                    "LIMIT 1;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);

            rs = st.executeQuery();

            while (rs.next()) {
                name = rs.getString("GreyhoundName");
            }
        } catch (Exception e) {
            System.out.println("Unable to find a Greyhound name");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return name;
    }

    //TODO: Consolidate
    public HashMap<String, String> getGreyhoundStats(String name, String trackCode, String race) {
        Connection connection = dbConnect.getDBConnection(getHost("bds") + "/bris_migration_v5",
                sqlDataCredentials.getBDSUsername(), sqlDataCredentials.getBDSPassword());
        PreparedStatement st = null;
        ResultSet rs = null;
        String raceDate = getTodaysDate();
        HashMap<String, String> stats = new HashMap<String, String>();

        try {
            String query = "SELECT * " +
                    "FROM `staging_greyhound_entry_start` " +
                    "WHERE greyhoundname = ? " +
                    "AND carddate = ? " +
                    "AND racenumber = ? " +
                    "AND trackcode = ?;";

            st = connection.prepareStatement(query);
            st.setString(1, name);
            st.setString(2, raceDate);
            st.setString(3, race);
            st.setString(4, trackCode);

            rs = st.executeQuery();

            while (rs.next()) {
                stats.put("postPosition", rs.getString("BetNumber"));
                stats.put("sex", rs.getString("Sex"));
                stats.put("weight", rs.getString("GreyhoundWeight"));
                stats.put("sire", rs.getString("Sire"));
                stats.put("dam", rs.getString("Dam"));
                stats.put("trainer", rs.getString("TrainerName"));
                stats.put("kennel", rs.getString("KennelName"));
                stats.put("bestTime", rs.getString("BestTime"));
                stats.put("startsRecord", rs.getString("StartsRecord"));
                stats.put("whelpDate", rs.getString("WhelpDate"));
                stats.put("name", name);
                stats.put("bestTime", rs.getString("BestTime"));
            }
        } catch (Exception e) {
            System.out.println("Unable to retrieve Greyhound stats");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return stats;
    }

    //TODO: Consolidate
    public HashMap<String, List<String>> getGreyhoundPPs(String trackCode, String race, String postPosition) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String raceDate = getTodaysDate();
        String[] dataBaseDate;
        String convertedDate;
        HashMap<String, List<String>> ppInfo = new HashMap<String, List<String>>();

        ppInfo.put("date", new ArrayList<>());
        ppInfo.put("race", new ArrayList<>());
        ppInfo.put("trackCode", new ArrayList<>());
        ppInfo.put("distance", new ArrayList<>());
        ppInfo.put("postPosition", new ArrayList<>());
        ppInfo.put("offPosition", new ArrayList<>());
        ppInfo.put("eighthPosition", new ArrayList<>());
        ppInfo.put("stretchPosition", new ArrayList<>());
        ppInfo.put("finishPosition", new ArrayList<>());
        ppInfo.put("margin", new ArrayList<>());
        ppInfo.put("time", new ArrayList<>());
        ppInfo.put("odds", new ArrayList<>());
        ppInfo.put("grade", new ArrayList<>());
        ppInfo.put("comments", new ArrayList<>());

        try {
            String query = "SELECT * FROM `program_greyhound_performance` " +
                    "WHERE bds_track_id = ? " +
                    "AND race_date = ? " +
                    "AND race_number = ? " +
                    "AND post_position = ? " +
                    "ORDER BY pp_race_date DESC " +
                    "LIMIT 2;";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);
            st.setString(4, postPosition.replace("0", ""));

            rs = st.executeQuery();

            while (rs.next()) {
                dataBaseDate = rs.getString("pp_race_date").split("-");
                convertedDate = dataBaseDate[1] + "-" + dataBaseDate[2] + "-" + dataBaseDate[0].substring(2);
                ppInfo.get("date").add(convertedDate);
                ppInfo.get("race").add(rs.getString("pp_race_number"));
                ppInfo.get("trackCode").add(rs.getString("pp_track_code"));
                ppInfo.get("distance").add(rs.getString("pp_distance"));
                ppInfo.get("postPosition").add(rs.getString("pp_post_position"));
                ppInfo.get("offPosition").add(rs.getString("pp_off_position"));
                if (rs.getString("pp_eighth_position") != null) {
                    ppInfo.get("eighthPosition").add(rs.getString("pp_eighth_position"));
                } else {
                    ppInfo.get("eighthPosition").add("-");
                }
                ppInfo.get("stretchPosition").add(rs.getString("pp_stretch_position"));
                ppInfo.get("finishPosition").add(rs.getString("pp_finish_position"));
                ppInfo.get("margin").add(rs.getString("pp_finish_margin"));
                ppInfo.get("time").add(rs.getString("pp_runner_time"));
                ppInfo.get("odds").add(rs.getString("pp_odds"));
                if (rs.getString("pp_grade") != null) {
                    ppInfo.get("grade").add(rs.getString("pp_grade"));
                } else {
                    ppInfo.get("grade").add("-");
                }
                ppInfo.get("comments").add(rs.getString("pp_comments"));

            }
        } catch (Exception e) {
            System.out.println("Unable to retrieve Greyhound stats");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return ppInfo;
    }

    //TODO: Consolidate
    public int getGreyhoundStatRecordCount(String trackCode, String race, String postPosition) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        String raceDate = getTodaysDate();
        int recordCnt = 0;
        try {
            String query = "SELECT count(1) as pp_count FROM `program_greyhound_performance` " +
                    "WHERE bds_track_id = ? " +
                    "AND race_date = ? " +
                    "AND race_number = ? " +
                    "AND post_position = ? " +
                    ";";

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceDate);
            st.setString(3, race);
            st.setString(4, postPosition.replace("0", ""));

            rs = st.executeQuery();
            while (rs.next()) {
                recordCnt = rs.getInt("pp_count");
            }
        } catch (Exception e) {
            System.out.println("Unable to retrieve Greyhound stats");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }
        return recordCnt;
    }

    //TODO: Delete later after OddsTests settled
    public HashMap<String, String> getProfitLineOdds(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> plOdds = new HashMap<String, String>();

        try {
            String query = selectProgramColumns("program_number, pl_fair_value");

            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);
            rs = st.executeQuery();
            while (rs.next()) {
                if (rs.getString("pl_fair_value") == null) {
                    plOdds.put(rs.getString("program_number"), "-");
                } else if (!rs.getString("pl_fair_value").isEmpty()) {
                    plOdds.put(rs.getString("program_number"), rs.getString("pl_fair_value"));
                } else {
                    plOdds.put(rs.getString("program_number"), "-");
                }
            }

        } catch (Exception e) {
            System.out.println("Unable to get Profit Line Odds");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return plOdds;
    }

    //TODO: Delete later after OddsTests settled
    public HashMap<String, String>  getMorningLineOdds(String trackCode, String raceNumber) {
        Connection connection = getBDSConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        HashMap<String, String> mlOdds = new HashMap<String, String>();

        try {
            String query = selectProgramColumns("program_number, morning_line_odds");
            st = connection.prepareStatement(query);
            st.setString(1, trackCode);
            st.setString(2, raceNumber);

            rs = st.executeQuery();
            while (rs.next()) {
                //Removes the '/1' in odds such as 4/1 to leave only 4
                mlOdds.put(rs.getString("program_number"),
                        rs.getString("morning_line_odds").replace("/1", ""));
            }

        } catch (Exception e) {
            System.out.println("Unable to get Morning Line Odds");
            e.printStackTrace();
        } finally {
            closeConnection(rs, st, connection);
        }

        return mlOdds;
    }
}