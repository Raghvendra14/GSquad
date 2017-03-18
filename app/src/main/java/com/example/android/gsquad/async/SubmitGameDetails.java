package com.example.android.gsquad.async;

import android.util.Log;

import com.example.android.gsquad.database.FirebaseAddGameData;
import com.example.android.gsquad.model.GameDetails;
import com.example.android.gsquad.rest.ApiClient;
import com.example.android.gsquad.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class SubmitGameDetails {

    public static final String TAG = SubmitGameDetails.class.getSimpleName();

    private List<com.example.android.gsquad.model.Game> mGameIdList;
    private final static String fields = "name,cover.url,summary,aggregated_rating";

    public SubmitGameDetails(List<com.example.android.gsquad.model.Game> gameList) {
        this.mGameIdList = gameList;
    }

    public void submitData() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        for (int i = 0; i < mGameIdList.size(); i++) {
            int id = mGameIdList.get(i).getId();
            Call<List<GameDetails>> call = apiService.getGameDetailList(id, fields);
            call.enqueue(new Callback<List<GameDetails>>() {
                @Override
                public void onResponse(Call<List<GameDetails>> call, Response<List<GameDetails>> response) {
                    List<GameDetails> gameDetails = response.body();
//                    Log.d(TAG, gameDetails.get(0).getName());
                    if (gameDetails != null) {
                        FirebaseAddGameData firebaseAddGameData = new FirebaseAddGameData(gameDetails.get(0));
                        firebaseAddGameData.addGameData();
                        FirebaseAddGameData addUserIntoGame = new FirebaseAddGameData();
                        addUserIntoGame.addUserData(gameDetails.get(0).getId());
                    }
                }

                @Override
                public void onFailure(Call<List<GameDetails>> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
    }
}
