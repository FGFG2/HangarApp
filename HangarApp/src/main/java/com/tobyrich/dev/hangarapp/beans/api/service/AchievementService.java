package com.tobyrich.dev.hangarapp.beans.api.service;

import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.model.UserProfile;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

// FIXME: this is not only achievement service, but also at least ranking.
public interface AchievementService {
    @GET("api/AllAchievements")
    Call<List<Achievement>> getAllAchievements(@Header("Authorization") String authToken);

    @GET("api/RankingList")
    Call<List<UserProfile>> getRankingList(@Header("Authorization") String authToken);
}
