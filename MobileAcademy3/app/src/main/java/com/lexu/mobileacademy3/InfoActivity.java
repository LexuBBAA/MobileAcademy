package com.lexu.mobileacademy3;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lexu.mobileacademy3.services.GetInstalledAppsService;
import com.lexu.mobileacademy3.services.ServiceReceiver;

import java.util.List;

public class InfoActivity extends AppCompatActivity implements ServiceUtils.ServiceCallback<String> {

    private static final String TAG = InfoActivity.class.getSimpleName();

    public static final String GET_INSTALLED_APPS_ACTION = "get_apps";

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private NavigationView mNavigationView = null;
    private RecyclerView recyclerView = null;

    private FrameLayout progressBarOverlay = null;
    private ProgressBar progressBar = null;

    private Adapter adapter = null;

    private ServiceReceiver mReceiver = null;

    @Override
    protected void onStart() {
        super.onStart();

        mReceiver = new ServiceReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GET_INSTALLED_APPS_ACTION);
        this.registerReceiver(mReceiver, intentFilter);

        Intent startService = new Intent(InfoActivity.this, GetInstalledAppsService.class);
        startService.setAction(GET_INSTALLED_APPS_ACTION);
        startService(startService);
    }

    @Override
    protected void onStop() {
        this.unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupDrawer();

        progressBarOverlay = (FrameLayout) findViewById(R.id.progress_bar_container);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressBarOverlay.getVisibility() != View.VISIBLE) {
                    return;
                }

                final int progress = progressBar.getProgress() + 1;
                if (progress < progressBar.getMax()) {
                    InfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    });
                } else {
                    InfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    });
                }
                handler.postDelayed(this, 200);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = null;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        adapter = new Adapter(this);
//        recyclerView.setAdapter(adapter);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.go_to_info:
                        toastOnUIThread("Already at Info");
                        mDrawerLayout.closeDrawer(Gravity.START);
                        return true;
                    case R.id.go_to_main:
                        onBackPressed();
                        return true;
                    case R.id.go_to_stored_news:
                    case R.id.go_to_settings:
                    case R.id.go_to_social:
                        toastOnUIThread("Under development");
                        return true;
                }

                return false;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(InfoActivity.this, mDrawerLayout, 0, 0) {
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
    public boolean onSupportNavigateUp() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        int i = 0;
        while(menu.hasVisibleItems()) {
            menu.getItem(i++).setVisible(false).setEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void toastOnUIThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InfoActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDataRetrieved(List<String> apps) {
        if (apps == null) {
            Log.e(TAG, "onDataRetrieved: null");
            return;
        }

        if (apps.size() == 0) {
            Log.e(TAG, "onDataRetrieved: size: 0");
        }

        for(String name: apps) {
            Log.d(TAG, "onDataRetrieved: " + name);
        }
        progressBarOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onErrorReceived(String msg) {
        progressBarOverlay.setVisibility(View.GONE);
        toastOnUIThread(msg);
    }
}
