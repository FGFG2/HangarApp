package com.tobyrich.dev.hangarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.api.model.Ranking;

import java.util.List;

/**
 * Customizing of ArrayAdapter to display the ranking list.
 */
public class RankingAdapter extends ArrayAdapter<Ranking>{

    private List<Ranking> rankingList;

    public RankingAdapter(Context context, List<Ranking> rankingList) {
        super(context, 0, rankingList);
        this.rankingList = rankingList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the ranking depending on its position in the list.
        Ranking ranking = rankingList.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.rankingUserName);
        TextView tvScore = (TextView) convertView.findViewById(R.id.rankingUserScore);

        // Populate the data into the template view using the data object
        tvName.setText(ranking.getUserName());
        tvScore.setText(ranking.getScore() + "");

        // Return the completed view to render on the screen
        return convertView;
    }

    @Override
    public Ranking getItem(int position) {
        return rankingList.get(position);
    }
}
