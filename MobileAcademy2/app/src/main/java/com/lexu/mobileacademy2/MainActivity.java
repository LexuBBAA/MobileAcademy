package com.lexu.mobileacademy2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.lexu.mobileacademy2.views.CustomTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnQuoteEventHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String PLACEHOLDER;
    private QuoteRequester mQuoteRequester = new QuoteRequester();

    private CustomTextView quoteDestinationTextView = null;
    private AppCompatButton button = null;
    private AppCompatButton navigateButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.quoteDestinationTextView = (CustomTextView) findViewById(R.id.quote_destination_text_view);
        this.button = (AppCompatButton) findViewById(R.id.button);
        this.navigateButton = (AppCompatButton) findViewById(R.id.navigate_button);
        this.button.setOnClickListener(MainActivity.this);
        this.navigateButton.setOnClickListener(MainActivity.this);

        PLACEHOLDER = this.getResources().getString(R.string.random_quote_text);

        this.quoteDestinationTextView.setText("Click the button to generate a new Quote!");
    }

    @Override
    public void onClick(View v) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.disableButtons();
            }
        });

        switch (v.getId()) {
            case R.id.button:
                mQuoteRequester.getQuote(MainActivity.this);
                break;
            case R.id.navigate_button:
                navigate();
                break;
        }
    }

    private void navigate() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent navigate = new Intent(MainActivity.this, QuotesActivity.class);
                MainActivity.this.startActivity(navigate);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.enableButtons();
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(ResponseData response) {
        switch (response.getCode()) {
            case QuoteRequester.StatusCodes.SUCCESS:
                updateView(response);
                break;
            default:
                showErrors(response);
        }
    }

    @Override
    public void onFailure(ResponseData responseData) {
        showErrors(responseData);
    }

    private void disableButtons() {
        MainActivity.this.button.setClickable(false);
        MainActivity.this.button.setFocusable(false);
        MainActivity.this.navigateButton.setClickable(false);
        MainActivity.this.navigateButton.setFocusable(false);
    }

    private void enableButtons() {
        MainActivity.this.button.setClickable(true);
        MainActivity.this.button.setFocusable(true);
        MainActivity.this.navigateButton.setClickable(true);
        MainActivity.this.navigateButton.setFocusable(true);
    }

    private void showErrors(final ResponseData data) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, data.getMsg(), Toast.LENGTH_LONG).show();
                MainActivity.this.enableButtons();
            }
        });
    }

    private void updateView(final ResponseData data) {
        if(data.getData() instanceof Quote) {
            final Quote quote = (Quote) data.getData();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.quoteDestinationTextView.setText(String.format(PLACEHOLDER, Html.fromHtml(quote.getContent())), quote.getTitle());
                    MainActivity.this.enableButtons();
                }
            });
        }
    }
}
