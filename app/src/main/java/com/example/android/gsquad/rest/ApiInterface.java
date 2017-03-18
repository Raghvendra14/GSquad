package com.example.android.gsquad.rest;

import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.model.GameDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public interface ApiInterface {
    @Headers({"X-Mashape-Key : lhe24dCZZimshyyXkvQTe51KZ5c1p1zGackjsnfaYVadbb1BYS", "Accept : application/json"})
    @GET("/games/")
    Call<List<Game>> getGameList(@Query("fields") String field, @Query("limit") int limit, @Query("offset") int offset, @Query("search") String gameTitle);

    @Headers({"X-Mashape-Key : lhe24dCZZimshyyXkvQTe51KZ5c1p1zGackjsnfaYVadbb1BYS", "Accept : application/json"})
    @GET("/games/{id}")
    Call<List<GameDetails>> getGameDetailList(@Path("id") int gameId, @Query("fields") String field);
}
