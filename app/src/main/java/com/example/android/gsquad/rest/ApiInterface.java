package com.example.android.gsquad.rest;

import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public interface ApiInterface {
    @Headers({"X-Mashape-Key : Fh0f4MAf1rmsh1SbpBJRAFXPNpuqp1kGfTCjsnfjfqHgjuSaSp", "Accept : application/json"})
    @GET("/games/")
    Call<List<Game>> getGameList(@Query("fields") String field, @Query("limit") int limit, @Query("offset") int offset, @Query("search") String gameTitle);

    @Headers({"X-Mashape-Key : Fh0f4MAf1rmsh1SbpBJRAFXPNpuqp1kGfTCjsnfjfqHgjuSaSp", "Accept : application/json"})
    @GET("/games/{id}")
    Call<List<GameDetails>> getGameDetailList(@Path("id") int gameId, @Query("fields") String field);

    @Headers({"Content-Type:application/json", "Authorization:key=AAAARjpNPOE:APA91bFl3I_M85rDHNNIzhU9kQH2OybJeQSZ0NppJBbM-MAsSMUhQzEYwp7Rgzimd3duOMUrOcpsM0QnjMvIXYC66FVMLfOZZcv5sI9wP9cDq06sVJPvNyAogP8Nc-IGjNztpxb5N4tn"})
    @POST
    Call<Notification> createNotification(@Url String url, @Body Notification notification);
}
