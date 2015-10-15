package com.tobyrich.dev.hangarapp.beans;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.db.DBHandler;
import com.tobyrich.dev.hangarapp.util.AppContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 02.06.2015.
 * This class gets the data from the plane memory.
 */
public class PlaneData {

    int accumulatedTime, lastTime, maxHeight, lastHeight, maxRPM, lastRPM, maxG, lastG, operationalRemainedTime = 0;
    double lastFlightCharge, currentBatteryCharge = 0;

    public void setAccumulatedTime(int accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setLastHeight(int lastHeight) {
        this.lastHeight = lastHeight;
    }

    public void setMaxRPM(int maxRPM) {
        this.maxRPM = maxRPM;
    }

    public void setLastRPM(int lastRPM) {
        this.lastRPM = lastRPM;
    }

    public void setMaxG(int maxG) {
        this.maxG = maxG;
    }

    public void setLastG(int lastG) {
        this.lastG = lastG;
    }

    public void setLastFlightCharge(double lastFlightCharge) {
        this.lastFlightCharge = lastFlightCharge;
    }

    public void setCurrentBatteryCharge(double currentBatteryCharge) {
        this.currentBatteryCharge = currentBatteryCharge;
    }

    public void setOperationalRemainedTime(int operationalRemainedTime) {
        this.operationalRemainedTime = operationalRemainedTime;
    }

    /**
     * FIXME: These variables are for tests. Later should be changed to proper values.
     */
    @Inject
    public PlaneData() {
        // Get the accumulated time which is saved in DB?
        this.accumulatedTime = 100;
        // Get the last flight time from the plane memory.
        this.lastTime = 10;
        // Get the max height which is saved in DB?
        this.maxHeight = 10;
        // Get the last max height from the plane memory.
        this.lastHeight = 9;
        // Get the max RPM which is saved in DB?
        this.maxRPM = 10000;
        // Get the last max RPM from the plane memory.
        this.lastRPM = 7000;
        // Get the max G-overload which is saved in DB?
        this.maxG = 10;
        // Get the last max G-overload from the plane memory.
        this.lastG = 7;
        // Get the battery charge used for the last flight from the plane memory.
        this.lastFlightCharge = 1.7;
        // Get the current battery charge in V.
        this.currentBatteryCharge = 3.5;
        // Get the operational remained time.
        this.operationalRemainedTime = 20;
    }


    /**
     * The function returns the total flight duration properly formatted.
     */
    public String getTotalFlightDuration() {
        String returnString = "";

        int sum = this.accumulatedTime + this.lastTime;
        int hours = sum/ 3600;
        int minutes = (sum % 3600) / 60;
        int seconds = sum % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    /**
     * The function returns the total flight duration properly formatted.
     */
    public String getLastFlightDuration() {

        int hours = this.lastTime/ 3600;
        int minutes = (this.lastTime % 3600) / 60;
        int seconds = this.lastTime % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    /**
     * The function formats the number to a 2 digit number.
     * @param number integer
     * @return formatted number
     */
    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    /**
     * The function returns the max relative height of the flight in meters.
     */
    public String getMaxRelativeHeight() {
        String returnString = "";

        if (this.lastHeight > this.maxHeight) {
            returnString = this.lastHeight + "m";
        } else {
            returnString = this.maxHeight + "m";
        }

        return returnString;
    }


    /**
     * The function returns the last relative height of the flight in meters.
     */
    public String getLastRelativeHeight() {
        String returnString = "";

        returnString = this.lastHeight + "m";

        return returnString;
    }


    /**
     * The function returns the max RPM.
     */
    public String getMaxRPM() {
        String returnString = "";

        if (this.lastRPM > this.maxRPM) {
            returnString = "" + this.lastRPM;
        } else {
            returnString = "" + this.maxRPM;
        }

        return returnString;
    }


    /**
     * The function returns the max RPM.
     */
    public String getLastRPM() {
        String returnString = "";

        returnString = "" + this.lastRPM;

        return returnString;
    }


    /**
     * The function returns the max G-overload.
     */
    public String getMaxG() {
        String returnString = "";

        if (this.lastG > this.maxG) {
            returnString = this.lastG + "G";
        } else {
            returnString = this.maxG + "G";
        }

        return returnString;
    }


    /**
     * The function returns the last G-overload.
     */
    public String getLastG() {
        String returnString = "";

        returnString = this.lastG + "m";

        return returnString;
    }


    /**
     * The function returns the eco-ranking based on flight duration and battery charge quotient.
     * The ranking-mapping is either hardcoded as array / enum here or saved in DB?
     * The ranking-mapping should be discussed.
     */
    public String getEcoRanking() {
        String returnString = "";

        double quot = this.lastTime / this.lastFlightCharge;
        if (quot > 200) {
            returnString = "Bad";
        } else if (quot > 100 && quot <= 200) {
            returnString = "Normal";
        } else {
            returnString = "Nice";
        }

        return returnString;
    }
    /**
     * The function returns the current battery charge in percent.
     * Max. battery charge = 4.2 V, min. battery charge = 3.0 V
     * 1% = 0.012 V
     */
    public int getCurrentBatteryCharge() {
        if(this.currentBatteryCharge == 4.2) {
            return 100;
        }
        if(this.currentBatteryCharge == 3.0) {
            return 0;
        }
        if(this.currentBatteryCharge > 3) {
            return (int)Math.round((this.currentBatteryCharge - 3)/0.012);
        }
        return 0;
    }

    /**
     * The function returns the operational remained time properly formatted.
     */
    public String getOperationalRemainedTime() {

        int hours = this.operationalRemainedTime/ 3600;
        int minutes = (this.operationalRemainedTime % 3600) / 60;
        int seconds = this.operationalRemainedTime % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    /**
     * This function saves the stats data to the DB.
     */
    public void save() {
        // statName identifies what value is being saved. Should be later defined in other table and saved using its id.
        String statName = "lastTime";

        // There is no date type in SQLite, hence String.
        // The date of saving is used for stat, not the date of actual flight.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTmp = new Date();
        String date = dateFormat.format(dateTmp); // 2015-06-30
        double value = this.lastTime;

        // Initialize the stats object.
        Statistics stats = new Statistics(statName, date, value);

        // Initialize DBHandler.
        DBHandler db = new DBHandler(AppContext.getContext());
        db.addStatistics(stats);
    }

}
