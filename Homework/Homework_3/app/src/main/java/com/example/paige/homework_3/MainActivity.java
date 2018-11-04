package com.example.paige.homework_3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    GridGameAdapter mObjGridGameAdapter;
    int mTurnsTaken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBoard();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setUpBoard(){
        //RV (FrontEnd) holds items in its in bound adapter
        //adapter contain the data model for the RV
        //layout manager handles the layout in the RV for the adapters items
        int squares =16;
        int rows = (int) (squares / Math.sqrt(squares));
        RecyclerView objRecyclerView = (RecyclerView) findViewById (R.id.recycler_view);
        objRecyclerView.setHasFixedSize (true);
        RecyclerView.LayoutManager objLayoutManager = new GridLayoutManager (this, rows); // cols/rows
       // objLayoutManager.setAutoMeasureEnabled(true);
        mObjGridGameAdapter = new GridGameAdapter (squares);

        objRecyclerView.setLayoutManager (objLayoutManager);
        objRecyclerView.setAdapter(mObjGridGameAdapter);

    }

    public void buttonHandler(View view) {
        showGuessResults(view);
        incrementGuessesCounter();
    }

    private void showGuessResults(View view) {
        //CoordinatorLayout item
        View sbContainer = findViewById (R.id.activity_main);

        String currentText = view.getTag().toString();
        int currentElement = Integer.parseInt(currentText);

        String msg = "You clicked on " + currentText + ".\n";
        msg+= mObjGridGameAdapter.isWinner(currentElement) ?
                "This is the winning number!" :
                "Please try a different number.";
        Snackbar.make (sbContainer, msg, Snackbar.LENGTH_SHORT).show ();
    }

    private void incrementGuessesCounter(){
        TextView tvStatusBar = findViewById(R.id.status_bar);
        tvStatusBar.setText("Guesses taken: " + ++mTurnsTaken);
    }

    public void newGame(MenuItem item) {
        mObjGridGameAdapter.startNewGame();
        mTurnsTaken = -1;
        incrementGuessesCounter();
        View sbContainer = findViewById (R.id.activity_main);
        Snackbar.make(sbContainer, "Welcome! Begin a new game.", Snackbar.LENGTH_SHORT).show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("WINNING_NUMBER", mObjGridGameAdapter.getWinningNumber());
        outState.putInt("CURRENT_GUESSES", mTurnsTaken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        mObjGridGameAdapter.overwriteWinningNumber((savedInstanceState.getInt("WINNING_NUMBER")));
        mTurnsTaken = savedInstanceState.getInt("CURRENT_GUESSES")-1;
        incrementGuessesCounter();
    }
}
