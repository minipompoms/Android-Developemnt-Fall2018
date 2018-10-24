package com.example.paige.tipcalculator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getApplicationContext (), MainActivity.class);
        Log.d("splash test", "Entered splash");
        startActivity(intent);
        finish();
    }

}
