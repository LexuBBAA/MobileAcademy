package com.lexu.mobileacademy1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * App that shows a random - Quote of the Day - .
     *
     * 1. Text View (75% screen)
     * 2. Button to change (wrap text + onClick -> change text in TV)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative_layout);

        final EditText input = (EditText) findViewById(R.id.nameInputField);
        final TextView destination = (TextView) findViewById(R.id.nameDestinationTextView);
        final AppCompatButton btn = (AppCompatButton) findViewById(R.id.useNameButton);

        final TextView wordCounter = (TextView) findViewById(R.id.wordCounterTextView);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btn.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordCounter.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newData = s.toString();

                if(newData.length() > 15 || newData.length() < 2) {
                    input.setError(getResources().getString(R.string.nameErrorTemplate));
                } else {
                    btn.setEnabled(true);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);
                String name = input.getText().toString();
                if(!name.isEmpty()) {
                    destination.setText(String.format(getResources().getString(R.string.nameDestinationTemplate), name));
                }

                btn.setEnabled(true);
            }
        });
    }
}
