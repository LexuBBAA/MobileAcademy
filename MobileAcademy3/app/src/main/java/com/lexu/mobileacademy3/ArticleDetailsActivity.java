package com.lexu.mobileacademy3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.squareup.picasso.Picasso;

public class ArticleDetailsActivity extends AppCompatActivity {

    private NewsArticle mNewsArticle = null;

    private AppCompatImageView imageView = null;
    private AppCompatTextView titleView = null;
    private AppCompatTextView descriptionView = null;
    private AppCompatTextView fullArticleUrl = null;
    private AppCompatTextView authorView = null;
    private AppCompatTextView dateView = null;

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

        imageView = (AppCompatImageView) findViewById(R.id.article_image_view);
        titleView = (AppCompatTextView) findViewById(R.id.article_title_view);
        descriptionView = (AppCompatTextView) findViewById(R.id.article_description_view);
        fullArticleUrl = (AppCompatTextView) findViewById(R.id.article_url_view);
        authorView = (AppCompatTextView) findViewById(R.id.article_author_view);
        dateView = (AppCompatTextView) findViewById(R.id.article_date_view);

        setupData();
    }

    private void setupData() {
        Picasso.get()
                .load(mNewsArticle.getImageSrc())
                .placeholder(R.drawable.ic_image_gray_24dp)
                .into(imageView);
        titleView.setText(mNewsArticle.getTitle());
        descriptionView.setText(mNewsArticle.getDescription());
        fullArticleUrl.setText(getResources().getString(R.string.go_to_full_article_text));
        authorView.setText(mNewsArticle.getAuthor() != null ? mNewsArticle.getAuthor(): mNewsArticle.getSourceName());
        dateView.setText(mNewsArticle.getDate());

        fullArticleUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleDetailsActivity.this.goToFullArticle();
            }
        });
    }

    private void goToFullArticle() {
        String destinationUrl = mNewsArticle.getUrl();
        Uri uri = Uri.parse(destinationUrl);

        Intent startBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(startBrowser);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return false;
    }
}
