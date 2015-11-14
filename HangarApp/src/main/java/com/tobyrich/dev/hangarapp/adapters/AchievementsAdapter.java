package com.tobyrich.dev.hangarapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Customizing of ArrayAdapter to display the list of achievements with the corresponding icons.
 */
public class AchievementsAdapter extends ArrayAdapter<Achievement>{

    private List<Achievement> achievements;

    public AchievementsAdapter(Context context, List<Achievement> achievements) {
        super(context, 0, achievements);
        this.achievements = achievements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the achievement and icon depending on its position in the list.
        Achievement achievement = achievements.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.achievement, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.achievementName);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.achievementIcon);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.achievementProgressBar);

        // Populate the data into the template view using the data object
        tvName.setText(achievement.getName());
        ivIcon.setImageBitmap(achievement.getIcon());

        progressBar.setProgress(achievement.getProgress());

        if(achievement.getProgress() == 100) {
            tvName.setTextColor(Color.parseColor("#7ca700"));
        } else {
            tvName.setTextColor(Color.parseColor("#c0c0c0"));
        }

        // Return the completed view to render on the screen
        return convertView;
    }

    @Override
    public Achievement getItem(int position) {
        return achievements.get(position);
    }
}
