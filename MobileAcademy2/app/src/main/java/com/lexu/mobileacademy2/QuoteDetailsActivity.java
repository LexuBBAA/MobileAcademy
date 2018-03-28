package com.lexu.mobileacademy2;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;

public class QuoteDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_details);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AppCompatTextView id = (AppCompatTextView) findViewById(R.id.quote_id_output_text_view);
        AppCompatTextView content = (AppCompatTextView) findViewById(R.id.quote_content_output_text_view);
        AppCompatTextView url = (AppCompatTextView) findViewById(R.id.quote_url_output_text_view);
        AppCompatTextView title = (AppCompatTextView) findViewById(R.id.quote_title_output_text_view);

        Quote quote = (Quote) getIntent().getSerializableExtra(QuotesActivity.QUOTE_KEY);

        String formattedId = "ID: " + quote.getId();
        Spanned formattedContent = Html.fromHtml(quote.getContent());
        String formattedTitle = "- " + quote.getTitle();

        id.setText(formattedId);
        content.setText(formattedContent);
        url.setText(quote.getSource());
        title.setText(formattedTitle);
    }

    @Override
    public void onBackPressed() {
        QuoteDetailsActivity.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            QuoteDetailsActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
