package com.example.paige.homework_3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {
    GridGameAdapter mObjGridGameAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBoard();

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
        //CoordinatorLayout item
        View sbContainer = findViewById (R.id.activity_main); // this is the CoordinatorLayout above
        Button currentButton = (Button) view; // this is the button that was clicked
        String currentText = view.getTag().toString ();
        int currentElement = Integer.parseInt (currentText);
        String msg = "You clicked on " + currentText + ".\n";        msg+= mObjGridGameAdapter.isWinner(currentElement) ? "This is the winning number!" : "Please try a different number.";
        Snackbar.make (sbContainer, msg, Snackbar.LENGTH_SHORT).show (); // remmeber .show()
          }
}
