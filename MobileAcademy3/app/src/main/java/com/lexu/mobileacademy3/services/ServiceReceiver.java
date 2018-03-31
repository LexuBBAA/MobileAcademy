package com.lexu.mobileacademy3.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lexu.mobileacademy3.NewsArticle;
import com.lexu.mobileacademy3.ServiceUtils;

import java.util.List;

import static com.lexu.mobileacademy3.services.CustomService.ARTICLES_EXTRA;
import static com.lexu.mobileacademy3.services.CustomService.STATUS_CODE;

public class ServiceReceiver extends BroadcastReceiver {

    public static final String ACTION_GENERATE_DATA = "ACTION_GENERATE_DATA";
    public static final String EXTRA_PAGE_NO = "PAGE_NO";

    private ServiceUtils.ServiceCallback mServiceCallback = null;

    public ServiceReceiver(ServiceUtils.ServiceCallback callback) {
        mServiceCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int code = intent.getIntExtra(STATUS_CODE, -1);
        if(code > 0) {
            ServiceUtils.ArticlesWrapper wrapper = (ServiceUtils.ArticlesWrapper) intent.getSerializableExtra(ARTICLES_EXTRA);
            if(wrapper != null && wrapper.articles != null) {
                List<NewsArticle> articles = wrapper.articles;
                mServiceCallback.onDataRetrieved(articles);
            }
        } else {
            String errorMessage = intent.getStringExtra(ARTICLES_EXTRA);
            if(errorMessage != null && !errorMessage.isEmpty()) {
                mServiceCallback.onErrorReceived(errorMessage);
            }
        }
    }
}
