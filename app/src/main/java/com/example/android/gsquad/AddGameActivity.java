package com.example.android.gsquad;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.gsquad.listener.RecyclerClickListener;
import com.example.android.gsquad.listener.RecyclerTouchListener;
import com.example.android.gsquad.menu.ToolbarActionModeCallback;
import com.example.android.gsquad.model.Game;

import java.util.List;

public class AddGameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Game>> {

    public static final int DEFAULT_GAME_TITLE_LENGTH_LIMIT = 100;

    private EditText mAddGameEditText;
    private Button mSearchButton;
    private RecyclerView mRecyclerView;
    private GameListAdapter mGameListAdapter;
    private Context context;
    private ActionMode mActionMode = null;
    private List<Game> mGameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = AddGameActivity.this;
        mAddGameEditText = (EditText) findViewById(R.id.add_game_text);
        mSearchButton = (Button) findViewById(R.id.search_game_button);
        setupRecyclerView();
        implementRecyclerViewClickListeners();
        mAddGameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSearchButton.setEnabled(true);
                } else {
                    mSearchButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAddGameEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(DEFAULT_GAME_TITLE_LENGTH_LIMIT)});

        mAddGameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });

        // Search button searches the igdb API for the respective game and clears the EditText
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchGameTask searchGameTask = new SearchGameTask(context, mAddGameEditText.getText().toString());
                useLoader();
                mAddGameEditText.setText("");
                hideSoftKeyboard(v);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<List<Game>> onCreateLoader(int id, Bundle args) {
        Log.d("Hello", mAddGameEditText.getText().toString());
        return new SearchGameTask(context, mAddGameEditText.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Game>> loader, List<Game> data) {
        mGameList = data;
        mGameListAdapter = new GameListAdapter(mGameList, context);
        mRecyclerView.setAdapter(mGameListAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Game>> loader) {

    }

    public void useLoader() {
        Loader loader = getLoaderManager().restartLoader(0, null, this);
        loader.forceLoad();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, mRecyclerView,
                new RecyclerClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // if ActionMode not null select item
//                        if (mActionMode != null) {
                        onListItemSelect(position);
//                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        // Select item on long click
                        onListItemSelect(position);
                    }
                }));
    }

    // List item select method
    public void onListItemSelect(int position) {
        mGameListAdapter.toggleSelection(position);

        boolean hasCheckedItems = mGameListAdapter.getSelectedCount() > 0; // Check if any items are already selected or not.
        if (hasCheckedItems && mActionMode == null && mGameList != null) {
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) context).startSupportActionMode(
                    new ToolbarActionModeCallback(AddGameActivity.this, mGameListAdapter, mGameList));
        } else if (!hasCheckedItems && mActionMode != null) {
            // there are no selected items, finish the actionmode
            mActionMode.finish();
        }

        if (mActionMode != null) {
            // set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mGameListAdapter.getSelectedCount()) + " selected");
        }
    }

    // Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
//        finish(); // Want to reselect items
    }
}
