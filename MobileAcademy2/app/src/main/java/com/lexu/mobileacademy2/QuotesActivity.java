package com.lexu.mobileacademy2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lexu.mobileacademy2.views.CustomTextView;

import java.util.ArrayList;

import static com.lexu.mobileacademy2.QuoteRequester.StatusCodes.SUCCESS;

public class QuotesActivity extends AppCompatActivity implements OnQuoteEventHandler {

    private static final String TAG = QuotesActivity.class.getSimpleName();
    public static final String QUOTE_KEY = "quote";
    private static final String QUOTE_COUNT = "quote_count";

    private QuoteRequester mQuoteRequester = new QuoteRequester();

    private ListViewCompat quotesListView = null;
    private AppCompatTextView messageTextView = null;
    private Adapter listAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.quotesListView = (ListViewCompat) findViewById(R.id.quotes_list_view);
        this.messageTextView = (AppCompatTextView) findViewById(R.id.message_container_text_view);
        this.listAdapter = new Adapter(QuotesActivity.this);
        this.quotesListView.setAdapter(this.listAdapter);
        this.quotesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Quote quote = (Quote) QuotesActivity.this.listAdapter.getItem(position);
                QuotesActivity.this.navigate(quote);
            }
        });

        if(this.listAdapter.getCount() == 0) {
            mQuoteRequester.getQuotes(QuotesActivity.this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(QUOTE_COUNT, this.listAdapter.getCount());
        for(int i = 0; i < this.listAdapter.getCount(); i++) {
            Quote q = this.listAdapter.getItem(i);
            outState.putSerializable(QUOTE_KEY + i, q);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int count = savedInstanceState.getInt(QUOTE_COUNT);
        if(this.listAdapter == null || this.listAdapter.getCount() != count && count != 0) {
            for(int i = 0; i < count; i++) {
                Quote q = (Quote) savedInstanceState.getSerializable(QUOTE_KEY + i);
                this.listAdapter.addQuote(q);
            }
        }
    }

    @Override
    public void onSuccess(ResponseData response) {
        int code = response.getCode();
        String msg = response.getMsg();

        switch (code) {
            case SUCCESS:
                updateViews(response);
                break;
            default:
                showErrors(response);
        }
    }

    @Override
    public void onFailure(ResponseData responseData) {
        showErrors(responseData);
    }

    private void navigate(final Quote quote) {
        QuotesActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent navigate = new Intent(QuotesActivity.this, QuoteDetailsActivity.class);
                navigate.putExtra(QUOTE_KEY, quote);
                QuotesActivity.this.startActivity(navigate);
            }
        });
    }

    private void updateViews(ResponseData data) {
        if(data.getData() instanceof Quote) {
            final Quote newQuote = (Quote) data.getData();
            QuotesActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(QuotesActivity.this.messageTextView.getVisibility() == View.VISIBLE) {
                        QuotesActivity.this.messageTextView.setVisibility(View.GONE);
                    }

                    QuotesActivity.this.listAdapter.addQuote(newQuote);
                }
            });
        }
    }

    private void showErrors(final ResponseData data) {
        QuotesActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QuotesActivity.this, data.getMsg(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

class Adapter extends ArrayAdapter<Quote> {

    private ArrayList<Quote> data = new ArrayList<Quote>();

    Adapter(@NonNull Context context) {
        super(context, 0);
    }

    @Override
    public int getCount() {
        return this.data == null ? 0: this.data.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = new CustomTextView(getContext());
        }

        Quote item = this.data.get(position);
        ((CustomTextView) convertView).setText(item.getContent(), item.getTitle());
        return convertView;
    }

    @Nullable
    @Override
    public Quote getItem(int position) {
        return position < this.data.size() ? this.data.get(position): null;
    }

    void addQuote(Quote newQuote) {
        this.data.add(newQuote);
        this.notifyDataSetChanged();
    }
}