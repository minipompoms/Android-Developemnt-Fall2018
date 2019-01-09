package com.example.paige.encryptionapp;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 34783;

    private ImageView mImageView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_encrypt:
                    changeDimensions();
                    switchFragment(new EncryptFragment());
                    return true;
                case R.id.navigation_decrypt:
                    changeDimensions();
                    switchFragment(new DecryptFragment());
                    return true;
                case R.id.navigation_directory:
                    changeDimensions();
                    switchFragment(new DirectoryFragment());
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.color.colorBackground);

        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        requestPermissions(permissions, REQUEST_CODE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadImage();
    }


    public void loadImage() {
        mImageView = findViewById(R.id.backgroundGif);
        String imageUrl = getURLForResource(R.drawable.background_main);
        Glide.with(this)
                .load(imageUrl)
                .into(mImageView);
    }

    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }


    public void changeDimensions() {
        mImageView.getLayoutParams().height = 0;
        mImageView.getLayoutParams().width = 0;
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    protected void switchFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAbout() {

 /*       final TextView textViewAbout = findViewById(R.id.tv_about);
        textViewAbout.postDelayed(new Runnable() {
            public void run() {
                textViewAbout.setVisibility(View.INVISIBLE);
            }
        }, 5000);*/
    }


    public void showAbout(View view) {
        Utils.showInfoDialog(MainActivity.this, R.string.app_name, R.string.about_message);

    }
}
