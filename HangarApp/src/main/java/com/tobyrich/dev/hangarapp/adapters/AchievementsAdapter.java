package com.tobyrich.dev.hangarapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;

import java.util.List;

/**
 * Customizing of ArrayAdapter to display the list of achievements.
 */
public class AchievementsAdapter extends ArrayAdapter<Achievement>{

    private List<Achievement> achievements;

    public AchievementsAdapter(Context context, List<Achievement> achievements) {
        super(context, 0, achievements);
        this.achievements = achievements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Achievement achievement = achievements.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.achievement, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.achievementName);
        TextView tvValue = (TextView) convertView.findViewById(R.id.achievementValue);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.achievementProgressBar);

        // Populate the data into the template view using the data object
        tvName.setText(achievement.getName());
        tvValue.setText("" + achievement.getProgress());
        progressBar.setProgress(achievement.getProgress());

        if(achievement.getProgress() == 100) {
            tvName.setTextColor(Color.parseColor("#7ca700"));
            tvValue.setTextColor(Color.parseColor("#7ca700"));
        } else {
            tvName.setTextColor(Color.parseColor("#d3d3d3"));
            tvValue.setTextColor(Color.parseColor("#d3d3d3"));
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
