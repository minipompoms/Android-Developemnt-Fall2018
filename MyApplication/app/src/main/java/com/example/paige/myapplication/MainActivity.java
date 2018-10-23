package com.example.paige.myapplication;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int repTimes;

    private static final String DEBUG_TAG= "MyFirstAppLogging";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(DEBUG_TAG, "In the onCreate() method of the MyFirstAndroidAppActivity Class");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    public void forceError(){
        if(true){
            throw new Error("Whoops");
        }
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

    public String getFlavor(int index){
        String[] aFlavors = getResources().getStringArray(R.array.flavors);
        return aFlavors[index];
    }

    public boolean getMode(){
        boolean isAdvancedMode = getResources().getBoolean(R.bool.isAdvancedFeaturesEnabled);
        return isAdvancedMode;
    }



    public void setRepTimes(){
        repTimes = getResources().getInteger(R.integer.numTimesToRepeat);
    }

    XmlResourceParser myPets = getResources().getXml(R.xml.my_pets);

    //TextView txt = (TextView)findViewById(R.id.TextView01);
 /*   public void animate(){
        ImageView flagImageView =
                (ImageView) findViewById(R.id.ImageView01);
        flagImageView.setImageResource(R.drawable.flag);
        Animation an = AnimationUtils.loadAnimation(this, R.anim.spin);
        flagImageView.startAnimation(an);
    }
*/
}
