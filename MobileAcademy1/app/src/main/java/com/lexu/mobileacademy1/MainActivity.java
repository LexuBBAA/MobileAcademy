package com.lexu.mobileacademy1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText sourceInput = null;
    private AppCompatButton button = null;
    private TextView destinationOutput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sourceInput = (AppCompatEditText) findViewById(R.id.source_edit_text);
        this.button = (AppCompatButton) findViewById(R.id.button);
        this.destinationOutput = (TextView) findViewById(R.id.destination_text_view);

        this.button.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        v.setClickable(false);
        v.setFocusable(false);

        String name = this.sourceInput.getText().toString();
        if(!name.isEmpty()) {
            String placeholderText = MainActivity.this.getResources().getString(R.string.message_placeholder_text);

            this.destinationOutput.setVisibility(View.VISIBLE);
            this.destinationOutput.setText(String.format(placeholderText, name));
        }
        v.setClickable(true);
        v.setFocusable(true);
    }
}
