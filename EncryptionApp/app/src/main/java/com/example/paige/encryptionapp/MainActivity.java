package com.example.paige.encryptionapp;



import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.net.http.SslCertificate.restoreState;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 34783;

    private List<File> mDirectories = new ArrayList<>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_encrypt:
                    switchFragment(new EncryptFragment());
                    return true;
                case R.id.navigation_decrypt:
                    switchFragment(new DecryptFragment());
                    return true;
                case R.id.navigation_directory:
                    switchFragment(new DirectoryFragment());

            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.color.colorBackground);
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            mDirectories.add(Environment.getExternalStorageDirectory());
        }


        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        requestPermissions(permissions, REQUEST_CODE);

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


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


}
