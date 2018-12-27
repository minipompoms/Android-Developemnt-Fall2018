package com.example.paige.perpetualmotion.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.paige.perpetualmotion.R;
import com.example.paige.perpetualmotion.lib.CardPilesAdapter;
import com.example.perpetual_motion.pm_game.Card;
import com.example.perpetual_motion.pm_game.IDGame;
import com.google.gson.Gson;

import java.util.EmptyStackException;

import static com.example.paige.perpetualmotion.lib.Utils.showInfoDialog;
import static com.example.paige.perpetualmotion.lib.Utils.showYesNoDialog;

public class MainActivity extends AppCompatActivity {

    private IDGame mCurrentGame;
    private CardPilesAdapter mAdapter;
    private String mWINNER_MSG, mNON_WINNER_MSG; // UI Strings

    //pref boolean fields
    private boolean mPrefUseAutoSave, mPrefShowErrors;
    private TextView mTv_cardsRemaining, mTv_cardsInDeck;

    //preference keys
    private String mKeyAutoSave, mKeyShowErrors;
    private final String mKeyPrefsName = "PREFS";
    private final String mKeyCheckedPiles = "CHECKED_PILES";
    private final String mKeyGame = "GAME";

    private Snackbar mSnackBar;
    private View mSbContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setDefaultValueForPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setFieldReferencesToResFileValues();
        setFieldReferencesToViewsAndSnackBar();
        setUpBoard();
        restoreAppSettingsFromPrefs();
        doInitialStartGame(savedInstanceState);
        setUpFab();

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

            editor.putString(mKeyGame, getJSONof(mCurrentGame));

            for (int i = 0; i < CHECKED_PILES.length ; i++) {
                editor.putBoolean(mKeyCheckedPiles+ i, CHECKED_PILES[i]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        // call the super-class's method to save fields, etc.
        super.onSaveInstanceState (outState);

        // save current game, which includes the piles from which we derive pile tops after restore
        outState.putString (mKeyGame, getJSONof (mCurrentGame));

        // save each checkbox's status so that we can check off those boxes after restore
        outState.putBooleanArray (mKeyCheckedPiles, mAdapter.getCheckedPiles ());
    }

    private void setDefaultValueForPreferences(){
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.main_prefs, true);
    }

    private void setUpBoard() {
        //create adapter
        mAdapter = new CardPilesAdapter(getApplicationContext(), R.string.cards_in_stack);
        mAdapter.setOnItemClickListener (listener);

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setFieldReferencesToViewsAndSnackBar () {
        mSbContainer = findViewById (R.id.activity_main);
        mTv_cardsRemaining = findViewById (R.id.tv_cards_remaining_to_discard);
        mTv_cardsInDeck = findViewById (R.id.tv_cards_in_deck);
        mSnackBar = Snackbar.make (mSbContainer, R.string.welcome_new_game, Snackbar.LENGTH_SHORT);
    }

    private void setFieldReferencesToResFileValues () {
        mKeyAutoSave = getString (R.string.key_use_auto_save);
        mKeyShowErrors = getString (R.string.key_show_turn_specific_error_messages);
        mWINNER_MSG = getString (R.string.you_have_cleared_the_board)
                .concat ("\n").concat (getString (R.string.new_game_question));
        mNON_WINNER_MSG = getString (R.string.no_more_turns_remain)
                .concat ("\n").concat (getString (R.string.new_game_question));
    }

    private void doInitialStartGame (Bundle savedInstanceState) {
        // If this is NOT the first run, meaning, we're recreating as a result of a device rotation
        // then restore the board (meaning both cards and user's checks) as from before the rotation
        // Otherwise (if this is a fresh start of the Activity and NOT after a rotation),
        // if auto-save is enabled then restore the game from sharedPrefs
        // otherwise (not post-rotation and auto-save off or no game in prefs): start a new game

        if (savedInstanceState != null) {
            restoreSavedGameAndBoardFromBundle (savedInstanceState);
        }
        else  if (mPrefUseAutoSave && isValidGameInPrefs ()) {
            restoreSavedGameAndBoardFromPrefs ();
        }
        else {
            startNewGame ();
        }
    }

    private boolean isValidGameInPrefs () {
        SharedPreferences preferences = getSharedPreferences (mKeyPrefsName, MODE_PRIVATE);

        // restore the current game
        String savedGame = preferences.getString (mKeyGame, "");
        return (!savedGame.equals (""));
    }

    private boolean[] getArrayFromPrefsValues (SharedPreferences preferences) {
        // Even if the Adapter's array is empty, we can use it to determine the array length, etc.
        final boolean [] checkedPiles = mAdapter.getCheckedPiles ();

        for (int i = 0; i < checkedPiles.length; i++) {
            checkedPiles[i] = preferences.getBoolean (mKeyCheckedPiles + i, false);
        }
        return checkedPiles;
    }


    private void startGameAndSetBoard(IDGame game, boolean[] checks, int msgID) {
        // create/restore the game
        mCurrentGame = game != null ? game : new IDGame ();

        // update the board
        doUpdatesAfterGameStartOrTakingTurn ();

        // overwrite checks if not null
        if (checks != null)
            mAdapter.overwriteChecksFrom(checks);

        // Show New Game message if non-zero
        if (msgID !=0)
        {
            mSnackBar = Snackbar.make (mSbContainer, msgID, Snackbar.LENGTH_SHORT);
            mSnackBar.show ();
        }
    }


    private void restoreSavedGameAndBoardFromBundle(Bundle savedInstanceState) {
        startGameAndSetBoard(restoreGameFromJSON(savedInstanceState.getString (mKeyGame)),
                savedInstanceState.getBooleanArray (mKeyCheckedPiles), 0);
    }

    private void restoreSavedGameAndBoardFromPrefs() {
        SharedPreferences preferences = getSharedPreferences (mKeyPrefsName, MODE_PRIVATE);

        startGameAndSetBoard(restoreGameFromJSON(preferences.getString (mKeyGame, "")),
                getArrayFromPrefsValues (preferences),
                R.string.welcome_restore_game);
    }

    private void setUpFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialog (MainActivity.this,"Information", mCurrentGame.getRules ());

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

        //dismiss the snackbar if shown
        dismissSnackbarIfShown();

        switch (id){
            //in a secondary activity this handles back button
            // (android.R.id.home:)
            // onBackPressed();
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_toggle_auto_save:
                toggleMenuItem(item);
                mPrefUseAutoSave = item.isChecked();
                return true;
            case R.id.action_turn_show_error_messages:
                toggleMenuItem(item);
                mPrefShowErrors = item.isChecked();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    private void dismissSnackbarIfShown(){
        if (mSnackBar.isShown ())
            mSnackBar.dismiss ();
    }

    private void toggleMenuItem(MenuItem item) {
        item.setChecked(item.isChecked());
    }

    private void showAbout() {
        showInfoDialog (this, R.string.app_name, R.string.about_message);

    }

    public void turn_action_discard(View view) {
       if(mCurrentGame.isGameOver())
           showSB_AlreadyGameOver();
       else{
           dismissSnackbarIfShown();
           final boolean[] checkedPiles = mAdapter.getCheckedPiles();
           attemptDiscard(checkedPiles, getCountofChecks(checkedPiles));
       }

    }

    private void attemptDiscard(boolean[] checkedPiles, int countOfChecks){
        try{
            discardOneOrTwo(checkedPiles, countOfChecks);
            doUpdatesAfterGameStartOrTakingTurn();
        }catch (EmptyStackException ese){
            showSB(getString(R.string.error_cannot_discard_from_empty_pile));
        }
        catch (UnsupportedOperationException uoe) {
            showSB (uoe.getMessage ());
        }
    }

    private void updateStatusBar () {
        // Update the Status Bar with the number of cards left (from Java) via our current game obj
        mTv_cardsRemaining.setText (getString (R.string.cards_to_discard).concat
                (String.valueOf (mCurrentGame.getNumberOfCardsLeftInAllStacks()))); //getNumCardsLeftToDiscardFromDeckAndStacks ())));

        mTv_cardsInDeck.setText (getString (R.string.in_deck).concat (
                String.valueOf (mCurrentGame.getNumberOfCardsLeftInDeck ())));
    }


    private void doUpdatesAfterGameStartOrTakingTurn() {
        updateStatusBar ();
        updateRecyclerViewAdapter ();
        checkForGameOver ();
    }

    private void discardOneOrTwo(boolean[] checkedPiles, int countOfChecks) {
        int pileTopToDiscard, secondPileTopToDiscard;
        switch(countOfChecks){
            case 1: {
                pileTopToDiscard = getCheckedItem(checkedPiles);
                mCurrentGame.discardOneLowestOfSameSuit(pileTopToDiscard);
                break;
            }
            case 2: {
                pileTopToDiscard = getCheckedItem(checkedPiles);
                secondPileTopToDiscard = getCheckedItem(checkedPiles);
                mCurrentGame.discardBothOfSameRank(pileTopToDiscard, secondPileTopToDiscard);
                break;
            }
                default: {
                    //show snackbar error one or two and how many checkboxes are currently checked
                    showSBErrorDiscardOneTwoOrOther(countOfChecks);
                }
        }
    }

    private void showSBErrorDiscardOneTwoOrOther (int checkedCount)
    {
        if (mPrefShowErrors) {
            String errorMsg = getString (checkedCount > 2 || checkedCount < 1 ?
                    R.string.turn_error_discard_other :
                    checkedCount == 2 ? R.string.turn_error_discard_two
                            : R.string.turn_error_discard_one);

            mSnackBar = Snackbar.make (mSbContainer, errorMsg, Snackbar.LENGTH_LONG);
            mSnackBar.show ();
        }
    }
    private int getCountofChecks(boolean[] checkedPiles) {
        int totalChecked = 0;
        for (boolean checkedPile : checkedPiles) {
            totalChecked += checkedPile ? 1 : 0;
        }
        return totalChecked;
    }

    private void showSB_AlreadyGameOver() {
        if (mPrefShowErrors) {
            mSnackBar = Snackbar.make (mSbContainer,
                    getString(R.string.msg_game_already_ended),
                    Snackbar.LENGTH_LONG);
            mSnackBar.setAction (R.string.new_game, new View.OnClickListener ()
            {
                @Override public void onClick (View view)
                {
                    startNewGame();
                }
            });
            mSnackBar.show ();
        }
    }

    private CardPilesAdapter.OIClickListener listener = new CardPilesAdapter.OIClickListener() {
        @Override
        public void onItemClick(int position, View v) {

        }
    };

    private final CardPilesAdapter.OIClickListener listener2 = new CardPilesAdapter.OIClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            try{
                if (mCurrentGame.getNumberOfCardsInStackAtPosition(position) > 0) {

                    if(mCurrentGame.isGameOver()){
                        showSB_AlreadyGameOver ();
                    }
                    else {
                        mAdapter.toggleCheck(position);
                    }
                }
        }
        catch (Exception e){
                Log.d("STACK_CRASH", e.getMessage());
        }
    }

    };


    public void turn_action_deal (@SuppressWarnings ("UnusedParameters") View view)
    {
        if (mCurrentGame.isGameOver ()) {
            showSB_AlreadyGameOver ();
        }
        else {
            try {
                dismissSnackbarIfShown();

                mCurrentGame.dealOneCardToEachStack ();
            }
            catch (EmptyStackException ese) {
                showInfoDialog (this, R.string.title_no_cards_remain,
                        R.string.body_all_cards_dealt_to_stacks);
            }

            // cards will remain as above but clear checkboxes either way, even if deck is empty
            doUpdatesAfterGameStartOrTakingTurn ();
        }
    }

    private int getCheckedItem(boolean[] checkedPiles) {
        // create a new int array containing the number of elements == the number of checked cards
        int[] checkedItems = new int[getCountOfChecks (checkedPiles)];

        // i is the index for the 4-element array of all stacks passed in
        // j is the index for the new array of position numbers just created
        for (int i = 0, j = 0; i < checkedPiles.length; i++) {
            // increment j only if current element is true
            if (checkedPiles[i]) {
                checkedItems[j++] = i;
            }

        }
        return checkedItems[0];
    }

    private int getCountOfChecks (boolean[] checkedPiles)
    {
        int totalChecked = 0;
        for (boolean checkedPile : checkedPiles) {
            totalChecked += checkedPile ? 1 : 0;
        }
        return totalChecked;
    }

    private void showSB(String msg)
    {
        if (mPrefShowErrors) {
            mSnackBar = Snackbar.make (mSbContainer, msg, Snackbar.LENGTH_LONG);
            mSnackBar.show ();
        }
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        return super.onKeyUp (keyCode, event);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        boolean isOldLG = false;

        //noinspection SimplifiableConditionalExpression
        return isOldLG ? true : super.onKeyDown (keyCode, event);
    }

    private void startNewGame() {
        startGameAndSetBoard(new IDGame(), null, R.string.welcome_new_game);
    }

    private IDGame restoreGameFromJSON(String gsonString) {
        Gson gson = new Gson();
        return gson.fromJson(gsonString, IDGame.class);
    }

    private String getJSONof(IDGame game) {
        Gson gson = new Gson();
        return gson.toJson(game);
    }

    public void startNewGame (@SuppressWarnings ("UnusedParameters") MenuItem item) {
        startNewGame();
    }

    private void showGameOverDialog (String strTitle, String strMsg) {
        final DialogInterface.OnClickListener newGameListener =
                new DialogInterface.OnClickListener ()
                {
                    @Override
                    public void onClick (DialogInterface dialog, int which)
                    {
                        startNewGame();
                    }
                };

        showYesNoDialog (this, strTitle, strMsg, newGameListener, null);
    }

    public void undoLastMove (@SuppressWarnings ("UnusedParameters") MenuItem item) {
        undoLastMove ();
    }

    private void undoLastMove () {
        try {
            mCurrentGame.undoLatestTurn ();
            doUpdatesAfterGameStartOrTakingTurn ();
        }
        catch (UnsupportedOperationException uoe) {
            showInfoDialog (this, "Can't Undo", uoe.getMessage ());
        }
    }

    private void checkForGameOver () {
        // If the game is over, let the user know what happened and then start a new game
        if (mCurrentGame.isGameOver ()) {
            showGameOverDialog (getString (R.string.game_over),
                    mCurrentGame.isWinner () ? mWINNER_MSG : mNON_WINNER_MSG);
        }
    }

    private void updateRecyclerViewAdapter ()
    {
        // get the data for the new board from our game object (Java) which tracks the four stacks
        Card[] currentTops = mCurrentGame.getCurrentStacksTopIncludingNulls ();

        // temporary card used when updating the board below
        Card currentCard, currentAdapterCard;

        // Update the board one pile/card at a time, as needed
        for (int i = 0; i < currentTops.length; i++) {
            currentCard = currentTops[i];
            currentAdapterCard = mAdapter.getCardAt (i);

            if ((currentAdapterCard == null || !currentAdapterCard.equals (currentCard))) {
                // Have Adapter set each card to the matching top card of each stack
                mAdapter.updatePile (i, currentCard,
                        mCurrentGame.getNumberOfCardsInStackAtPosition (i));
            }

            // Clear the checks that the user might have just set
            mAdapter.clearCheck (i);
        }
    }
}
