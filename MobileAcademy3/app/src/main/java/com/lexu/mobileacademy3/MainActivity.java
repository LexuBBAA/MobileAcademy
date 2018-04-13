package com.lexu.mobileacademy3;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lexu.mobileacademy3.services.CustomService;
import com.lexu.mobileacademy3.services.ServiceReceiver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceUtils.ServiceCallback, OnNavigationOccurredListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final String ARTICLE_DATA = "ARTICLE";
    private ServiceReceiver receiver = null;

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private NavigationView mNavigationView = null;
    private RecyclerView recyclerView = null;

    private FrameLayout progressBarOverlay = null;
    private ProgressBar progressBar = null;

    private Adapter adapter = null;

    private int mPage = -1;
    private List<NewsArticle> mNewArticles = new ArrayList<NewsArticle>();

    @Override
    protected void onStart() {
        this.receiver = new ServiceReceiver(MainActivity.this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServiceReceiver.ACTION_GENERATE_DATA);
        this.registerReceiver(this.receiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        this.unregisterReceiver(this.receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupDrawer();

        setupUI();

        if(savedInstanceState != null && savedInstanceState.containsKey(CustomService.ARTICLES_EXTRA)) {
            ServiceUtils.ArticlesWrapper wrapper = (ServiceUtils.ArticlesWrapper) savedInstanceState.getSerializable(CustomService.ARTICLES_EXTRA);
            if(wrapper != null) {
                this.adapter.setItems(wrapper.articles);
            }
            progressBarOverlay.setVisibility(View.GONE);
        } else {
            startCustomService();
        }
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.go_to_main:
                        toastOnUIThread("Already at Home");
                        mDrawerLayout.closeDrawer(Gravity.START);
                        return true;
                    case R.id.go_to_stored_news:
                    case R.id.go_to_info:
                    case R.id.go_to_settings:
                    case R.id.go_to_social:

                        toastOnUIThread("Under development");
                        return true;
                }

                return false;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, 0, 0) {
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

    private void setupUI() {
        progressBarOverlay = (FrameLayout) findViewById(R.id.progress_bar_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = null;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        }
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void startCustomService() {
        progressBarOverlay.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressBarOverlay.getVisibility() != View.VISIBLE) {
                    return;
                }

                final int progress = progressBar.getProgress() + 1;
                if (progress < progressBar.getMax()) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    });
                }
                handler.postDelayed(this, 200);
            }
        });

        Intent newIntent = new Intent(MainActivity.this, CustomService.class);
        newIntent.setAction(ServiceReceiver.ACTION_GENERATE_DATA);
        newIntent.putExtra(ServiceReceiver.EXTRA_PAGE_NO, ++mPage);
        startService(newIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ServiceUtils.ArticlesWrapper wrapper = new ServiceUtils.ArticlesWrapper();
        wrapper.articles = this.adapter.getItems();

        outState.putSerializable(CustomService.ARTICLES_EXTRA, wrapper);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getResources().getString(R.string.search_icon_text));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MainActivity.this.adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MainActivity.this.adapter.getFilter().filter("");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
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
                MainActivity.this.refreshUI();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toastOnUIThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDataRetrieved(final List<NewsArticle> articles) {
        if (MainActivity.this.mNewArticles.isEmpty()) {
            mNewArticles.addAll(articles);
            MainActivity.this.refreshUI();
            return;
        } else {
            mNewArticles.addAll(articles);
        }
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mNavigationView, "New articles received", Snackbar.LENGTH_LONG)
                        .setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.refreshUI();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onErrorReceived(final String msg) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.progressBarOverlay.setVisibility(View.GONE);
                Log.e(TAG, "run: " + msg);
                Snackbar.make(mNavigationView, "An error occurred.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainActivity.this.startCustomService();
                            }
                        }).show();
            }
        });
    }

    private void refreshUI() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mNewArticles.size() != 0) {
                    MainActivity.this.progressBarOverlay.setVisibility(View.GONE);
                    MainActivity.this.adapter.addItems(mNewArticles);
                    mNewArticles.clear();
                    MainActivity.this.recyclerView.smoothScrollToPosition(0);
                } else {
                    MainActivity.this.startCustomService();
                }
            }
        });
    }

    @Override
    public void navigate(final NewsArticle article) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent navigate = new Intent(MainActivity.this, ArticleDetailsActivity.class);
                navigate.putExtra(ARTICLE_DATA, article);
                startActivity(navigate);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }
}

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {

    private Context context = null;
    private OnNavigationOccurredListener callback = null;
    private List<NewsArticle> data = null;
    private List<NewsArticle> displayedData = null;

    Adapter(Context context) {
        this.context = context;
        this.data = new ArrayList<NewsArticle>();
        this.displayedData = new ArrayList<NewsArticle>();
        if(context instanceof OnNavigationOccurredListener) {
            this.callback = (OnNavigationOccurredListener) context;
        }
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_news_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Adapter.ViewHolder holder, int position) {
        final NewsArticle article = this.displayedData.get(position);

        if(!article.getImageSrc().isEmpty()) {
            Picasso.get()
                    .load(article.getImageSrc())
                    .placeholder(R.drawable.ic_image_gray_24dp)
                    .error(R.drawable.ic_broken_image_gray_24dp)
                    .into(holder.image);
        }

        holder.title.setText(article.getTitle() != null ? article.getTitle() : article.getDescription());
        holder.date.setText(article.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) Adapter.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(Adapter.this.callback != null) {
                            Adapter.this.callback.navigate(article);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.displayedData == null ? 0 : this.displayedData.size();
    }

    public void addItems(List<NewsArticle> articles) {
        for (NewsArticle article : articles) {
            this.data.add(0, article);
            this.displayedData.add(0, article);
            this.notifyItemInserted(0);
        }
    }

    List<NewsArticle> getItems() {
        return this.data;
    }

    void setItems(List<NewsArticle> items) {
        this.data = items;
        this.displayedData = items;
        this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<NewsArticle> filteredList = new ArrayList<NewsArticle>();
                for(NewsArticle article: Adapter.this.data) {
                    if(article.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(article);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Adapter.this.displayedData = (List<NewsArticle>) results.values;
                Adapter.this.notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView image = null;
        AppCompatTextView title = null;
        AppCompatTextView date = null;

        ViewHolder(View itemView) {
            super(itemView);
            image = (AppCompatImageView) itemView.findViewById(R.id.article_image);
            title = (AppCompatTextView) itemView.findViewById(R.id.article_title);
            date = (AppCompatTextView) itemView.findViewById(R.id.article_date);
        }
    }
}

interface OnNavigationOccurredListener {
    void navigate(NewsArticle article);
}