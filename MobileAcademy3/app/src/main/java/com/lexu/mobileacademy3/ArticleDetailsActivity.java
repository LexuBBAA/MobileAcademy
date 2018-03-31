package com.lexu.mobileacademy3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ArticleDetailsActivity extends AppCompatActivity {

    private NewsArticle mNewsArticle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mNewsArticle = (NewsArticle) getIntent().getSerializableExtra(MainActivity.ARTICLE_DATA);

        //TODO: Setup views

        setupData();
    }

    private void setupData() {
        //TODO: Setup newsArticle data inside views
    }

    @Override
    public boolean onSupportNavigateUp() {
        ArticleDetailsActivity.this.setResult(RESULT_CANCELED);
        ArticleDetailsActivity.this.finish();
        return super.onSupportNavigateUp();
    }
}
