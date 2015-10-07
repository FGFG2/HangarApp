package com.tobyrich.dev.hangarapp.objects;

/**
 * Created by Alex on 03.06.2015.
 * This is a Statistics class.
 */
public class Statistics {

    private String statName, date;
    private double value;
    private int id;

    // If initialized from DB.
    public Statistics(int id, String statName, String date, double value) {
        this.id = id;
        this.statName = statName;
        this.date = date;
        this.value = value;
    }

    // If initialized from Plane.
    public Statistics(String statName, String date, double value) {
        this.id = 0;
        this.statName = statName;
        this.date = date;
        this.value = value;
    }

    public int getId() { return this.id; }

    public String getStatName() { return this.statName; }

    public String getDate() { return this.date; }

    public double getValue() { return this.value; }
}
