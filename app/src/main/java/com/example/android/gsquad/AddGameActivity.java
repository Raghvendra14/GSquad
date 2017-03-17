package com.example.android.gsquad;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.gsquad.model.Game;

import java.util.List;

public class AddGameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Game>> {

    public static final int DEFAULT_GAME_TITLE_LENGTH_LIMIT = 100;

    private EditText mAddGameEditText;
    private Button mSearchButton;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = AddGameActivity.this;
        mAddGameEditText = (EditText) findViewById(R.id.add_game_text);
        mSearchButton = (Button) findViewById(R.id.search_game_button);

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

        // Search button searches the igdb API for the respective game and clears the EditText
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SearchGameTask searchGameTask = new SearchGameTask(context, mAddGameEditText.getText().toString());
                useLoader();
                mAddGameEditText.setText("");
            }
        });
    }

    @Override
    public Loader<List<Game>> onCreateLoader(int id, Bundle args) {
        return new SearchGameTask(context, mAddGameEditText.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Game>> loader, List<Game> data) {
        Log.d("Hello", data.get(0).getName());
    }

    @Override
    public void onLoaderReset(Loader<List<Game>> loader) {

    }

    public void useLoader() {
        Loader loader = getLoaderManager().initLoader(0, null, this);
        loader.forceLoad();
    }
}
