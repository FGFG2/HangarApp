package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.graphics.Bitmap;

import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.model.UserProfile;

import java.util.List;

/**
 * Callbacks used by feeders are here.
 */
public interface FeedersCallback {
    void onAchievementsFeederComplete(List<Achievement> achievementList);
    void onImageFeederComplete(String key, Bitmap bm);

    void onTokenFeederComplete(String authToken);
    void onRankingFeederComplete(List<UserProfile> userProfileList);
}

