package com.lexu.mobileacademy3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout = null;
    private NavigationView mNavigationView = null;

    @Override
    protected void onStart() {


        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupDrawer();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        List<NewsArticle> articles = new ArrayList<NewsArticle>();
        articles.add(new NewsArticle("News 1", System.currentTimeMillis()));
        articles.add(new NewsArticle("News 2", System.currentTimeMillis()));
        articles.add(new NewsArticle("News 3", System.currentTimeMillis()));
        articles.add(new NewsArticle("News 4", System.currentTimeMillis()));
        articles.add(new NewsArticle("News 5", System.currentTimeMillis()));

        Adapter adapter = new Adapter(this, articles);
        recyclerView.setAdapter(adapter);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_button:
                //TODO: create refresh function
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context context = null;
    private List<NewsArticle> data = null;

    Adapter(Context context, List<NewsArticle> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_news_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Adapter.ViewHolder holder, int position) {
        final NewsArticle article = this.data.get(position);

        holder.title.setText(article.getTitle());
        holder.date.setText(article.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) Adapter.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final NewsArticle oldArticle = article.cloneArticle();
                        final int oldPosition = holder.getAdapterPosition();

                        Adapter.this.data.remove(article);
                        Adapter.this.notifyItemRemoved(oldPosition);
                        Snackbar.make(holder.itemView, "Item duplicated: " + article.getTitle(), Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Adapter.this.data.add(oldPosition, oldArticle);
                                        Adapter.this.notifyItemRangeInserted(oldPosition, 1);
                                    }
                                }).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0: this.data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView title = null;
        AppCompatTextView date = null;

        ViewHolder(View itemView) {
            super(itemView);
            title = (AppCompatTextView) itemView.findViewById(R.id.article_title);
            date = (AppCompatTextView) itemView.findViewById(R.id.article_date);
        }
    }
}