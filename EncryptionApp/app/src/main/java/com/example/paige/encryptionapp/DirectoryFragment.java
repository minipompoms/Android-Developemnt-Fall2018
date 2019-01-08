package com.example.paige.encryptionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.paige.encryptionapp.FilesFragment.TAG;

public class DirectoryFragment extends Fragment implements View.OnClickListener,
        FilesFragment.OnFileClickedListener, FilesFragment.OnDirectoryClickedListener {

    private static final String KEY_PATHS = "key_paths";

    private List<File> mDirectories = new ArrayList<>();
    private ViewPager mViewPager;
    private Button mBackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_directory, container, false);
        mDirectories.add(Environment.getExternalStorageDirectory());
        mViewPager = v.findViewById(R.id.viewPager);
        mBackButton = v.findViewById(R.id.back_button);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager.setAdapter(new DirectoryAdapter(getFragmentManager(), mDirectories,
                this, this));
        mBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }

        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<String> paths = convertFilesToStrings(mDirectories);
        outState.putStringArrayList(KEY_PATHS, paths);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void OnFileClicked(File file) {
        Log.d("PATHS", file.getAbsoluteFile().getAbsolutePath());

        try {
            startActivity(FileUtil.createOpenFileIntent(file));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnDirectoryClicked(File directory) {
        Log.d(TAG, "Opening directory " + directory.getAbsolutePath());
        mDirectories.subList(mViewPager.getCurrentItem() + 1, mDirectories.size()).clear();
        mDirectories.add(directory);
        DirectoryAdapter adapter = (DirectoryAdapter) mViewPager.getAdapter();
        assert adapter != null;
        adapter.setDirectories(mDirectories);
        adapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mDirectories.size() - 1);

    }


    private static ArrayList<String> convertFilesToStrings(List<File> files) {
        if (files == null) {
            throw new IllegalArgumentException("Files cannot be null. ");
        }
        ArrayList<String> paths = new ArrayList<>();
        for (File file : files) {
            paths.add(file.getAbsolutePath());
        }
        return paths;
    }


    public void onBackPressed() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }
}
