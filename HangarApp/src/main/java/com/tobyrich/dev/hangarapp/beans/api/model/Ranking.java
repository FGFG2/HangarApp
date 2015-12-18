package com.tobyrich.dev.hangarapp.beans.api.model;

import android.graphics.Bitmap;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;

/**
 * Represents Ranking.
 *
*/
public class Ranking {
    private String userName;
    private int score;

    public Ranking(String name, int score) {
        this.userName = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                ", User='" + userName + '\'' +
                ", Score=" + score +
                '}';
    }
}
