package com.lexu.mobileacademy3.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lexu.mobileacademy3.NewsArticle;
import com.lexu.mobileacademy3.ServiceUtils;

import java.util.List;

import static com.lexu.mobileacademy3.services.CustomService.ARTICLES_EXTRA;
import static com.lexu.mobileacademy3.services.CustomService.STATUS_CODE;

public class ServiceReceiver extends BroadcastReceiver {

    private static final String TAG = ServiceReceiver.class.getSimpleName();

    public static final String ACTION_GENERATE_DATA = "ACTION_GENERATE_DATA";
    public static final String EXTRA_PAGE_NO = "PAGE_NO";

    private ServiceUtils.ServiceCallback mServiceCallback = null;

    public ServiceReceiver(ServiceUtils.ServiceCallback callback) {
        mServiceCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int code = intent.getIntExtra(STATUS_CODE, -1);
        Log.d(TAG, "onReceive: code: " + code);

        switch (code) {
            case 1:
                processNews(intent);
                break;
            case 2:
                processApps(intent);
                break;
            default:
                processUnknown(intent);
        }
    }

    private void processApps(Intent intent) {
        ServiceUtils.Wrapper<String> wrapper = (ServiceUtils.Wrapper<String>) intent.getSerializableExtra(ARTICLES_EXTRA);
        if(wrapper != null && wrapper.data != null) {
            List<String> data = wrapper.data;
            mServiceCallback.onDataRetrieved(data);
        }
    }

    private void processNews(Intent intent) {
        ServiceUtils.Wrapper<NewsArticle> wrapper = (ServiceUtils.Wrapper<NewsArticle>) intent.getSerializableExtra(ARTICLES_EXTRA);
        if(wrapper != null && wrapper.data != null) {
            List<NewsArticle> articles = wrapper.data;
            mServiceCallback.onDataRetrieved(articles);
        }
    }

    private void processUnknown(Intent intent) {
        String errorMessage = intent.getStringExtra(ARTICLES_EXTRA);
        if(errorMessage != null && !errorMessage.isEmpty()) {
            mServiceCallback.onErrorReceived(errorMessage);
        }
    }
}
