package com.tobyrich.dev.hangarapp.beans.api.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 *
 * Example of get-response from http://chaos-krauts.de/Achievement/api/allAchievements
    [
        {
            "Id": 1,
            "Name": "sample string 2",
            "Description": "sample string 3",
            "ImageUrl": "sample string 4",
            "Progress": 64
        },
        {
            "Id": 1,
            "Name": "sample string 2",
            "Description": "sample string 3",
            "ImageUrl": "sample string 4",
            "Progress": 64
        }
    ]
*/
public class Achievement {
    private String Description;
    private int Id;
    private String Name;
    private int Progress;
    // Fake URL for testing.
    private String ImageUrl = "http://4.bp.blogspot.com/_C5a2qH8Y_jk/StYXDpZ9-WI/AAAAAAAAAJQ/sCgPx6jfWPU/S1600-R/android.png";
    private Bitmap icon;

    public Achievement(String name, int progress, String description) {
        Name = name;
        Progress = progress;
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

}
