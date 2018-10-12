package com.example.paige.bmi_calculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private BMICalc mCalc;                                 //android fields start with m to distinguish between local variable and class members

    private EditText mEtHeight, mEtWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                                                     //R = resources
        setupToolbar();
        setupFAB();

        //initialize BMI field object
        mCalc = new BMICalc();
        //get a reference to the EditText objects containing the user input

        mEtHeight = findViewById(R.id.et_height);
        mEtWeight = findViewById(R.id.et_weight);
    }
                                                                                                    //clean up code:
    private void setupToolbar() {                                                                   //right click, refactor, extract, method
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etHeight, etWeight;
                String strWeight, strHeight;
                double height, weight;

                //get the value from the EditEexts
                etHeight = findViewById(R.id.et_height);
                etWeight = findViewById(R.id.et_weight);

                //get the text from those EditTexts
                strHeight = etHeight.getText().toString();
                strWeight = etWeight.getText().toString();

                try {


                    height = Double.parseDouble(strHeight);
                    weight = Double.parseDouble(strWeight);

                    //put those values into the mCalc object

                    mCalc.setHeight(height);
                    mCalc.setWeight(weight);

                    //Calculate then show the user for the numbers
                    Snackbar.make(view, "BMI is: " + mCalc.getBMI(), Snackbar.LENGTH_LONG)
                            .setAction("Additional Details...", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Create an intent object pointing to Results Activity
                                    Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);

                                    //serialize the BMI object and store that in the intent
                                    String currentBMIObject = BMICalc.getJSONStringFromObject(mCalc);
                                    intent.putExtra("BMI", currentBMIObject);

                                    //start the results activity, passing
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                catch (IllegalArgumentException iae){
                    Snackbar.make(view, "Error! Height & Weight must be > 0",
                            Snackbar.LENGTH_LONG)
                            .show();
                }
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
}
