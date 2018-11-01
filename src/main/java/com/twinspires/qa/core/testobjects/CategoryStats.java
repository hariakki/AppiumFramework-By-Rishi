package com.twinspires.qa.core.testobjects;

import java.util.Objects;

/*
 * Define the Object for all possible category stats instead of using HashMap
 */
public class CategoryStats {
    String category;
    String starts;
    String wins;
    String places;
    String shows;
    String winPercent;
    String earnings;
    String roi;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWins() {
        return wins;
    }

    public String getStarts() {
        return starts;
    }

    public void setStarts(String starts) {
        this.starts = starts;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public String getShows() {
        return shows;
    }

    public void setShows(String shows) {
        this.shows = shows;
    }

    public String getWinPercent() {
        return winPercent;
    }

    public void setWinPercent(String winPercent) {
        this.winPercent = winPercent;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getRoi() {
        return roi;
    }

    public void setRoi(String roi) {
        this.roi = roi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryStats that = (CategoryStats) o;
        return Objects.equals(category, that.category) &&
                Objects.equals(starts, that.starts) &&
                Objects.equals(wins, that.wins) &&
                Objects.equals(places, that.places) &&
                Objects.equals(shows, that.shows) &&
                Objects.equals(winPercent, that.winPercent) &&
                Objects.equals(earnings, that.earnings) &&
                Objects.equals(roi, that.roi);
    }

    @Override
    public int hashCode() {

        return Objects.hash(category, starts, wins, places, shows, winPercent, earnings, roi);
    }

    @Override
    public String toString() {
        return "CategoryStats{" +
                "category='" + category + '\'' +
                ", starts='" + starts + '\'' +
                ", wins='" + wins + '\'' +
                ", places='" + places + '\'' +
                ", shows='" + shows + '\'' +
                ", winPercent='" + winPercent + '\'' +
                ", earnings='" + earnings + '\'' +
                ", roi='" + roi + '\'' +
                '}';
    }
}
