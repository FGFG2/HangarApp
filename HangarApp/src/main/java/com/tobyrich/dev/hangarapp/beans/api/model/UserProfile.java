package com.tobyrich.dev.hangarapp.beans.api.model;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;

/**
 *
 * Example of GET SERVER_URL/api/RankingList response from remote server
    [
        {
             "Key": "sample string 1",
             "Value": 2
        },
        {
             "Key": "sample string 1",
             "Value": 2
        }
    ]
 */
public class UserProfile {
    private String Key;         // Username
    private int Value;          // Points

    public UserProfile(String key, int value) {
        Key = key;
        Value = value;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "Key='" + Key + '\'' +
                ", Value=" + Value +
                '}';
    }
}
