package com.lexu.mobileacademy3.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lexu.mobileacademy3.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class CustomService extends Service {

    private List<NewsArticle> newArticles = new ArrayList<NewsArticle>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startBackgroundProcessing();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startBackgroundProcessing() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 25; i++) {
                    newArticles.add(new NewsArticle("Service Article " + i, System.currentTimeMillis()));

                    try {
                        this.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent();
                intent.setAction(ServiceReceiver.ACTION_GENERATE_DATA);
                //TODO: set the list of articles as extras in the intent

                sendBroadcast(intent);
            }
        };

        new Thread(runnable).start();
    }
}
