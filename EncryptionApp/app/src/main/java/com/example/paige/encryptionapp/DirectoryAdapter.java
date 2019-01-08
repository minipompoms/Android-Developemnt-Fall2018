package com.example.paige.encryptionapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.File;
import java.util.List;

class DirectoryAdapter extends FragmentStatePagerAdapter {

    private List<File> mDirectory;
    private FilesFragment.OnDirectoryClickedListener mOnDirectoryClickedListener;
    private FilesFragment.OnFileClickedListener mOnFileClickedListener;

    DirectoryAdapter(FragmentManager fm, List<File> directories,
                     @Nullable FilesFragment.OnDirectoryClickedListener onDirectoryClickedListener,
                     @Nullable FilesFragment.OnFileClickedListener onFileClickedListener) {
        super(fm);
        setDirectories(directories);
        mOnDirectoryClickedListener = onDirectoryClickedListener;
        mOnFileClickedListener = onFileClickedListener;
    }

    @Override
    public int getCount() {
        return mDirectory.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Internal Storage";
        }
        return mDirectory.get(position).getName();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    void setDirectories(List<File> directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        mDirectory = directory;
    }

    private FilesFragment createFilesFragment(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        FilesFragment filesFragment = FilesFragment.newInstance(directory);
        filesFragment.setOnDirectoryClickedListener(mOnDirectoryClickedListener);
        filesFragment.setOnFileClickedListener(mOnFileClickedListener);
        return filesFragment;
    }


    @Override
    public Fragment getItem(int i) {
        return createFilesFragment(mDirectory.get(i));
    }
}