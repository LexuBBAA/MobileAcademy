package com.lexu.mobileacademy3.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lexu.mobileacademy3.Manager;
import com.lexu.mobileacademy3.NewsArticle;
import com.lexu.mobileacademy3.ServiceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.lexu.mobileacademy3.services.ServiceReceiver.ACTION_GENERATE_DATA;

public class CustomService extends Service {

    private static final String TAG = CustomService.class.getSimpleName();

    public static final String ARTICLES_EXTRA = "articles";
    public static final String STATUS_CODE = "code";
    private List<NewsArticle> newArticles = new ArrayList<NewsArticle>();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int page = intent.getIntExtra(ServiceReceiver.EXTRA_PAGE_NO, 0);
        startBackgroundProcessing(page);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startBackgroundProcessing(final int page) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Manager manager = new Manager();
                manager.requestNews(page, new ServiceUtils.OnNetworkUpdatesListener() {
                    @Override
                    public void onSuccess(@NonNull ResponseData responseData) {
                        switch (responseData.getCode()) {
                            case 200: {
                                try {
                                    CustomService.this.processResponse(responseData);
                                } catch (JSONException | ParseException e) {
                                    e.printStackTrace();
                                    CustomService.this.showMessageOnUI(e.getLocalizedMessage());
                                }
                                break;
                            }

                            default: {
                                Log.e(TAG, "onSuccess: " + responseData.getCode() +
                                        " " + responseData.getMessage());
                                CustomService.this.showMessageOnUI(responseData.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
                        e.printStackTrace();
                        CustomService.this.showMessageOnUI(e.getLocalizedMessage());
                    }
                });
            }
        };

        new Thread(runnable).start();
    }

    private void processResponse(ServiceUtils.OnNetworkUpdatesListener.ResponseData responseData) throws JSONException, ParseException {
        String json = (String) responseData.getData();
        CustomService.this.newArticles = Parser.parseJsonNews(json);
        CustomService.this.sendUpdateNotification();
    }

    private void showMessageOnUI(String message) {
        Intent intent = new Intent();
        intent.setAction(ACTION_GENERATE_DATA);

        intent.putExtra(STATUS_CODE, -1);
        intent.putExtra(ARTICLES_EXTRA, message);
        sendBroadcast(intent);

        CustomService.this.stopSelf();
    }

    private void sendUpdateNotification() {
        Intent intent = new Intent();
        intent.setAction(ACTION_GENERATE_DATA);

        ServiceUtils.Wrapper<NewsArticle> wrapper = new ServiceUtils.Wrapper<NewsArticle>();
        wrapper.data = newArticles;

        intent.putExtra(STATUS_CODE, 1);
        intent.putExtra(ARTICLES_EXTRA, wrapper);
        sendBroadcast(intent);

        CustomService.this.stopSelf();
    }
}

final class Parser {
    private static final String TAG = Parser.class.getSimpleName();
    public static final String ARTICLES_KEY = "articles";

    static List<NewsArticle> parseJsonNews(String json) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray(ARTICLES_KEY);

        List<NewsArticle> articles = new ArrayList<NewsArticle>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonArticle = jsonArray.getJSONObject(i);

            Log.e(TAG, "parseJsonNews: " + jsonArticle);
            articles.add(NewsArticle.parseFromJson(jsonArticle));
        }

        return articles;
    }
}