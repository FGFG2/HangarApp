package com.tobyrich.dev.hangarapp.beans.api.model;

import android.graphics.Bitmap;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;

import java.util.List;

/**
 *
 * Example of get-response from remote server
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
    private String ImageUrl = APIConstants.DEFAULT_ICON_URL;
    private Bitmap Icon;

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
        return Icon;
    }

    public void setIcon(Bitmap icon) {
        this.Icon = icon;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "Icon=" + Icon +
                ", Description='" + Description + '\'' +
                ", Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Progress=" + Progress +
                ", ImageUrl='" + ImageUrl + '\'' +
                '}';
    }
}
