package com.example.android.gsquad.menu;

import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.gsquad.AddGameActivity;
import com.example.android.gsquad.GameListAdapter;
import com.example.android.gsquad.R;
import com.example.android.gsquad.model.Game;

import java.util.List;

/**
 * Created by Raghvendra on 18-03-2017.
 */

public class ToolbarActionModeCallback implements ActionMode.Callback {

    private AddGameActivity addGameActivity;
    private GameListAdapter mGameListAdapter;
    private List<Game> mGameList;

    public ToolbarActionModeCallback(AddGameActivity addGameActivity, GameListAdapter gameListAdapter,
                                     List<Game> gameList) {
        this.addGameActivity = addGameActivity;
        this.mGameListAdapter = gameListAdapter;
        this.mGameList = gameList;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_action_menu, menu); // Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Sometimes the menu will not be visible so for that we need to set their visibility
        // manually in this method
        menu.findItem(R.id.submit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit: {

                SparseBooleanArray selected = mGameListAdapter.getSelectedIds();

                int selectedMessageSize = selected.size();

                // Loop to all selected items
                for (int i = (selectedMessageSize - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        // get selected data in Model
                        Game game = mGameList.get(selected.keyAt(i));
                        Log.d("Hello", "Selected items: Id - " + game.getId() + " Name - " + game.getName());

                    }
                }
                mode.finish();
                break;
            }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // When action mode destroyed remove selected selections and set action mode to null
        mGameListAdapter.removeSelections();
        addGameActivity.setNullToActionMode(); // Set action mode to null

    }
}