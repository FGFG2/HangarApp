package com.tobyrich.dev.hangarapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.api.model.UserProfile;

import java.util.List;

/**
 * Customizing of ArrayAdapter to display the ranking list.
 */
public class RankingAdapter extends ArrayAdapter<UserProfile>{

    private List<UserProfile> rankingList;

    public RankingAdapter(Context context, List<UserProfile> rankingList) {
        super(context, 0, rankingList);
        this.rankingList = rankingList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the ranking depending on its position in the list.
        UserProfile ranking = rankingList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.rankingUserName);
        TextView tvScore = (TextView) convertView.findViewById(R.id.rankingUserScore);
        TextView tvPosition = (TextView) convertView.findViewById(R.id.rankingPosition);

        // Populate the data into the template view using the data object
        tvName.setText(ranking.getKey());
        tvScore.setText(ranking.getValue() + "");
        tvPosition.setText(ranking.getPosition() + ".");


        // Set background of current user
        if(ranking.isCurrentUser()) {
            convertView.setBackgroundColor(Color.parseColor("#696969"));
        } else {
            convertView.setBackground(null);
        }


        // Return the completed view to render on the screen
        return convertView;
    }

    @Override
    public UserProfile getItem(int position) {
        return rankingList.get(position);
    }
}
