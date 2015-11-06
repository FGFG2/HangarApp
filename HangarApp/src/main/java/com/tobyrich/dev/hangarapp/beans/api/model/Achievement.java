package com.tobyrich.dev.hangarapp.beans.api.model;

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
    private String ImageUrl;
    private String Name;
    private int Progress;

    public Achievement(String name, int progress) {
        Name = name;
        Progress = progress;
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
}
