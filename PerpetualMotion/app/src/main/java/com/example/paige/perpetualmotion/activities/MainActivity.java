package com.example.paige.perpetualmotion.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.paige.perpetualmotion.R;
import com.example.paige.perpetualmotion.lib.CardPilesAdapter;
import com.example.perpetual_motion.pm_game.IDGame;

public class MainActivity extends AppCompatActivity {

    private IDGame mCurrentGame;
    private CardPilesAdapter mAdapter;

    //pref boolean fields
    private boolean mPrefUseAutoSave, mPrefShowErrors;

    //preference keys
    private String mKeyAutoSave, mKeyShowErrors, mKeyGame, mKeyCheckedPiles;
    private final String mKeyPrefsName = "PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDefaultValueForPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpFab();
        setUpBoard();
        restoreAppSettingsFromPrefs();

    }


    @Override
    protected void onStop() {
        saveToSharedPref();
        super.onStop();
    }


    private void restoreAppSettingsFromPrefs() {
        SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);
        mPrefUseAutoSave = preferences.getBoolean(mKeyAutoSave, true);
        mPrefShowErrors = preferences.getBoolean(mKeyShowErrors, true);
    }

    private void saveToSharedPref() {
        SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        saveSettingsToSharedPrefs(editor);
        saveGameAndBoardToSharePrefsIfAutoSaveIsOn(editor);

        editor.apply();
    }

    private void saveSettingsToSharedPrefs(SharedPreferences.Editor editor) {
        editor.putBoolean(mKeyShowErrors, mPrefShowErrors);
        editor.putBoolean(mKeyAutoSave, mPrefUseAutoSave);
    }

    private void saveGameAndBoardToSharePrefsIfAutoSaveIsOn(SharedPreferences.Editor editor) {
        if(mPrefUseAutoSave){
            final boolean[] CHECKED_PILES = mAdapter.getCheckedPiles();

            //editor.putString(mKeyGame, getJSONOF(mCurrentGame));

            for (int i = 0; i < CHECKED_PILES.length ; i++) {
                editor.putBoolean(mKeyCheckedPiles+ i, CHECKED_PILES[i]);
            }
        }
    }



    private void setDefaultValueForPreferences(){
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.main_prefs, true);
    }

    private void setUpBoard() {
        //create adapter
        mAdapter = new CardPilesAdapter(getApplicationContext(), R.string.cards_in_stack);

        //get a reference to RecyclerView
        RecyclerView rvPiles = findViewById(R.id.rv_piles);

        //set the number of columns based on currently loaded integers.xml (values-land)
        final int RV_COLUMN_COUNT = getResources().getInteger(R.integer.rv_columns);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, RV_COLUMN_COUNT);

        rvPiles.setHasFixedSize(true);
        rvPiles.setLayoutManager(layoutManager);
        rvPiles.setAdapter(mAdapter);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_toggle_auto_save).setChecked(mPrefUseAutoSave);
        menu.findItem(R.id.action_turn_show_error_messages).setChecked(mPrefShowErrors);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void turn_action_discard(View view) {
    }

    public void turn_action_deal(View view) {
    }
}
