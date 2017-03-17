package com.example.android.gsquad;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.rest.ApiClient;
import com.example.android.gsquad.rest.ApiInterface;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Raghvendra on 17-03-2017.
 */

public class SearchGameTask extends AsyncTaskLoader<List<Game>> {

    public static final String TAG = SearchGameTask.class.getSimpleName();

    private final static String field = "name,cover";
    private static String mGameTitle;
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();

    List<Game> mGames;

    public SearchGameTask(Context context, String gameTitle) {
        super(context);
        mGameTitle = gameTitle;
    }
    @Override
    public List<Game> loadInBackground() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Game>> call = apiService.getGameList(field, 20, 0, mGameTitle);
        try {
            Response<List<Game>> response = call.execute();
            List<Game> entries = response.body();
            Log.d(TAG, entries.get(0).getName());
            if (!entries.isEmpty()) {
                return entries;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


    @Override
    public void deliverResult(List<Game> data) {
        mGames = data;

        if(isStarted()) {
            // If the loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mGames != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mGames);
        }
        // Has something interesting in the configuration changed since we
        // last fetched the game list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || mGames == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<Game> data) {
        super.onCanceled(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mGames != null) {
            mGames = null;
        }
    }

    private static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        boolean applyNewConfig(Resources res) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
                    |ActivityInfo.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }
}
