package com.lexu.mobileacademy3.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lexu.mobileacademy3.NewsArticle;
import com.lexu.mobileacademy3.ServiceUtils;

import java.util.ArrayList;

public class ServiceReceiver extends BroadcastReceiver {

    public static final String ACTION_GENERATE_DATA = BroadcastReceiver.class.getCanonicalName();

    private ServiceUtils.ServiceCallback mServiceCallback = null;

    public ServiceReceiver(ServiceUtils.ServiceCallback callback) {
        mServiceCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: parse intent data into List<NewsArticle>
        mServiceCallback.onServiceCompleted(new ArrayList<NewsArticle>());
    }
}
