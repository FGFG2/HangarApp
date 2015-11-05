package com.tobyrich.dev.hangarapp.beans;

/**
 * Represents Achievement.
 */
public class Achievement {

    private String name;
    private String value;
    private double progress;

    public Achievement(String name, String value, double progress) {
        this.name = name;
        this.value = value;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
