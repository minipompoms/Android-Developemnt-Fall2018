package com.example.paige.encryptionapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class EncryptFragment extends Fragment {
    FilePickerDialog dialog;
    String path;
    DataHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new DataHelper(getContext());

        return inflater.inflate(R.layout.fragment_encrypt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        {
            super.onActivityCreated(savedInstanceState);

            Button mSelectButton;
            Button mEncryptButton;
            final EditText key;
            final TextView p;
            View view = getView();

            assert view != null;
            mSelectButton = view.findViewById(R.id.button_select_file);
            mEncryptButton = view.findViewById(R.id.button_encrypt);

            p =  view.findViewById(R.id.tv_encrypt_line);
            key =  view.findViewById(R.id.et_encrypt_key);


            mSelectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.selection_type = DialogConfigs.FILE_SELECT;
                    properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                    properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                    properties.extensions = null;
                    dialog = new FilePickerDialog(getActivity(), properties);
                    dialog.setTitle("Select a File: ");

                    dialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            path = files[0];
                            p.setText(path);
                        }
                    });
                    dialog.show();

                }
            });

            mEncryptButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String pass = key.getText().toString().trim();
                    if (TextUtils.isEmpty(pass) || pass.length() < 16 || pass.length() > 16) {
                        key.setError("Key must be 16 characters");
                        return;
                    }
                    if (TextUtils.isEmpty(path)) {
                        Toast.makeText(getActivity(), "Please choose a File!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (key.getText().toString().length() != 0) {
                        String keyc = key.getText().toString();
                        File inputFile = new File(path);
                        String correctFileName;
                        if (Pattern.compile(Pattern.quote("decrypted_"), Pattern.CASE_INSENSITIVE).matcher(inputFile.getName()).find()) {

                            correctFileName = inputFile.getName().replace("decrypted_", "");
                        } else {
                            correctFileName = inputFile.getName();
                        }

                        File encryptedFile = new File(inputFile.getParent() + "/encrypted_" + correctFileName);
                        int error = 1;
                        try {
                            CryptoUtils.encrypt(keyc, inputFile, encryptedFile);
                            error = 0;
                            db.insertDataHistory(inputFile.getName(), keyc);
                        } catch (CryptoException ex) {
                            Toast.makeText(getActivity(), "Wrong Key!!", Toast.LENGTH_LONG).show();
                        }
                        if (error == 0) {
                            Toast.makeText(getActivity(), "File encrypted successfully", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter the key.", Toast.LENGTH_LONG).show();
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
                    if (dialog != null) {
                        dialog.show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}