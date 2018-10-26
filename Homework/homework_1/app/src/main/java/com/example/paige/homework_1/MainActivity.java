package com.example.paige.homework_1;
/*
Exercise 1.
Create a simple application that accepts text input from a user
with an EditText object and,when the user clicks an update Button,
displays the text within a TextView control.
 */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.edit_text);
        mTextView = findViewById(R.id.view_text);

    }

    public void displayText(View view) {
        String text = mEditText.getText().toString();
        mTextView.setText(text);

    }
}
