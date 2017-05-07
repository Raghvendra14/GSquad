package com.example.android.gsquad.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.gsquad.R;
import com.example.android.gsquad.adapter.GameListAdapter;
import com.example.android.gsquad.async.SearchGameTask;
import com.example.android.gsquad.listener.RecyclerClickListener;
import com.example.android.gsquad.listener.RecyclerTouchListener;
import com.example.android.gsquad.menu.ToolbarActionModeCallback;
import com.example.android.gsquad.model.Game;
import com.example.android.gsquad.utils.Constants;
import com.example.android.gsquad.utils.Utils;

import java.util.List;

public class AddGameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Game>> {

    public static final int DEFAULT_GAME_TITLE_LENGTH_LIMIT = 100;

    private EditText mAddGameEditText;
    private Button mSearchButton;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private GameListAdapter mGameListAdapter;
    private Context context;
    private ActionMode mActionMode = null;
    private List<Game> mGameList;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = AddGameActivity.this;
        mAddGameEditText = (EditText) findViewById(R.id.add_game_text);
        mSearchButton = (Button) findViewById(R.id.search_game_button);
        mProgressBar = (ProgressBar) findViewById(R.id.game_list_progressBar);
        mEmptyTextView = (TextView) findViewById(R.id.empty_game_list);
        setupRecyclerView();
        implementRecyclerViewClickListener();
        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
        }
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
                if (Utils.isNetworkAvailable(context)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    useLoader();
                } else {
                    Snackbar.make(v, getString(R.string.no_connection_available),
                            Snackbar.LENGTH_LONG).show();
                }
                mAddGameEditText.setText("");
                hideSoftKeyboard(v);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public Loader<List<Game>> onCreateLoader(int id, Bundle args) {
        return new SearchGameTask(context, mAddGameEditText.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Game>> loader, List<Game> data) {
        if (data == null) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mGameList = data;
            mGameListAdapter = new GameListAdapter(mGameList, context);
            mRecyclerView.setAdapter(mGameListAdapter);
            retainSavedInstanceState();
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Game>> loader) {

    }

    public void useLoader() {
        Loader loader = getLoaderManager().restartLoader(0, null, this);
        loader.forceLoad();

    }

    protected void retainSavedInstanceState() {
        if (mSavedInstanceState != null) {
                onListItemSelect(mSavedInstanceState.getInt(Constants.SELECTED_KEY));
        }
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
    private void implementRecyclerViewClickListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, mRecyclerView,
                new RecyclerClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // Select item on normal click
                        onListItemSelect(position);
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

        boolean hasCheckedItems = mGameListAdapter.getSelectedId() > -1; // Check if any item is already selected or not.
        if (hasCheckedItems && mActionMode == null && mGameList != null) {
            // there is a selected item, start the actionMode
            mActionMode = ((AppCompatActivity) context).startSupportActionMode(
                    new ToolbarActionModeCallback(AddGameActivity.this, mGameListAdapter, mGameList));
        } else if (!hasCheckedItems && mActionMode != null) {
            // there are no selected items, finish the actionmode
            mActionMode.finish();
        }

        if (mActionMode != null) {
            // set action mode title on item selection
            mActionMode.setTitle(getString(R.string.selected_count_label));
        }
    }

    // Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mGameListAdapter != null) {
            int position = mGameListAdapter.getSelectedId();
            outState.putInt(Constants.SELECTED_KEY, position);
        }
    }
}
