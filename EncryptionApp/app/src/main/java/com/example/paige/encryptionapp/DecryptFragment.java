package com.example.paige.encryptionapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.regex.Pattern;


public class DecryptFragment extends Fragment {
    FilePickerDialog mDialog;
    String path;
    DataHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DataHelper(getContext());
        return inflater.inflate(R.layout.fragment_decrypt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        {
            super.onActivityCreated(savedInstanceState);
            Button mSelectButton;
            Button mDecryptButton;
            final EditText key;

            final TextView p;
            View view = getView();

            assert view != null;
            mSelectButton = view.findViewById(R.id.button_select_file);
            mDecryptButton = view.findViewById(R.id.button_decrypt);

            key =  view.findViewById(R.id.et_decrypt_key);
            p = view.findViewById(R.id.tv_decrypt_line);

            mSelectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.selection_type = DialogConfigs.FILE_SELECT;
                    properties.root = new File(Environment.getExternalStorageDirectory().getPath());
                    properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                    properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                    properties.extensions = null;
                    mDialog = new FilePickerDialog(getActivity(),properties);
                    mDialog.setTitle("Select a File:");

                    mDialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            path = files[0];
                            p.setText(path);
                        }
                    });
                    mDialog.show();

                }
            });

            mDecryptButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String pass = key.getText().toString().trim();
                    if(TextUtils.isEmpty(pass) || pass.length() < 16 || pass.length() > 16)
                    {
                        key.setError("Key must contain 16 characters");
                        return;
                    }
                    if(TextUtils.isEmpty(path)){
                        Toast.makeText(getActivity(), "Please choose the File!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (key.getText().toString().length() != 0) {
                        String keyc = key.getText().toString();
                        File encryptedFile = new File(path);//new File("/storage"+path);
                        String correctFileName;
                        if(Pattern.compile(Pattern.quote("encrypted_"), Pattern.CASE_INSENSITIVE).matcher(encryptedFile.getName()).find()) {

                            correctFileName = encryptedFile.getName().replace("encrypted_", "");
                        }else{
                            correctFileName=encryptedFile.getName();
                        }
                        File decryptedFile = new File(encryptedFile.getParent()+"/decrypted_"+correctFileName);
                        int error;
                        try {
                            CryptoUtils.decrypt(keyc, encryptedFile, decryptedFile);
                            error = 0;
                            db.insertDataHistory(encryptedFile.getName(),keyc);
                        } catch (CryptoException ex) {
                            Toast.makeText(getActivity(), "Wrong Key!!", Toast.LENGTH_LONG).show();
                            error = 1;
                        }
                        if(error == 0){
                            Toast.makeText(getActivity(), "Successfully decrypted!!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter the key!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mDialog!=null) {
                       mDialog.show();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
